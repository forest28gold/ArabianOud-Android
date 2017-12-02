package com.ecommerce.dell.mcommerce.activity.login;

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
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.ecommerce.dell.mcommerce.R;
import com.ecommerce.dell.mcommerce.jsonparser.JsonFunctions;
import com.ecommerce.dell.mcommerce.utility.UtilityClass;

import org.json.JSONObject;

import java.util.ArrayList;

public class SignupActivity extends ActionBarActivity {

    private EditText fname, sname, email, password;
    RelativeLayout relativeLayout_alert;
    TextView text_alert;
    private ArrayList<String> registerparams = new ArrayList<>();
    public SVProgressHUD mSVProgressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_signup);

        fname = (EditText) findViewById(R.id.fname);
        sname = (EditText) findViewById(R.id.sname);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

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

        Button register = (Button) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fname.getText().toString().equals("")) {

                    new AlertView(getString(R.string.alert),
                            getString(R.string.alert_input_first_name), null,
                            new String[]{getString(R.string.ok)}, null, SignupActivity.this,
                            AlertView.Style.Alert, null).show();

                } else if (sname.getText().toString().equals("")) {

                    new AlertView(getString(R.string.alert),
                            getString(R.string.alert_input_last_name), null,
                            new String[]{getString(R.string.ok)}, null, SignupActivity.this,
                            AlertView.Style.Alert, null).show();

                } else if (!isValidEmail(email.getText().toString())) {

                    new AlertView(getString(R.string.alert),
                            getString(R.string.alert_input_email_correct), null,
                            new String[]{getString(R.string.ok)}, null, SignupActivity.this,
                            AlertView.Style.Alert, null).show();

                } else if (password.getText().toString().length() < 6) {

                    new AlertView(getString(R.string.alert),
                            getString(R.string.alert_input_password_length), null,
                            new String[]{getString(R.string.ok)}, null, SignupActivity.this,
                            AlertView.Style.Alert, null).show();

                } else {
                    uploadRegisterData();
                }
            }
        });
    }

    public class signup extends AsyncTask<ArrayList<String> , Void , Boolean> {

        JSONObject result = new JSONObject();
        String error = "";
        JsonFunctions jsonFunctions = new JsonFunctions();
        Boolean logedin = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(ArrayList<String>... params) {
            try {
                result = jsonFunctions.register(params[0].get(0) , params[0].get(1) , params[0].get(2) , params[0].get(3)  );
            } catch (Exception e) {
                Log.d("ResultJsonParsing", e.toString());
            }

            if (result == null) {
                error = "null json object";
                return  null;
            }

            try{
                if(result.getInt("success") == 1) {
                    logedin = true;
                } else
                    logedin = false;

            } catch (Exception ex){
                error = ex.toString();
            }
            return  logedin;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);

            mSVProgressHUD.dismiss();

           if (s == null){
               text_alert.setText(getString(R.string.alert_network_connection_failed));
               relativeLayout_alert.setVisibility(View.VISIBLE);
           }
        }
    }

    public  static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public void uploadRegisterData() {

        registerparams.clear();
        registerparams.add(fname.getText().toString().trim()) ;
        registerparams.add(sname.getText().toString().trim() ) ;
        registerparams.add(email.getText().toString());
        registerparams.add(password.getText().toString());


        try {
            Boolean x = false ;

            if (UtilityClass.isInternetAvailable(getApplicationContext())) {

                mSVProgressHUD = new SVProgressHUD(SignupActivity.this);
                mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);
                x = new signup().execute(registerparams).get();

            } else {
                text_alert.setText(getString(R.string.alert_network_connection_failed));
                relativeLayout_alert.setVisibility(View.VISIBLE);
            }

            if (x) {
                Toast.makeText(getApplicationContext(), "registered successfully", Toast.LENGTH_SHORT).show();

                SharedPreferences s = getSharedPreferences("user", 0);
                SharedPreferences.Editor edit = s.edit();
                edit.putString("firstname", fname.getText().toString().trim());
                edit.putString("lastname", sname.getText().toString().trim());
                edit.putString("email", email.getText().toString());
                edit.putString("status", "1");
                edit.commit();

                finish();

            } else {
                text_alert.setText(getString(R.string.alert_user_registered));
                relativeLayout_alert.setVisibility(View.VISIBLE);
            }

        } catch (Exception ex) {
            text_alert.setText(getString(R.string.alert_register_failed));
            relativeLayout_alert.setVisibility(View.VISIBLE);
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
