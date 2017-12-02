package com.ecommerce.dell.mcommerce.activity.payment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.ecommerce.dell.mcommerce.R;
import com.ecommerce.dell.mcommerce.jsonparser.JsonFunctions;
import com.ecommerce.dell.mcommerce.models.Global;
import com.ecommerce.dell.mcommerce.utility.UtilityClass;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class CheckoutActivity extends ActionBarActivity {

    public SVProgressHUD mSVProgressHUD;
    public Handler handler = new Handler();
    LinearLayout item;
    float total = 0;
    int id = 1;
    float sub = 0;
    TextView username, address, telephone;
    Button checkout;
    String ids, qtys, email;
    String jsonException;
    String orderId;
    String[] data;
    int items;

    Spinner shippingMethod;
    String[] payment_methods;

    TextView subtotal, shipping, grandtotal;
    String paymentMethod;

    Button btnCouponApply, btnCouponCancel;
    EditText edit_couponCode;
    TextView discount, discountTotal;

    String strCouponName = "";
    float couponPercent = 0.0f;

    RelativeLayout relativeLayout_alert;
    TextView text_alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_checkout);

        mSVProgressHUD = new SVProgressHUD(this);

        ids = "";
        qtys = "";
        email = "";
        orderId = "";
        data = new String[3];
        jsonException = "";

        shippingMethod = (Spinner) findViewById(R.id.paymentMethod);

        payment_methods = getResources().getStringArray(R.array.methods);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, payment_methods);
        shippingMethod.setAdapter(spinnerAdapter);
        shippingMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        paymentMethod = "CashOnDeliver";
                        break;
                    case 1:
                        paymentMethod = "BanskTransfer";
                        break;
                    case 2:
                        paymentMethod = "Paypal";
                        break;
                    case 3:
                        paymentMethod = "CreditCard";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

        item = (LinearLayout) findViewById(R.id.item);
        username = (TextView) findViewById(R.id.user);
        address = (TextView) findViewById(R.id.address);
        telephone = (TextView) findViewById(R.id.telephone);
        subtotal = (TextView) findViewById(R.id.subtotal);
        grandtotal = (TextView) findViewById(R.id.grandtotal);
        checkout = (Button) findViewById(R.id.checkout);
        shipping = (TextView) findViewById(R.id.shipping);
        discount = (TextView) findViewById(R.id.discounttext);
        discountTotal = (TextView) findViewById(R.id.discounttotal);

        btnCouponApply = (Button)findViewById(R.id.button_coupon_apply);
        btnCouponCancel = (Button)findViewById(R.id.button_coupon_cancel);
        edit_couponCode = (EditText) findViewById(R.id.editText_couponCode);

        btnCouponApply.setVisibility(View.VISIBLE);
        btnCouponCancel.setVisibility(View.INVISIBLE);

        strCouponName = "";
        couponPercent = 0.0f;

        Global.strCouponCode = "";
        Global.discountPercent = 0.0f;

        discount.setText(getString(R.string.str_discount));
        discountTotal.setText("- 0.00" + " " + getResources().getString(R.string.main_activity_currency));

        btnCouponApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String couponCodeString = edit_couponCode.getText().toString();

                if (couponCodeString.equals("")) {
                    new AlertView(getString(R.string.alert),
                            getString(R.string.alert_input_coupon_code), null,
                            new String[]{getString(R.string.ok)}, null, CheckoutActivity.this,
                            AlertView.Style.Alert, null).show();
                } else {

                    mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);

                    Handler handlerCheckout = new Handler();
                    handlerCheckout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (new onApplyCouponCode().execute(couponCodeString).get()) {
                                    mSVProgressHUD.dismiss();
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            onShowDiscountCost();
                                        }
                                    });

                                } else {
                                    mSVProgressHUD.dismiss();
                                    text_alert.setText(getString(R.string.alert_network_connection_failed));
                                    relativeLayout_alert.setVisibility(View.VISIBLE);
                                }

                            } catch (Exception ex) {
                                mSVProgressHUD.dismiss();
                                text_alert.setText(getString(R.string.alert_network_connection_failed));
                                relativeLayout_alert.setVisibility(View.VISIBLE);
                            }
                        }
                    }, 1000);

                }
            }
        });

        btnCouponCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edit_couponCode.setText("");
                btnCouponCancel.setVisibility(View.INVISIBLE);
                btnCouponApply.setEnabled(true);
                edit_couponCode.setEnabled(true);

                Global.strCouponCode = "";
                Global.discountPercent = 0.0f;

                discount.setText(getString(R.string.str_discount));
                discountTotal.setText("- 0.00" + " " + getResources().getString(R.string.main_activity_currency));

                if (sub > 200.0) {
                    grandtotal.setText(String.format("%.02f", sub) + " " + getString(R.string.main_activity_currency));
                } else {
                    grandtotal.setText(String.format("%.02f", sub + 20) + " " + getString(R.string.main_activity_currency));
                }

                new AlertView(getString(R.string.alert),
                        getString(R.string.alert_cancel_coupon_code), null,
                        new String[]{getString(R.string.ok)}, null, CheckoutActivity.this,
                        AlertView.Style.Alert, null).show();
            }
        });

        Global.strShipmentUsername = getIntent().getStringExtra("name");
        Global.strShipmentAddress = getIntent().getStringExtra("address");
        Global.strShipmentTelephone = getIntent().getStringExtra("telephone");

        username.setText(Global.strShipmentUsername);
        address.setText(Global.strShipmentAddress);
        telephone.setText(Global.strShipmentTelephone);

        Global.strShippingAddress = Global.strShipmentUsername + "\n" + Global.strShipmentAddress + "\nT:" + Global.strShipmentTelephone;

        int shipmentcount = 0;

        try {

            mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);

            if (new DeleteAllCart().execute(email).get()) {
                if (new addToCart().execute(data).get()) {

                    mSVProgressHUD.dismiss();
                    for (int index = 1; index <= itemsCount; index++) {

                        int id = ss1.getInt("id" + index, 0);

                        if (id != 0) {
                            shipmentcount++;
                            String imagePath = ss1.getString("image" + id, "");
                            String title = ss1.getString("title" + id, "");
                            String price = ss1.getString("price" + id, "");
                            String amount = ss1.getString("amount" + id, "");

                            sub += Float.parseFloat(price) * Integer.parseInt(amount);

                            total += Float.parseFloat(price) * Integer.parseInt(amount);  //Integer.parseInt(price) * Integer.parseInt(amount);
                            if (imagePath != "") {
                                addItem(imagePath, title, price, amount, id, shipmentcount);
                            }
                        }
                    }

                    Log.d("sub", String.valueOf(sub));

                    if (sub > 200.0) {
                        shipping.setText("0.00" + " " + getResources().getString(R.string.main_activity_currency));
                        subtotal.setText(String.format("%.02f", sub) + " " + getString(R.string.main_activity_currency));
                        grandtotal.setText(String.format("%.02f", sub) + " " + getString(R.string.main_activity_currency));
                    } else {
                        shipping.setText("20.00" + " " + getResources().getString(R.string.main_activity_currency));
                        subtotal.setText(String.format("%.02f", sub) + " " + getString(R.string.main_activity_currency));
                        grandtotal.setText(String.format("%.02f", sub + 20) + " " + getString(R.string.main_activity_currency));
                    }

                } else {
                    mSVProgressHUD.dismiss();
                    text_alert.setText(getString(R.string.no_products_message));
                    relativeLayout_alert.setVisibility(View.VISIBLE);
                }
            } else {
                mSVProgressHUD.dismiss();
                text_alert.setText(getString(R.string.alert_network_connection_failed));
                relativeLayout_alert.setVisibility(View.VISIBLE);
            }

        } catch (Exception ex) {

        }

        items = itemsCount;
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CheckOUTCon", "what");

                if (paymentMethod.equals("CashOnDeliver")) {

                    mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);

                    Handler handlerCheckout = new Handler();
                    handlerCheckout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (new checkout().execute(email, String.valueOf(total), Global.strShippingAddress, Global.strCouponCode).get()) {
                                    mSVProgressHUD.dismiss();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            onCashCheckOut();
                                        }
                                    }, 0);
                                } else {
                                    if (new DeleteAllCart().execute(email).get()) {
                                        if (new addToCart().execute(data).get()) {
                                            if (new checkout().execute(email,String.valueOf(total), Global.strShippingAddress, Global.strCouponCode).get()) {
                                                mSVProgressHUD.dismiss();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        onCashCheckOut();
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

                } else if (paymentMethod.equals("Paypal")) {
                    Intent i = new Intent(CheckoutActivity.this, CheckoutPaypalActivity.class);
                    startActivity(i);
                    finish();
                } else if (paymentMethod.equals("CreditCard")) {
                    Intent i = new Intent(CheckoutActivity.this, CheckoutCreditCardActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(CheckoutActivity.this, CheckoutBankTransferActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });

    }

    public void onShowDiscountCost() {

        if (strCouponName.equals("") || strCouponName.equals("invalid_code")) {

            text_alert.setText(getString(R.string.alert_invalid_coupon_code));
            relativeLayout_alert.setVisibility(View.VISIBLE);

            btnCouponCancel.setVisibility(View.INVISIBLE);
            btnCouponApply.setEnabled(true);
            edit_couponCode.setEnabled(true);

            Global.strCouponCode = "";
            Global.discountPercent = 0.0f;

        } else {

            text_alert.setText(getString(R.string.alert_success_coupon_code));
            relativeLayout_alert.setVisibility(View.VISIBLE);

            btnCouponCancel.setVisibility(View.VISIBLE);
            btnCouponApply.setEnabled(false);
            edit_couponCode.setEnabled(false);

            Global.strCouponCode = strCouponName;
            Global.discountPercent = couponPercent;
        }

        if (Global.strCouponCode.equals("")) {

            discount.setText(getString(R.string.str_discount));
            discountTotal.setText("- 0.00" + " " + getResources().getString(R.string.main_activity_currency));

            if (sub > 200.0) {
                grandtotal.setText(String.format("%.02f", sub) + " " + getString(R.string.main_activity_currency));
            } else {
                grandtotal.setText(String.format("%.02f", sub + 20) + " " + getString(R.string.main_activity_currency));
            }

        } else {

            discount.setText(getString(R.string.str_discount) + " (" + Global.strCouponCode + ")");

            float discountCost = sub * (Global.discountPercent / 100);

            discountTotal.setText("- " + String.format("%.02f", discountCost) + " " + getResources().getString(R.string.main_activity_currency));

            if (sub > 200.0) {
                grandtotal.setText(String.format("%.02f", sub - discountCost) + " " + getString(R.string.main_activity_currency));
            } else {
                grandtotal.setText(String.format("%.02f", sub + 20 - discountCost) + " " + getString(R.string.main_activity_currency));
            }
        }
    }

    public void onCashCheckOut() {

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

        int orderCount = s.getInt("count", 0) + 1;
        edit.putString("order" + orderCount, String.valueOf(orderumber));
        edit.putString("total" + orderCount, String.format("%.02f", amountTotal));
        edit.putString("date" + orderCount, strDate);
        edit.putString("ship" + orderCount, strShipping);
        edit.putString("email" + orderCount, email);
        edit.putString("shipmentname" + orderCount, Global.strShipmentUsername);
        edit.putString("shipmentaddress" + orderCount, Global.strShipmentAddress);
        edit.putString("shipmenttelephone" + orderCount, Global.strShipmentTelephone);
        edit.putString("paymentmethod" + orderCount, Global.PAYMENT_CASH);
        edit.putString("discount" + orderCount, Global.strCouponCode);
        edit.putString("discountPercent" + orderCount, String.valueOf(Global.discountPercent));
        edit.putInt("count", orderCount);
        edit.commit();
        ss1.edit().clear().commit();

        Intent intent = new Intent(CheckoutActivity.this, PaymentConfirmationActivity.class);
        intent.putExtra("orderID", String.valueOf(orderumber));
        intent.putExtra("orderDate", strDate);
        intent.putExtra("shipping", strShipping);
        intent.putExtra("totalCost", String.format("%.02f", amountTotal));
        intent.putExtra("email", email);
        intent.putExtra("shipmentUsername", Global.strShipmentUsername);
        intent.putExtra("shipmentAddress", Global.strShipmentAddress);
        intent.putExtra("shipmentTelephone", Global.strShipmentTelephone);
        intent.putExtra("paymentMethod", Global.PAYMENT_CASH);
        intent.putExtra("discount", Global.strCouponCode);
        intent.putExtra("discountPercent", String.valueOf(Global.discountPercent));
        startActivity(intent);
        finish();
    }

    public int pxToDp(int px) {
        final float scale = getBaseContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (px * scale + 0.5f);

        return pixels;
    }

    public void addItem(String imagePath, String titleString, String priceString, String numString, final int numbersSelectorID, int shipmentNumber) {

        final RelativeLayout content = new RelativeLayout(this);
        final ImageView image = new ImageView(this);
        TextView title = new TextView(this);
        LinearLayout num = new LinearLayout(this);
        LinearLayout line = new LinearLayout(this);
        ImageView leftArrow = new ImageView(this);
        final TextView value = new TextView(this);
        ImageView rightArrow = new ImageView(this);

        TextView shipmentNum = new TextView(this);
        LinearLayout shipItem = new LinearLayout(this);
        LinearLayout deliveryTaxes = new LinearLayout(this);
        TextView delivery = new TextView(this);
        TextView taxes = new TextView(this);
        LinearLayout shippinglayout = new LinearLayout(this);

        LinearLayout.LayoutParams shipItemParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams deliveryTaxesParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams shippinglayoutparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams deliveryParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams taxesParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        delivery.setGravity(Gravity.CENTER_VERTICAL);
        delivery.setPadding(pxToDp(10), pxToDp(10), pxToDp(10), pxToDp(10));
        taxes.setPadding(pxToDp(10), pxToDp(10), pxToDp(10), pxToDp(10));
        shipItemParams.setMargins(0, 0, 0, pxToDp(15));

        shipItem.setLayoutParams(shipItemParams);
        deliveryTaxes.setLayoutParams(deliveryTaxesParams);
        shippinglayout.setLayoutParams(shippinglayoutparams);
        delivery.setLayoutParams(deliveryParams);
        taxes.setLayoutParams(taxesParams);

        shipItem.setOrientation(LinearLayout.VERTICAL);
        shipItem.setPadding(0, 0, 0, pxToDp(6));
        deliveryTaxes.setOrientation(LinearLayout.HORIZONTAL);
        shippinglayout.setOrientation(LinearLayout.HORIZONTAL);

        deliveryTaxes.addView(delivery);
        deliveryTaxes.addView(taxes);
        shipItem.addView(shippinglayout);
        shipItem.addView(content);

        shipItem.setBackgroundColor(Color.parseColor("#d7d6d5"));

        delivery.setText(R.string.checkout_standard_delivery);
        delivery.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

        if (sub > 200.0) {
            taxes.setText(getResources().getString(R.string.taxes));
        } else {
            taxes.setText("20.00 " + getString(R.string.main_activity_currency));
        }

        taxes.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        taxes.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

        content.setBackgroundColor(Color.WHITE);


        try {

            TextView price = new TextView(this);
            ImageView details = new ImageView(this);
            final ImageView delete = new ImageView(this);
            line.setBackgroundColor(Color.GRAY);


            LinearLayout.LayoutParams cotenetParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(pxToDp(60), pxToDp(60));
            RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams numParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            RelativeLayout.LayoutParams priceParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams detailsParams = new RelativeLayout.LayoutParams(pxToDp(20), pxToDp(70));
            RelativeLayout.LayoutParams deleteParams = new RelativeLayout.LayoutParams(pxToDp(20), pxToDp(70));
            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
            LinearLayout.LayoutParams shipmentNumParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


            LinearLayout.LayoutParams arrowParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
            LinearLayout.LayoutParams valueParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);


            line.setLayoutParams(lineParams);


            image.setId(id);
            ++id;
            title.setId(id);
            ++id;
            num.setId(id);
            ++id;
            price.setId(id);
            ++id;
            details.setId(id);
            ++id;
            delete.setId(id);
            ++id;


            shipmentNum.setLayoutParams(shipmentNumParams);
            shipmentNum.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            shipmentNum.setText(getString(R.string.checkout_shipment) + " " + shipmentNumber + "");
            item.addView(shipmentNum);

            imageParams.addRule(RelativeLayout.RIGHT_OF, num.getId());

            titleParams.addRule(RelativeLayout.RIGHT_OF, image.getId());
            titleParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

            numParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

            priceParams.addRule(RelativeLayout.RIGHT_OF, image.getId());
            priceParams.addRule(RelativeLayout.BELOW, title.getId());


            valueParams.gravity = Gravity.CENTER;

            cotenetParams.setMargins(0, 0, 0, pxToDp(1));
            content.setLayoutParams(cotenetParams);

            Context context = this;
            Picasso.with(context).load(imagePath).into(image);
            image.setAdjustViewBounds(true);
            imageParams.setMargins(0, 0, pxToDp(10), 0);
            image.setLayoutParams(imageParams);

            title.setLayoutParams(titleParams);
            title.setText(titleString);
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            title.setPadding(0, 0, 0, pxToDp(10));
            title.setMaxLines(1);
            title.setEllipsize(TextUtils.TruncateAt.END);

            numParams.setMargins(0, 0, pxToDp(10) + pxToDp(10), 0);
            num.setLayoutParams(numParams);

            price.setLayoutParams(priceParams);
            price.setText(priceString + " " + getString(R.string.main_activity_currency));
            price.setTextColor(Color.BLACK);

            price.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);


            content.addView(image);
            content.addView(title);
            content.addView(num);
            content.addView(price);

            content.setBackgroundColor(Color.WHITE);
            content.setPadding(pxToDp(10), pxToDp(10), pxToDp(10), pxToDp(10));


            item.addView(shipItem);


            num.setOrientation(LinearLayout.HORIZONTAL);

            value.setLayoutParams(valueParams);
            value.setGravity(Gravity.CENTER_VERTICAL);
            value.setText(numString + "x");
            value.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            value.setId(numbersSelectorID);

            num.addView(value);

        } catch (Exception ex) {

        }

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
//                    cartTotal = result.getInt("total");
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

    public class checkout extends AsyncTask<String, Void, Boolean> {

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
                result = jsonFunctions.checkout(params[0], params[1], params[2], params[3]);
                Log.d("Parameter",params[1]);
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

    public class onApplyCouponCode extends AsyncTask<String, Void, Boolean> {

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
                result = jsonFunctions.setCouponCode(params[0]);
                Log.d("Parameter",params[0]);
            } catch (Exception e) {
                Log.d("ResultJsonParsing", e.toString());
            }

            if (result == null) {
                error = "null json object";
                return false;
            }
            try {
                if (result.getInt("success") == 1) {
                    strCouponName = result.getString("shopcart_name");
                    couponPercent = Float.valueOf(result.getString("shopcart_rule"));
                    Log.d("Parameter", String.format("CouponCode = %s,  DiscountPercent = %f", strCouponName, couponPercent));
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}