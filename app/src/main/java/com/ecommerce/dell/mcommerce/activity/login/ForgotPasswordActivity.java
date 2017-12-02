package com.ecommerce.dell.mcommerce.activity.login;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.ecommerce.dell.mcommerce.R;
import com.ecommerce.dell.mcommerce.jsonparser.JsonFunctions;
import com.ecommerce.dell.mcommerce.utility.UtilityClass;

import org.json.JSONObject;

public class ForgotPasswordActivity extends ActionBarActivity {

    public SVProgressHUD mSVProgressHUD;
    private EditText forgottenmail;
    RelativeLayout relativeLayout_alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.forgetPassword_title);

        forgottenmail = (EditText) findViewById(R.id.forgetenmail);
        relativeLayout_alert = (RelativeLayout)findViewById(R.id.relativeLayout_alert);
        relativeLayout_alert.setVisibility(View.INVISIBLE);

        Button btn_alert_ok = (Button)findViewById(R.id.button_alert_ok);
        Button btn_alert_cancel = (Button)findViewById(R.id.button_alert_cancel);

        btn_alert_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout_alert.setVisibility(View.INVISIBLE);
            }
        });

        btn_alert_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout_alert.setVisibility(View.INVISIBLE);
                finish();
            }
        });

        Button forget = (Button) findViewById(R.id.forgetBtn);
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (UtilityClass.isInternetAvailable(getApplicationContext())) {
                    if (forgottenmail.getText().toString().equals("")){
                        new AlertView(getString(R.string.alert),
                                getString(R.string.alert_input_email), null,
                                new String[]{getString(R.string.ok)}, null, ForgotPasswordActivity.this,
                                AlertView.Style.Alert, null).show();
                    } else {

                        mSVProgressHUD = new SVProgressHUD(ForgotPasswordActivity.this);
                        mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);

                        new forgettenMail().execute(forgottenmail.getText().toString());
                    }
                } else {
                    new AlertView(getString(R.string.alert),
                            getString(R.string.alert_network_connection_failed), null,
                            new String[]{getString(R.string.ok)}, null, ForgotPasswordActivity.this,
                            AlertView.Style.Alert, null).show();
                }
            }
        });

    }

    public class forgettenMail extends AsyncTask<String,String,Void> {

        JSONObject result = new JSONObject();
        String error = "";
        JsonFunctions jsonFunctions = new JsonFunctions();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("getProductsTask", "getProductsTask");
        }

        @Override
        protected Void doInBackground(String... params) {

            try {
                result = jsonFunctions.resetPassword(params[0]);
            } catch (Exception e) {
                Log.d("ResultJsonParsing", e.toString());
            }

            if (result == null) {
                error = "null json object";
            }

            try {
                if (result.getInt("success") == 1) {

                }
            } catch (Exception ex) {
                error = ex.toString();
                Log.d("getProductsTask", error);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            mSVProgressHUD.dismiss();
            relativeLayout_alert.setVisibility(View.VISIBLE);
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
