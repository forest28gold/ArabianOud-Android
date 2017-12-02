package com.ecommerce.dell.mcommerce.activity.login;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.ecommerce.dell.mcommerce.R;
import com.ecommerce.dell.mcommerce.jsonparser.JsonFunctions;
import com.ecommerce.dell.mcommerce.utility.UtilityClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginActivity extends ActionBarActivity {

    public SVProgressHUD mSVProgressHUD;
    private EditText username, password;
    RelativeLayout relativeLayout_alert;
    private String[] params = new String[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.login_signin);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        relativeLayout_alert = (RelativeLayout)findViewById(R.id.relativeLayout_alert);
        relativeLayout_alert.setVisibility(View.INVISIBLE);

        Button btn_alert_ok = (Button)findViewById(R.id.button_alert_ok);
        btn_alert_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout_alert.setVisibility(View.INVISIBLE);
            }
        });

        Button login = (Button) findViewById(R.id.login);
        LinearLayout newaccount = (LinearLayout) findViewById(R.id.newaccount);
        LinearLayout forgetpassword = (LinearLayout) findViewById(R.id.forgetPassword);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String usernameString = username.getText().toString();
                String passwordString = password.getText().toString();

                if (username.getText().toString().equals("")) {

                    new AlertView(getString(R.string.alert),
                            getString(R.string.alert_input_email), null,
                            new String[]{getString(R.string.ok)}, null, LoginActivity.this,
                            AlertView.Style.Alert, null).show();

                } else if (password.getText().toString().equals("")) {

                    new AlertView(getString(R.string.alert),
                            getString(R.string.alert_input_password), null,
                            new String[]{getString(R.string.ok)}, null, LoginActivity.this,
                            AlertView.Style.Alert, null).show();

                } else {
                    params[0] = usernameString;
                    params[1] = passwordString;

                    if (UtilityClass.isInternetAvailable(getApplicationContext())) {

                        mSVProgressHUD = new SVProgressHUD(LoginActivity.this);
                        mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);
                        new login().execute(params);

                    } else {
                        new AlertView(getString(R.string.alert),
                                getString(R.string.alert_network_connection_failed), null,
                                new String[]{getString(R.string.ok)}, null, LoginActivity.this,
                                AlertView.Style.Alert, null).show();
                    }
                }
            }
        });

        newaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });

        forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(i);
            }
        });
    }

    public class login extends AsyncTask<String[], Void, Boolean> {

        JSONObject result = new JSONObject();
        JsonFunctions jsonFunctions = new JsonFunctions();
        String error = "";
        Boolean logedin = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String[]... params) {

            try {
                result = jsonFunctions.login(params[0][0], params[0][1]);
            } catch (Exception e) {
                Log.d("ResultJsonParsing", e.toString());
            }

            if (result == null) {
                error = "null json object";
                return null;
            }
            try {
                if (result.getInt("success") == 1) {
                    logedin = true;
                    JSONArray json_products = result.getJSONArray("customerData");
                    ArrayList<String> product_list = new ArrayList<>();
                    for (int x = 0; x < json_products.length(); x++) {
                        product_list.add(json_products.getString(x));
                    }
                    SharedPreferences s = getSharedPreferences("user", 0);
                    SharedPreferences.Editor edit = s.edit();
                    edit.putString("firstname", product_list.get(1));
                    edit.putString("lastname", product_list.get(2));
                    edit.putString("email", product_list.get(0));
                    edit.putString("address", product_list.get(4) + " / " + product_list.get(5));
                    edit.putString("status", "1");
                    edit.putString("id", product_list.get(3));
                    edit.putString("telephone", product_list.get(6));
                    edit.commit();
                } else
                    logedin = false;
            } catch (Exception ex) {
                error = ex.toString();
            }
            return logedin;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            mSVProgressHUD.dismiss();

            if (aBoolean) {
                finish();
            } else {
                relativeLayout_alert.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}