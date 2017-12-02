package com.ecommerce.dell.mcommerce.activity.payment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.ecommerce.dell.mcommerce.R;
import com.ecommerce.dell.mcommerce.jsonparser.JsonFunctions;
import com.ecommerce.dell.mcommerce.models.Global;
import com.ecommerce.dell.mcommerce.utility.UtilityClass;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class CheckoutBankTransferActivity extends ActionBarActivity {

    public SVProgressHUD mSVProgressHUD;
    public Handler handler = new Handler();
    String ids , qtys ,email ;
    String jsonException;
    String orderId;
    String[] data;
    float total = 0.0f;
    int items;

    RelativeLayout relativeLayout_alert;
    TextView text_alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_transfer);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.bankTransfer_Title));

        mSVProgressHUD = new SVProgressHUD(this);

        Button button = (Button) findViewById(R.id.buy);

        relativeLayout_alert = (RelativeLayout)findViewById(R.id.relativeLayout_alert);
        text_alert = (TextView)findViewById(R.id.textView_alert_content);
        relativeLayout_alert.setVisibility(View.INVISIBLE);

        Button btn_alert_ok = (Button)findViewById(R.id.button_alert_ok);
        btn_alert_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout_alert.setVisibility(View.INVISIBLE);
            }
        });

        ids = "";
        qtys ="";
        email = "";
        orderId = "";
        data = new String[3];
        jsonException="";

        SharedPreferences ss1 = getSharedPreferences("item", 0);
        int itemsCount = ss1.getInt("count", 0) ;

        SharedPreferences ss = getSharedPreferences("user", 0);
        email = ss.getString("email", "") ;

        for (int index = 1 ; index <= itemsCount ;index++ ) {

            if (ss1.getInt("id" + index, 0) !=0 ) {
                if (index == itemsCount) {
                    ids += ss1.getInt("id" + index, 0)+"";
                } else {
                    ids += ss1.getInt("id" + index, 0)+ ",";
                }
                int id = ss1.getInt("id" + index, 0);

                if (index == itemsCount) {
                    qtys += ss1.getString("amount"+id ,"" );
                } else {
                    qtys += ss1.getString("amount"+id ,"" ) + ",";
                }
            }
        }

        data[0] = ids;
        data[1] = qtys;
        data[2] = email;

        int items = itemsCount;

        for (int index = 1; index <= itemsCount; index++) {
            int id = ss1.getInt("id" + index, 0);
            if (id != 0) {
                String price = ss1.getString("price" + id, "");
                String amount = ss1.getString("amount" + id, "");

                total += Float.parseFloat(price) * Integer.parseInt(amount);
            }
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);
                Handler handlerCheckout = new Handler();
                handlerCheckout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (new CheckOutBank().execute(email, String.valueOf(total), Global.strShippingAddress, Global.strCouponCode).get()) {
                                mSVProgressHUD.dismiss();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        onCheckOutBank();
                                    }
                                }, 0);
                            } else {
                                if (new DeleteAllCart().execute(email).get()) {
                                    if (new addToCart().execute(data).get()) {
                                        if (new CheckOutBank().execute(email, String.valueOf(total), Global.strShippingAddress, Global.strCouponCode).get()) {
                                            mSVProgressHUD.dismiss();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    onCheckOutBank();
                                                }
                                            }, 0);
                                        } else {
                                            mSVProgressHUD.dismiss();
                                            text_alert.setText(getString(R.string.alert_ordering_failed));
                                            relativeLayout_alert.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        mSVProgressHUD.dismiss();
                                        text_alert.setText(getString(R.string.no_products_message));
                                        relativeLayout_alert.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    mSVProgressHUD.dismiss();
                                    text_alert.setText(getString(R.string.alert_ordering_failed));
                                    relativeLayout_alert.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (Exception ex) {
                            mSVProgressHUD.dismiss();
                            text_alert.setText(getString(R.string.alert_ordering_failed));
                            relativeLayout_alert.setVisibility(View.VISIBLE);
                        }
                    }
                }, 1000);
            }
        });
    }

    public void onCheckOutBank() {

        long orderumber = Integer.parseInt(orderId);

        SharedPreferences ss1 = getSharedPreferences("item", 0);
        int itemsCount = ss1.getInt("count", 0);

        for (int index = 1; index <= itemsCount; index++) {
            int id = ss1.getInt("id" + index, 0);
            if (id != 0) {
                String imagePath = ss1.getString("image" + id, "");
                String title = ss1.getString("title" + id, "");
                String price = ss1.getString("price" + id, "");
                String amount = ss1.getString("amount" + id, "");

                if (imagePath != "") {
                    UtilityClass.insertOrderDBData(String.valueOf(orderumber), email, title, amount, "sku", price, imagePath);
                }
            }
        }

        SharedPreferences s = getSharedPreferences("order", 0);
        SharedPreferences.Editor edit = s.edit();
        edit.putInt("count", items);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
        String strDate = sdf.format(c.getTime());

        String strShipping = "Free";
        if (total < 200) {
            total += 20;
            strShipping = "20.00";
        }

        float amountTotal = total;
        float discountCost = total * Global.discountPercent / 100;
        amountTotal = amountTotal - discountCost;

        int orderCount = s.getInt("count" , 0) + 1;
        edit.putString("order"+orderCount, String.valueOf(orderumber));
        edit.putString("total" + orderCount, String.format("%.02f", amountTotal));
        edit.putString("date" + orderCount, strDate);
        edit.putString("ship" + orderCount, strShipping);
        edit.putString("email" + orderCount, email);
        edit.putString("shipmentname" + orderCount, Global.strShipmentUsername);
        edit.putString("shipmentaddress" + orderCount, Global.strShipmentAddress);
        edit.putString("shipmenttelephone" + orderCount, Global.strShipmentTelephone);
        edit.putString("paymentmethod" + orderCount, Global.PAYMENT_BANK);
        edit.putString("discount" + orderCount, Global.strCouponCode);
        edit.putString("discountPercent" + orderCount, String.valueOf(Global.discountPercent));
        edit.putInt("count", orderCount);
        edit.commit();
        ss1.edit().clear().commit();

        Intent intent = new Intent(CheckoutBankTransferActivity.this, PaymentConfirmationActivity.class);
        intent.putExtra("orderID", String.valueOf(orderumber));
        intent.putExtra("orderDate", strDate);
        intent.putExtra("shipping", strShipping);
        intent.putExtra("totalCost", String.format("%.02f", amountTotal));
        intent.putExtra("email", email);
        intent.putExtra("shipmentUsername", Global.strShipmentUsername);
        intent.putExtra("shipmentAddress", Global.strShipmentAddress);
        intent.putExtra("shipmentTelephone", Global.strShipmentTelephone);
        intent.putExtra("paymentMethod", Global.PAYMENT_BANK);
        intent.putExtra("discount", Global.strCouponCode);
        intent.putExtra("discountPercent", String.valueOf(Global.discountPercent));
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class DeleteAllCart extends AsyncTask<String, Void, Boolean> {

        JSONObject result = new JSONObject();
        String error = "";
        JsonFunctions jsonFunctions = new JsonFunctions();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                result = jsonFunctions.deleteCart(params[0]);
            } catch (Exception e) {
                Log.d("ResultJsonParsing", e.toString());
            }

            if (result == null) {
                error = "null json object";
                return false;
            }
            try {
                if (result.getInt("success") == 1) {
                    return true;
                }
            } catch (Exception ex) {

            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

    public class addToCart extends AsyncTask<String[], Void, Boolean> {

        JSONObject result = new JSONObject();
        String error = "";
        JsonFunctions jsonFunctions = new JsonFunctions();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String[]... params) {

            try {
                result = jsonFunctions.addToCart(params[0][0], params[0][1], params[0][2]);
            } catch (Exception e) {
                Log.d("ResultJsonParsing", e.toString());
            }

            if (result == null) {
                error = "null json object";
                return false;
            }
            try {
                if (result.getInt("success") == 1) {
//                    total = result.getInt("total");
                    return true;
                }
            } catch (Exception ex) {
                error = ex.toString();
                Log.d("addtocart", error);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

    public class CheckOutBank extends AsyncTask<String , Void , Boolean> {

        JSONObject result = new JSONObject();
        String error = "";
        JsonFunctions jsonFunctions = new JsonFunctions();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                result = jsonFunctions.checkout_BankTransfer(params[0], params[1], params[2], params[3]);
            } catch (Exception e) {
                Log.d("ResultJsonParsing", e.toString());
            }

            if (result == null) {
                error = "null json object";
                return false;
            }
            try{
                if(result.getInt("success") == 1) {
                    orderId = result.getString("orderId");
                    return true;
                } else {
                    return false;
                }
            } catch (Exception ex){
                error = ex.toString();
                Log.d("checkout" , error);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }
}
