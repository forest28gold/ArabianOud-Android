package com.ecommerce.dell.mcommerce.activity.payment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.ecommerce.dell.mcommerce.R;
import com.ecommerce.dell.mcommerce.jsonparser.JsonFunctions;
import com.ecommerce.dell.mcommerce.utility.UtilityClass;

import org.json.JSONObject;

import java.util.ArrayList;


public class AddAddressActivity extends ActionBarActivity {

    private EditText firstname, lastname, tel, streetname, streetnumber, addressextra, country, city;
    public SVProgressHUD mSVProgressHUD;
    private ArrayList<String> addressData;

    RelativeLayout relativeLayout_alert;
    TextView text_alert;

    private String FIRST_NAME = "";
    private String LAST_NAME = "";
    private String EMAIL = "";
    private String countryString;
    private String input;
    private String[] cities ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_add_address);

        mSVProgressHUD = new SVProgressHUD(this);

        Intent i1 = getIntent();
        input = i1.getStringExtra("activityType");

        cities = getResources().getStringArray(R.array.cities);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Choose City");
        alertBuilder.setItems(cities, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                city.setText(cities[which]);

            }
        });

        final AlertDialog alertDialog = alertBuilder.create();

        addressData = new ArrayList<>();

        SharedPreferences user = getSharedPreferences("user" , 0);
        SharedPreferences settings = getSharedPreferences("setting",0);

        firstname = (EditText) findViewById(R.id.fname);
        lastname = (EditText) findViewById(R.id.lname);
        tel = (EditText) findViewById(R.id.tel);
        streetname = (EditText) findViewById(R.id.stname);
        streetnumber = (EditText) findViewById(R.id.stnumber);
        addressextra = (EditText) findViewById(R.id.addextra);
        country = (EditText) findViewById(R.id.country);
        city = (EditText) findViewById(R.id.city);

        FIRST_NAME  = user.getString("firstname" , "");
        LAST_NAME = user.getString("lastname" , "");
        EMAIL = user.getString("email" , "");
        countryString = settings.getString("country","");
        firstname.setText(FIRST_NAME);
        lastname.setText(LAST_NAME);
        country.setText(countryString);

        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();
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

        Button addAddress = (Button) findViewById(R.id.add);
        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstname.getText().toString().equals("")) {

                    new AlertView(getString(R.string.alert),
                            getString(R.string.alert_input_first_name), null,
                            new String[]{getString(R.string.ok)}, null, AddAddressActivity.this,
                            AlertView.Style.Alert, null).show();

                } else if (lastname.getText().toString().equals("")) {

                    new AlertView(getString(R.string.alert),
                            getString(R.string.alert_input_last_name), null,
                            new String[]{getString(R.string.ok)}, null, AddAddressActivity.this,
                            AlertView.Style.Alert, null).show();

                } else if (tel.getText().toString().equals("")) {

                    new AlertView(getString(R.string.alert),
                            getString(R.string.alert_input_phone), null,
                            new String[]{getString(R.string.ok)}, null, AddAddressActivity.this,
                            AlertView.Style.Alert, null).show();

                } else if (streetname.getText().toString().equals("")) {

                    new AlertView(getString(R.string.alert),
                            getString(R.string.alert_input_street_name), null,
                            new String[]{getString(R.string.ok)}, null, AddAddressActivity.this,
                            AlertView.Style.Alert, null).show();

                } else if (addressextra.getText().toString().equals("")) {

                    new AlertView(getString(R.string.alert),
                            getString(R.string.alert_input_address_extra), null,
                            new String[]{getString(R.string.ok)}, null, AddAddressActivity.this,
                            AlertView.Style.Alert, null).show();

                } else if (city.getText().toString().equals("")) {

                    new AlertView(getString(R.string.alert),
                            getString(R.string.alert_input_city), null,
                            new String[]{getString(R.string.ok)}, null, AddAddressActivity.this,
                            AlertView.Style.Alert, null).show();

                } else {

                    addressData.clear();
                    addressData.add(EMAIL);
                    addressData.add("");
                    addressData.add(FIRST_NAME);
                    addressData.add(LAST_NAME);
                    addressData.add(streetname.getText().toString());
                    addressData.add(streetnumber.getText().toString());
                    addressData.add(addressextra.getText().toString());
                    addressData.add(city.getText().toString());
                    addressData.add("Spelenzo");
                    addressData.add("1234");
                    addressData.add(countryString);
                    addressData.add(tel.getText().toString());
                    addressData.add("1234");

                    try {
                        if (UtilityClass.isInternetAvailable(getApplicationContext())) {

                            mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);

                            Boolean done = new addAddress().execute(addressData).get();
                            if (done) {
                                if (input.equals("StoredAddress")) {
                                    finish();
                                } else{
                                    finish();
                                }
                            } else {
                                text_alert.setText(getString(R.string.alert_network_connection_failed));
                                relativeLayout_alert.setVisibility(View.VISIBLE);
                            }
                        } else {

                            new AlertView(getString(R.string.alert),
                                    getString(R.string.alert_network_connection_failed), null,
                                    new String[]{getString(R.string.ok)}, null, AddAddressActivity.this,
                                    AlertView.Style.Alert, null).show();
                        }
                    } catch (Exception ex) {

                        new AlertView(getString(R.string.alert),
                                getString(R.string.alert_network_connection_failed), null,
                                new String[]{getString(R.string.ok)}, null, AddAddressActivity.this,
                                AlertView.Style.Alert, null).show();
                    }
                }

            }
        });

    }

    public  class addAddress extends AsyncTask<ArrayList<String> , Void , Boolean> {

        JSONObject result = new JSONObject();
        String error = "";
        JsonFunctions jsonFunctions = new JsonFunctions();
        boolean added = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(ArrayList<String>... params) {

            try {
                result = jsonFunctions.DefaultShippingBillingAddress(params[0].get(0), params[0].get(1),params[0].get(2), params[0].get(3),params[0].get(4), params[0].get(5),params[0].get(6), params[0].get(7),params[0].get(8), params[0].get(9),params[0].get(10), params[0].get(11), params[0].get(12));
            } catch (Exception e) {
                Log.d("ResultJsonParsing", e.toString());
            }

            if (result == null) {
                error = "null json object";
                return  null;
            }

            try{
                if(result.getInt("success") == 1) {
                    added = true;
                } else
                    added = false;

            } catch (Exception ex) {
                error = ex.toString();
            }
            return  added;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            mSVProgressHUD.dismiss();

            if (aBoolean == null){

                text_alert.setText(getString(R.string.alert_network_connection_failed));
                relativeLayout_alert.setVisibility(View.VISIBLE);
            }
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