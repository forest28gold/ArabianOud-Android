package com.ecommerce.dell.mcommerce.activity.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.ecommerce.dell.mcommerce.R;
import com.ecommerce.dell.mcommerce.activity.payment.AddAddressActivity;
import com.ecommerce.dell.mcommerce.jsonparser.JsonFunctions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.ArrayList;

public class ProfileShippingAdressesActivity extends ActionBarActivity {

    private LinearLayout container;
    public SVProgressHUD mSVProgressHUD;
    public static Boolean toggleIsOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_shipping_adresses);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_stored_address);

        container = (LinearLayout) findViewById(R.id.container);

        Button addAddress = (Button) findViewById(R.id.addAddress);
        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileShippingAdressesActivity.this, AddAddressActivity.class);
                i.putExtra("activityType","ProfileStoredAddress");
                startActivity(i);
            }
        });

        toggleIsOn = false;
        onShowShippingAddress();
    }

    public void onShowShippingAddress() {

        mSVProgressHUD = new SVProgressHUD(this);
        mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);

        SharedPreferences s = getSharedPreferences("user" , 0);
        new getAddressesTask().execute(s.getString("email", ""));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (toggleIsOn)
            onShowShippingAddress();
    }

    public int pxToDp(int px) {
        final float scale = getBaseContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (px * scale + 0.5f);

        return pixels;
    }

    public class getAddressesTask extends AsyncTask<String , Void , ArrayList<ArrayList<String>>> {

        JSONObject result = new JSONObject();
        String error = "";
        ArrayList<ArrayList<String>> return_products = new ArrayList<ArrayList<String>>();
        JsonFunctions jsonFunctions = new JsonFunctions();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<ArrayList<String>> doInBackground(String... params) {
            try {
                result = jsonFunctions.getAllAddresses(params[0]);
            } catch (Exception e) {
                Log.d("ResultJsonParsing", e.toString());
            }

            if (result == null) {
                error = "null json object";
                return null;
            }

            try {
                if (result.getInt("success") == 1) {
                    JSONArray json_products = result.getJSONArray("addresses");
                    for (int i = 0; i < json_products.length(); i++) {
                        JSONArray json_products_list = json_products.getJSONArray(i);
                        ArrayList<String> product_list = new ArrayList<>();

                        for (int x = 0; x < json_products_list.length(); x++) {
                            product_list.add(json_products_list.getString(x));
                        }
                        return_products.add(product_list);
                    }
                    return return_products;
                }
            } catch (Exception ex) {
                error = ex.toString();
                Log.d("getProductsTask" ,error );

            }
            return null;

        }

        @Override
        protected void onPostExecute(ArrayList<ArrayList<String>> products) {
            super.onPostExecute(products);

            toggleIsOn = true;
            mSVProgressHUD.dismiss();

            if (products == null) {
                Toast.makeText(getBaseContext() , getResources().getString(R.string.NoAddressesFound) , Toast.LENGTH_SHORT).show();
            } else {
                if (products.size() > 0) {
                    container.removeAllViews();
                    fillActivity(products);
                } else {
                    Toast.makeText(getBaseContext() , getResources().getString(R.string.NoAddressesFound) , Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void fillActivity( ArrayList<ArrayList<String>> data) {

        for (int x=0 ; x<data.size() ; x++) {

            LinearLayout main = new LinearLayout(this);
            LinearLayout cityEdit = new LinearLayout(this);
            LinearLayout addressPhone = new LinearLayout(this);
            LinearLayout line = new LinearLayout(this);
            final TextView city = new TextView(this);
            final TextView street = new TextView(this);
            final TextView phone = new TextView(this);

            LinearLayout streetImage = new LinearLayout(this);
            LinearLayout phoneImage = new LinearLayout(this);

            ImageView stIM = new ImageView(this);
            ImageView phIM = new ImageView(this);

            streetImage.setGravity(Gravity.CENTER_VERTICAL);
            phoneImage.setGravity(Gravity.CENTER_VERTICAL);

            LinearLayout.LayoutParams mainParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams phoneEditParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT ,pxToDp(1) );
            LinearLayout.LayoutParams addressPhoneParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            LinearLayout.LayoutParams cityParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams streetParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT );
            LinearLayout.LayoutParams phoneParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT ,LinearLayout.LayoutParams.WRAP_CONTENT );
            LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT ,LinearLayout.LayoutParams.WRAP_CONTENT );

            LinearLayout.LayoutParams streetImageParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT  , 1f);
            LinearLayout.LayoutParams phoneImageParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT , 1f);

            LinearLayout.LayoutParams stIMParams = new LinearLayout.LayoutParams(pxToDp(20) , pxToDp(20));
            LinearLayout.LayoutParams phIMParams = new LinearLayout.LayoutParams(pxToDp(20) , pxToDp(20));

            streetImage.setLayoutParams(streetImageParams);
            phoneImage.setLayoutParams(phoneImageParams);

            stIM.setLayoutParams(stIMParams);
            phIM.setLayoutParams(phIMParams);

            stIM.setImageResource(com.ecommerce.dell.mcommerce.R.drawable.location);
            phIM.setImageResource(com.ecommerce.dell.mcommerce.R.drawable.phone);

            streetImage.setOrientation(LinearLayout.HORIZONTAL);
            phoneImage.setOrientation(LinearLayout.HORIZONTAL);

            streetImage.addView(stIM);
            streetImage.addView(street);

            phoneImage.addView(phIM);
            phoneImage.addView(phone);

            main.setOrientation(LinearLayout.VERTICAL);
            cityParams.setMargins(pxToDp(5), pxToDp(10), pxToDp(5), pxToDp(5));

            addressPhone.setOrientation(LinearLayout.HORIZONTAL);
            addressPhone.setWeightSum(2);
            addressPhoneParams.setMargins(pxToDp(10), pxToDp(5), pxToDp(10), pxToDp(5));
            phoneParams.setMargins(pxToDp(10), pxToDp(5), pxToDp(10), pxToDp(5));

            main.setLayoutParams(mainParams);
            cityEdit.setLayoutParams(phoneEditParams);
            addressPhone.setLayoutParams(addressPhoneParams);
            line.setLayoutParams(lineParams);
            city.setLayoutParams(cityParams);
            street.setLayoutParams(streetParams);
            phone.setLayoutParams(phoneParams);

            main.addView(cityEdit);
            main.addView(addressPhone);
            main.addView(line);

            cityEdit.addView(city);

            addressPhone.addView(streetImage);
            addressPhone.addView(phoneImage);

            line.setBackgroundColor(Color.GRAY);
            lineParams.setMargins(0, pxToDp(5), 0, 0);

            city.setTextSize(pxToDp(8));
            city.setTextColor(Color.BLACK);

            String decodeCity = URLDecoder.decode(data.get(x).get(0).toUpperCase());
            String decodedStreet = URLDecoder.decode(data.get(x).get(1).toUpperCase());
            city.setText(decodeCity);
            street.setText(decodedStreet);
            street.setMaxLines(2);
            street.setEllipsize(TextUtils.TruncateAt.END);
            phone.setText(data.get(x).get(2));

            main.setId(Integer.parseInt(data.get(x).get(3)));

            container.addView(main);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home) {
            toggleIsOn = false;
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        toggleIsOn = false;
        finish();
    }
}
