package com.ecommerce.dell.mcommerce.activity.payment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.ecommerce.dell.mcommerce.R;
import com.ecommerce.dell.mcommerce.jsonparser.JsonFunctions;
import com.ecommerce.dell.mcommerce.models.Global;
import com.ecommerce.dell.mcommerce.utility.UtilityClass;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalOAuthScopes;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.paypal.android.sdk.payments.ShippingAddress;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class CheckoutPaypalActivity extends ActionBarActivity {

    private static final String TAG = "paymentExample";

    public SVProgressHUD mSVProgressHUD;
    public Handler handler = new Handler();
    String orderId;
    String jsonException,email,ids , qtys;
    String[] data1;
    int items;
    float total;
    public Boolean toggleIsOn;

    RelativeLayout relativeLayout_alert;
    TextView text_alert;

    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;
    private static final String CONFIG_CLIENT_ID = "Abb__fI4mshH----------------------------------S2DAJRc";
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            .merchantName("ArabianOud")
            .merchantPrivacyPolicyUri(Uri.parse("https://shop.arabianoud.com/index.php/privacy-policy/"))
            .merchantUserAgreementUri(Uri.parse("https://shop.arabianoud.com/index.php/terms/"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_paypal);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_paypal));
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

        ids = "";
        qtys = "";
        email = "";
        orderId = "";
        data1 = new String[3];
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
        data1[0] = ids;
        data1[1] = qtys;
        data1[2] = email;

        items = itemsCount;

        for (int index = 1; index <= itemsCount; index++) {
            int id = ss1.getInt("id" + index, 0);
            if (id != 0) {
                String price = ss1.getString("price" + id, "");
                String amount = ss1.getString("amount" + id, "");

                total += Float.parseFloat(price) * Integer.parseInt(amount);
            }
        }

        toggleIsOn = false;
        Button btn_payment_confirm = (Button)findViewById(R.id.button_payment);
        btn_payment_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggleIsOn) {

                    mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);

                    Handler handlerCheckout = new Handler();
                    handlerCheckout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (new checkoutPaypal().execute(email, String.valueOf(total), Global.strShippingAddress, Global.strCouponCode).get()) {
                                    mSVProgressHUD.dismiss();
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            onCheckoutPaypal();
                                        }
                                    });
                                } else {

                                    if (new DeleteAllCart().execute(email).get()) {
                                        if (new addToCart().execute(data1).get()) {
                                            if (new checkoutPaypal().execute(email, String.valueOf(total), Global.strShippingAddress, Global.strCouponCode).get()) {
                                                mSVProgressHUD.dismiss();
                                                runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        onCheckoutPaypal();
                                                    }
                                                });
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
                                Log.d("checkoutError",ex.toString());
                                mSVProgressHUD.dismiss();
                                text_alert.setText(getString(R.string.alert_ordering_failed));
                                relativeLayout_alert.setVisibility(View.VISIBLE);
                            }
                        }
                    }, 1000);
                } else {
                    PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);

                    Intent intent1 = new Intent(CheckoutPaypalActivity.this, PaymentActivity.class);
                    intent1.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                    intent1.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
                    startActivityForResult(intent1, REQUEST_CODE_PAYMENT);
                }
            }
        });

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent1 = new Intent(CheckoutPaypalActivity.this, PaymentActivity.class);
        intent1.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent1.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
        startActivityForResult(intent1, REQUEST_CODE_PAYMENT);

    }

    public void onBuyPressed(View pressed) {

        PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(CheckoutPaypalActivity.this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    private PayPalPayment getThingToBuy(String paymentIntent) {

        int shipmentcount = 0;
        total = 0;
        SharedPreferences ss1 = getSharedPreferences("item", 0);
        int itemsCount = ss1.getInt("count", 0) ;

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
        }

        double amount1 = total;
        if (amount1 < 200) {
            amount1 += 20;
        }

        float discountCost = total * Global.discountPercent / 100;
        amount1 = amount1 - discountCost;

        return new PayPalPayment(new BigDecimal(amount1/3.75), "USD", "Paid Amount", paymentIntent);
    }

    /*
     * This method shows use of optional payment details and item list.
     */
    private PayPalPayment getStuffToBuy(String paymentIntent) {
        //--- include an item list, payment amount details
        PayPalItem[] items =
                {
                        new PayPalItem("sample item #1", 2, new BigDecimal("87.50"), "USD",
                                "sku-12345678"),
                        new PayPalItem("free sample item #2", 1, new BigDecimal("0.00"),
                                "USD", "sku-zero-price"),
                        new PayPalItem("sample item #3 with a longer name", 6, new BigDecimal("37.99"),
                                "USD", "sku-33333")
                };
        BigDecimal subtotal = PayPalItem.getItemTotal(items);
        BigDecimal shipping = new BigDecimal("7.21");
        BigDecimal tax = new BigDecimal("4.67");
        PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails(shipping, subtotal, tax);
        BigDecimal amount = subtotal.add(shipping).add(tax);
        PayPalPayment payment = new PayPalPayment(amount, "SAR", "sample item", paymentIntent);
        payment.items(items).paymentDetails(paymentDetails);

        return payment;
    }

    /*
     * Add app-provided shipping address to payment
     */
    private void addAppProvidedShippingAddress(PayPalPayment paypalPayment) {
        ShippingAddress shippingAddress =
                new ShippingAddress().recipientName("Mom Parker").line1("52 North Main St.")
                        .city("Austin").state("TX").postalCode("78729").countryCode("US");
        paypalPayment.providedShippingAddress(shippingAddress);
    }

    /*
     * Enable retrieval of shipping addresses from buyer's PayPal account
     */
    private void enableShippingAddressRetrieval(PayPalPayment paypalPayment, boolean enable) {
        paypalPayment.enablePayPalShippingAddressesRetrieval(enable);
    }

    public void onFuturePaymentPressed(View pressed) {
        Intent intent = new Intent(CheckoutPaypalActivity.this, PayPalFuturePaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startActivityForResult(intent, REQUEST_CODE_FUTURE_PAYMENT);
    }

    public void onProfileSharingPressed(View pressed) {
        Intent intent = new Intent(CheckoutPaypalActivity.this, PayPalProfileSharingActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PayPalProfileSharingActivity.EXTRA_REQUESTED_SCOPES, getOauthScopes());
        startActivityForResult(intent, REQUEST_CODE_PROFILE_SHARING);
    }

    private PayPalOAuthScopes getOauthScopes() {
        Set<String> scopes = new HashSet<String>(
                Arrays.asList(PayPalOAuthScopes.PAYPAL_SCOPE_EMAIL, PayPalOAuthScopes.PAYPAL_SCOPE_ADDRESS) );
        return new PayPalOAuthScopes(scopes);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                if (confirm != null) {
                    try {
                        Log.i(TAG, confirm.toJSONObject().toString(4));
                        Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));

                        toggleIsOn = true;
                        Toast.makeText(getApplicationContext(), "Payment Confirmation info received from PayPal", Toast.LENGTH_LONG).show();

                        mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);

                        try {
                            if (new checkoutPaypal().execute(email, String.valueOf(total), Global.strShippingAddress, Global.strCouponCode).get()) {
                                mSVProgressHUD.dismiss();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        onCheckoutPaypal();
                                    }
                                }, 0);
                            } else {

                                if (new DeleteAllCart().execute(email).get()) {
                                    if (new addToCart().execute(data1).get()) {
                                        if (new checkoutPaypal().execute(email, String.valueOf(total), Global.strShippingAddress, Global.strCouponCode).get()) {
                                            mSVProgressHUD.dismiss();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    onCheckoutPaypal();
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
                            Log.d("checkoutError",ex.toString());
                            mSVProgressHUD.dismiss();
                            text_alert.setText(getString(R.string.alert_ordering_failed));
                            relativeLayout_alert.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                        Toast.makeText(getApplicationContext(), "Paypal payment is failed.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Paypal payment is failed.", Toast.LENGTH_LONG).show();
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i(TAG, "The user canceled.");
                Toast.makeText(getApplicationContext(), "Paypal Payment cancelled", Toast.LENGTH_LONG).show();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(TAG, "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
                Toast.makeText(getApplicationContext(), "An invalid Payment or PayPalConfiguration was submitted", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth = data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("FuturePaymentExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("FuturePaymentExample", authorization_code);

                        sendAuthorizationToServer(auth);
                        Toast.makeText(getApplicationContext(), "Future Payment code received from PayPal", Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        Log.e("FuturePaymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("FuturePaymentExample", "The user canceled.");

            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("FuturePaymentExample", "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
            finish();
        } else if (requestCode == REQUEST_CODE_PROFILE_SHARING) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth = data.getParcelableExtra(PayPalProfileSharingActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("ProfileSharingExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("ProfileSharingExample", authorization_code);

                        sendAuthorizationToServer(auth);
                        Toast.makeText(
                                getApplicationContext(),
                                "Profile Sharing code received from PayPal", Toast.LENGTH_LONG)
                                .show();
                        finish();
                    } catch (JSONException e) {
                        Log.e("ProfileSharingExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("ProfileSharingExample", "The user canceled.");

            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("ProfileSharingExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }
    }

    private void sendAuthorizationToServer(PayPalAuthorization authorization) {

    }

    public void onFuturePaymentPurchasePressed(View pressed) {
        // Get the Client Metadata ID from the SDK
        String metadataId = PayPalConfiguration.getClientMetadataId(this);

        Log.i("FuturePaymentExample", "Client Metadata ID: " + metadataId);
        // PayPal...
        Toast.makeText(
                getApplicationContext(), "Client Metadata Id received from SDK", Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onDestroy() {
        // Stop service when done
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    public void onCheckoutPaypal() {

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

        int orderCount = s.getInt("count" , 0)+1;
        edit.putString("order"+orderCount, String.valueOf(orderumber));
        edit.putString("total" + orderCount, String.format("%.02f", amountTotal));
        edit.putString("date" + orderCount, strDate);
        edit.putString("ship" + orderCount, strShipping);
        edit.putString("email" + orderCount, email);
        edit.putString("shipmentname" + orderCount, Global.strShipmentUsername);
        edit.putString("shipmentaddress" + orderCount, Global.strShipmentAddress);
        edit.putString("shipmenttelephone" + orderCount, Global.strShipmentTelephone);
        edit.putString("paymentmethod" + orderCount, Global.PAYMENT_PAYPAL);
        edit.putString("discount" + orderCount, Global.strCouponCode);
        edit.putString("discountPercent" + orderCount, String.valueOf(Global.discountPercent));
        edit.putInt("count", orderCount);
        edit.commit();
        ss1.edit().clear().commit();

        Intent intent = new Intent(CheckoutPaypalActivity.this, PaymentConfirmationActivity.class);
        intent.putExtra("orderID", String.valueOf(orderumber));
        intent.putExtra("orderDate", strDate);
        intent.putExtra("shipping", strShipping);
        intent.putExtra("totalCost", String.format("%.02f", amountTotal));
        intent.putExtra("email", email);
        intent.putExtra("shipmentUsername", Global.strShipmentUsername);
        intent.putExtra("shipmentAddress", Global.strShipmentAddress);
        intent.putExtra("shipmentTelephone", Global.strShipmentTelephone);
        intent.putExtra("paymentMethod", Global.PAYMENT_PAYPAL);
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

    public  class addToCart extends AsyncTask<String[] , Void , Boolean> {

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
                result = jsonFunctions.addToCart(params[0][0], params[0][1] , params[0][2]);
            } catch (Exception e) {
                Log.d("ResultJsonParsing", e.toString());
            }

            if (result == null) {
                error = "null json object";
                return  false;
            }
            try{
                if(result.getInt("success") == 1) {
//                    total = result.getInt("total");
                    return true;
                }
            } catch (Exception ex){
                error = ex.toString();
                Log.d("addtocart" , error);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

    public class checkoutPaypal extends AsyncTask<String , Void , Boolean> {

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
                result = jsonFunctions.checkout_paypal(params[0], params[1], params[2], params[3]);
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
