package com.ecommerce.dell.mcommerce.activity.payment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.ecommerce.dell.mcommerce.R;
import com.ecommerce.dell.mcommerce.jsonparser.JsonFunctions;
import com.ecommerce.dell.mcommerce.models.Global;
import com.ecommerce.dell.mcommerce.utility.UtilityClass;
import com.oppwa.mobile.connect.exception.PaymentError;
import com.oppwa.mobile.connect.exception.PaymentException;
import com.oppwa.mobile.connect.payment.CheckoutInfo;
import com.oppwa.mobile.connect.payment.PaymentParams;
import com.oppwa.mobile.connect.payment.PaymentParamsBrand;
import com.oppwa.mobile.connect.payment.card.CardPaymentParams;
import com.oppwa.mobile.connect.provider.Connect;
import com.oppwa.mobile.connect.provider.ITransactionListener;
import com.oppwa.mobile.connect.provider.Transaction;
import com.oppwa.mobile.connect.service.ConnectService;
import com.oppwa.mobile.connect.service.IProviderBinder;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CheckoutCreditCardActivity extends ActionBarActivity implements ITransactionListener {

    public SVProgressHUD mSVProgressHUD;
    public Handler handler = new Handler();
    Spinner spinner, monthSpinner, yearSpinner;
    String method;
    TextView errortext;
    float total;
    String orderId, checkoutId;
    String jsonException, email, ids, qtys;
    String[] data;
    int items;

    RelativeLayout relativeLayout_alert;
    TextView text_alert;

    private EditText dateView;
    private String year1 = "2017", month1 = "01";

    public Boolean toggleIsOn;
    float amount1 = 0.0f;

    private void setStatusText(final String string) {
        runOnUiThread(new Runnable() {
            public void run() {
                mSVProgressHUD.dismiss();
                findViewById(R.id.button_purchase).setClickable(true);
                Toast.makeText(CheckoutCreditCardActivity.this, string, Toast.LENGTH_LONG).show();
            }
        });
    }

    private IProviderBinder binder;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (IProviderBinder) service;
            /* we have a connection to the service */
            try {
                binder.initializeProvider(Connect.ProviderMode.LIVE);
                binder.addTransactionListener(CheckoutCreditCardActivity.this);
            } catch (PaymentException ee) {
	            /* error occurred */
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, ConnectService.class);

        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        unbindService(serviceConnection);
        stopService(new Intent(this, ConnectService.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gate2play);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_credit_card));

        mSVProgressHUD = new SVProgressHUD(this);

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

        toggleIsOn = false;

        ids = "";
        qtys = "";
        email = "";
        orderId = "";
        data = new String[3];
        jsonException = "";

        SharedPreferences ss1 = getSharedPreferences("item", 0);
        int itemsCount = ss1.getInt("count", 0);

        SharedPreferences ss = getSharedPreferences("user", 0);
        email = ss.getString("email", "");

        for (int index = 1; index <= itemsCount; index++) {

            if (ss1.getInt("id" + index, 0) != 0) {
                if (index == itemsCount) {
                    ids += ss1.getInt("id" + index, 0) + "";
                } else {
                    ids += ss1.getInt("id" + index, 0) + ",";
                }
                int id = ss1.getInt("id" + index, 0);
                if (index == itemsCount) {
                    qtys += ss1.getString("amount" + id, "");
                } else {
                    qtys += ss1.getString("amount" + id, "") + ",";
                }
            }
        }

        data[0] = ids;
        data[1] = qtys;
        data[2] = email;

        items = itemsCount;

        for (int index = 1; index <= itemsCount; index++) {
            int id = ss1.getInt("id" + index, 0);
            if (id != 0) {
                String price = ss1.getString("price" + id, "");
                String amount = ss1.getString("amount" + id, "");

                total += Float.parseFloat(price) * Integer.parseInt(amount);
            }
        }

        dateView = (EditText) findViewById(R.id.editText3);
        showDate(year1, month1);

        errortext = (TextView) findViewById(R.id.error);
        spinner = (Spinner) findViewById(R.id.spinner);
        monthSpinner = (Spinner) findViewById(R.id.month_spinner);
        yearSpinner = (Spinner) findViewById(R.id.year_spinner);
        Log.e("null", getResources().getStringArray(R.array.gate2play_methods).length + "");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.gate2play_methods));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        errortext.setText(getResources().getString(R.string.gateError));
                        findViewById(R.id.button_purchase).setClickable(false);
                        findViewById(R.id.button_purchase).setActivated(false);
                        method = "NoPayment";
                        break;
                    case 1:
                        method = "VisaCard";
                        findViewById(R.id.button_purchase).setClickable(true);
                        break;
                    case 2:
                        method = "MasterCard";
                        findViewById(R.id.button_purchase).setClickable(true);
                        break;
                    default:
                        errortext.setText(getResources().getString(R.string.gateError));
                        findViewById(R.id.button_purchase).setClickable(false);
                        findViewById(R.id.button_purchase).setActivated(false);
                        method = "NoPayment";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        ArrayAdapter<String> monthDataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        monthDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthDataAdapter);
        monthSpinner.setDropDownWidth(200);
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                month1 = months[position];
                showDate(year1, months[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final String[] years = {"2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030",
                "2031", "2032", "2033", "2034", "2035", "2036", "2037", "2038", "2039", "2040"};
        ArrayAdapter<String> yearDataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearDataAdapter);
        yearSpinner.setDropDownWidth(200);
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year1 = years[position];
                showDate(years[position], month1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        findViewById(R.id.button_purchase).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);
                findViewById(R.id.button_purchase).setClickable(false);

                if (toggleIsOn) {
                    Handler handlerCheckout = new Handler();
                    handlerCheckout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (new checkoutCreditCard().execute(email, String.valueOf(total), Global.strShippingAddress, Global.strCouponCode).get()) {
                                    mSVProgressHUD.dismiss();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            onCheckoutCreditCard();
                                        }
                                    }, 0);
                                } else {
                                    if (new DeleteAllCart().execute(email).get()) {
                                        if (new addToCart().execute(data).get()) {
                                            if (new checkoutCreditCard().execute(email, String.valueOf(total), Global.strShippingAddress, Global.strCouponCode).get()) {
                                                mSVProgressHUD.dismiss();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        onCheckoutCreditCard();
                                                    }
                                                }, 0);
                                            } else {
                                                mSVProgressHUD.dismiss();
                                                findViewById(R.id.button_purchase).setClickable(true);
                                                text_alert.setText(getString(R.string.alert_ordering_failed));
                                                relativeLayout_alert.setVisibility(View.VISIBLE);
                                            }
                                        } else {
                                            mSVProgressHUD.dismiss();
                                            findViewById(R.id.button_purchase).setClickable(true);
                                            text_alert.setText(getString(R.string.no_products_message));
                                            relativeLayout_alert.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        mSVProgressHUD.dismiss();
                                        findViewById(R.id.button_purchase).setClickable(true);
                                        text_alert.setText(getString(R.string.alert_ordering_failed));
                                        relativeLayout_alert.setVisibility(View.VISIBLE);
                                    }
                                }
                            } catch (Exception ex) {
                                Log.d("checkoutError",ex.toString());
                                mSVProgressHUD.dismiss();
                                findViewById(R.id.button_purchase).setClickable(true);
                                text_alert.setText(getString(R.string.alert_ordering_failed));
                                relativeLayout_alert.setVisibility(View.VISIBLE);
                            }
                        }
                    }, 1000);
                } else {
                    // collect payment details
                    int shipmentcount = 0;
                    total = 0;
                    SharedPreferences ss1 = getSharedPreferences("item", 0);
                    int itemsCount = ss1.getInt("count", 0);

                    for (int index = 1; index <= itemsCount; index++) {
                        int id = ss1.getInt("id" + index, 0);
                        if (id != 0) {
                            shipmentcount++;
                            String imagePath = ss1.getString("image" + id, "");
                            String title = ss1.getString("title" + id, "");
                            String price = ss1.getString("price" + id, "");
                            String amount = ss1.getString("amount" + id, "");
                            total += Float.parseFloat(price) * Integer.parseInt(amount);  //Integer.parseInt(price) * Integer.parseInt(amount);
                        }
                        Log.e("total", total + "");
                        items = itemsCount;
                    }

                    final String holder = ((EditText) findViewById(R.id.editText1)).getText().toString();
                    final String cardnumber = ((EditText) findViewById(R.id.editText2)).getText().toString();
                    final String cvv = ((EditText) findViewById(R.id.editText4)).getText().toString();
                    // extract month
                    String month = "", year = "";
                    if (((EditText) findViewById(R.id.editText3)).getText().toString().length() == 7) {
                        month = ((EditText) findViewById(R.id.editText3)).getText().toString().substring(0, 2);
                        year = ((EditText) findViewById(R.id.editText3)).getText().toString().substring(3, 7);
                    }


                    amount1 = total;
                    Log.e("PaymentMethod", method);
                    if (amount1 < 200) {
                        amount1 += 20;
                    }

                    float discountCost = total * Global.discountPercent / 100;
                    amount1 = amount1 - discountCost;

                    Log.e("total",  amount1 + "");


                    mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);

                    Handler handlerCheckout = new Handler();
                    final String finalMonth = month;
                    final String finalYear = year;
                    handlerCheckout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (new requestCheckoutId().execute(String.format("%.02f", amount1)).get()) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            PaymentParams paymentParams = null;
                                            try {
                                                if (method.equals("VisaCard")) {
                                                    paymentParams = new CardPaymentParams(
                                                            checkoutId,
                                                            PaymentParamsBrand.VISA,
                                                            cardnumber,
                                                            holder,
                                                            finalMonth,
                                                            finalYear,
                                                            cvv
                                                    );
                                                } else if (method.equals("MasterCard")) {
                                                    paymentParams = new CardPaymentParams(
                                                            checkoutId,
                                                            PaymentParamsBrand.MASTER_CARD,
                                                            cardnumber,
                                                            holder,
                                                            finalMonth,
                                                            finalYear,
                                                            cvv
                                                    );
                                                }

                                            } catch (PaymentException e) {
                                                e.printStackTrace();
                                            }

                                            Transaction transaction = null;

                                            try {
                                                transaction = new Transaction(paymentParams);
                                                binder.submitTransaction(transaction);
                                            } catch (PaymentException ee) {
                                                /* error occurred */
//                                                setStatusText("Error: Could not contact Gateway!");
                                                mSVProgressHUD.dismiss();
                                                findViewById(R.id.button_purchase).setClickable(true);
                                                text_alert.setText(getString(R.string.alert_invalid_card_number));
                                                relativeLayout_alert.setVisibility(View.VISIBLE);
                                                ee.printStackTrace();
                                            }

                                        }
                                    });

                                } else {
                                    mSVProgressHUD.dismiss();
                                    findViewById(R.id.button_purchase).setClickable(true);
                                    text_alert.setText(getString(R.string.alert_ordering_failed));
                                    relativeLayout_alert.setVisibility(View.VISIBLE);
                                }

                            } catch (Exception ex) {
                                mSVProgressHUD.dismiss();
                                findViewById(R.id.button_purchase).setClickable(true);
                                text_alert.setText(getString(R.string.alert_ordering_failed));
                                relativeLayout_alert.setVisibility(View.VISIBLE);
                            }
                        }
                    }, 1000);

                }
            }

        });
    }

    private void showDate(String year, String month) {
        dateView.setText(new StringBuilder().append(month).append("/").append(year));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void paymentConfigRequestSucceeded(CheckoutInfo checkoutInfo) {
        Log.e("Tokenization", "Payment Config requested Successfully!");
    }

    @Override
    public void paymentConfigRequestFailed(PaymentError paymentError) {
//        setStatusText("Error contacting the gateway.");
        Log.e("Tokenization", paymentError.getErrorMessage());
        text_alert.setText(getString(R.string.alert_invalid_card_number));
        relativeLayout_alert.setVisibility(View.VISIBLE);
        findViewById(R.id.button_purchase).setClickable(true);
    }

    @Override
    public void transactionCompleted(Transaction transaction) {

//        Toast.makeText(CheckoutCreditCardActivity.this, "Charged Successfully! Please wait a minute...", Toast.LENGTH_LONG).show();
        findViewById(R.id.button_purchase).setClickable(false);
        toggleIsOn = true;

        try {
            if (new checkoutCreditCard().execute(email, String.valueOf(total), Global.strShippingAddress, Global.strCouponCode).get()) {
                mSVProgressHUD.dismiss();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onCheckoutCreditCard();
                    }
                }, 0);
            } else {
                if (new DeleteAllCart().execute(email).get()) {
                    if (new addToCart().execute(data).get()) {
                        if (new checkoutCreditCard().execute(email, String.valueOf(total), Global.strShippingAddress, Global.strCouponCode).get()) {
                            mSVProgressHUD.dismiss();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    onCheckoutCreditCard();
                                }
                            }, 0);
                        } else {
                            mSVProgressHUD.dismiss();
                            text_alert.setText(getString(R.string.alert_ordering_failed));
                            relativeLayout_alert.setVisibility(View.VISIBLE);
                            findViewById(R.id.button_purchase).setClickable(true);
                        }
                    } else {
                        mSVProgressHUD.dismiss();
                        text_alert.setText(getString(R.string.no_products_message));
                        relativeLayout_alert.setVisibility(View.VISIBLE);
                        findViewById(R.id.button_purchase).setClickable(true);
                    }
                } else {
                    mSVProgressHUD.dismiss();
                    text_alert.setText(getString(R.string.alert_ordering_failed));
                    relativeLayout_alert.setVisibility(View.VISIBLE);
                    findViewById(R.id.button_purchase).setClickable(true);
                }
            }
        } catch (Exception ex) {
            Log.d("checkoutError",ex.toString());
            mSVProgressHUD.dismiss();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onCheckoutCreditCard();
                }
            }, 0);
        }
    }

    @Override
    public void transactionFailed(Transaction transaction, PaymentError paymentError) {
//        setStatusText("Error : contacting the gateway.");
        Log.e("Tokenization", paymentError.getErrorMessage());
        text_alert.setText(getString(R.string.alert_transaction_failed));
        relativeLayout_alert.setVisibility(View.VISIBLE);
        findViewById(R.id.button_purchase).setClickable(true);
    }

    public void onCheckoutCreditCard() {

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
        edit.putString("order" + orderCount, String.valueOf(orderumber));
        edit.putString("total" + orderCount, String.format("%.02f", amountTotal));
        edit.putString("date" + orderCount, strDate);
        edit.putString("ship" + orderCount, strShipping);
        edit.putString("email" + orderCount, email);
        edit.putString("shipmentname" + orderCount, Global.strShipmentUsername);
        edit.putString("shipmentaddress" + orderCount, Global.strShipmentAddress);
        edit.putString("shipmenttelephone" + orderCount, Global.strShipmentTelephone);
        edit.putString("paymentmethod" + orderCount, method);
        edit.putString("discount" + orderCount, Global.strCouponCode);
        edit.putString("discountPercent" + orderCount, String.valueOf(Global.discountPercent));
        edit.putInt("count", orderCount);
        edit.commit();
        ss1.edit().clear().commit();

        Intent intent = new Intent(CheckoutCreditCardActivity.this, PaymentConfirmationActivity.class);
        intent.putExtra("orderID", String.valueOf(orderumber));
        intent.putExtra("orderDate", strDate);
        intent.putExtra("shipping", strShipping);
        intent.putExtra("totalCost", String.format("%.02f", amountTotal));
        intent.putExtra("email", email);
        intent.putExtra("shipmentUsername", Global.strShipmentUsername);
        intent.putExtra("shipmentAddress", Global.strShipmentAddress);
        intent.putExtra("shipmentTelephone", Global.strShipmentTelephone);
        intent.putExtra("paymentMethod", method);
        intent.putExtra("discount", Global.strCouponCode);
        intent.putExtra("discountPercent", String.valueOf(Global.discountPercent));
        startActivity(intent);
        finish();
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

    public class checkoutCreditCard extends AsyncTask<String, Void, Boolean> {

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
                result = jsonFunctions.checkout_gate2play(params[0], params[1], params[2], params[3]);
            } catch (Exception e) {
                Log.d("ResultJsonParsing", e.toString());
            }

            if (result == null) {
                error = "null json object";
                return false;
            }
            try {
                if (result.getInt("success") == 1) {
                    orderId = result.getString("orderId");
                    return true;
                } else {
                    return false;
                }
            } catch (Exception ex) {
                error = ex.toString();
                Log.d("checkout", error);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

    public class requestCheckoutId extends AsyncTask<String, Void, Boolean> {

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
                result = jsonFunctions.creditcardCheckout(params[0]);
            } catch (Exception e) {
                Log.d("ResultJsonParsing", e.toString());
            }

            if (result == null) {
                error = "null json object";
                return false;
            }
            try {

                JSONObject code = new JSONObject();
                code = result.getJSONObject("result");

                if (code.getString("code").equals("000.200.100")) {
                    checkoutId = result.getString("id");
                    return true;
                } else {
                    return false;
                }

            } catch (Exception ex) {
                error = ex.toString();
                Log.d("checkout", error);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }
}
