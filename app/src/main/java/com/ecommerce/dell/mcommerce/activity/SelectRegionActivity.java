package com.ecommerce.dell.mcommerce.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.ecommerce.dell.mcommerce.R;


public class SelectRegionActivity extends ActionBarActivity {

    private Spinner lang_spinner;

    private SharedPreferences user;
    private String selected_language ="";
    private String country;
    private String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_region);

        SharedPreferences setting = getSharedPreferences("setting", 0);
        language = setting.getString("language", "");

        String[] languages = getResources().getStringArray(R.array.language);

        lang_spinner = (Spinner) findViewById(R.id.lang_spinner);
        Button proceed = (Button) findViewById(R.id.proceed);

        ArrayAdapter<String> lang_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, languages);
        lang_spinner.setAdapter(lang_adapter);
        lang_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_language = String.valueOf(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        country = getResources().getString(R.string.countryName);

        user = getSharedPreferences("setting" ,0);
        String storedCountry = user.getString("country" , "");
        String activityMode ="";

        if (getIntent().hasExtra("mode")) {
            activityMode = getIntent().getStringExtra("mode");
        }

        if (!storedCountry.equals("")) {
            if (!activityMode.equals("edit")) {
                if (!selected_language.equals("")) {
                    restartApplication();
                }
            }
        }

        final String finalActivityMode = activityMode;

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!((selected_language.equals("0") && language.equals("ar")) || (selected_language.equals("1") && language.equals("en")))){
                    Log.d("Selected Language",selected_language);
                    Log.d("Language",language);

                    if (finalActivityMode.equals("edit")) {

                        new AlertView(getString(R.string.changeLanguageTitle),
                                getString(R.string.changeLanguage),
                                getString(R.string.cancel),
                                new String[]{getString(R.string.ok)},
                                null, SelectRegionActivity.this, AlertView.Style.Alert, (new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, int position) {
                                if(position != AlertView.CANCELPOSITION) {

                                    SharedPreferences.Editor edit = user.edit();
                                    edit.putString("country", country);
                                    if (selected_language.equals("0"))
                                        edit.putString("language","ar");
                                    else if (selected_language.equals("1"))
                                        edit.putString("language", "en");

                                    edit.commit();
                                    restartApplication();
                                }
                            }
                        })).setCancelable(true).show();

                    } else {
                        SharedPreferences.Editor edit = user.edit();
                        edit.putString("country", country);

                        if (selected_language.equals("0"))
                            edit.putString("language", "ar");
                        else if (selected_language.equals("1"))
                            edit.putString("language", "en");

                        edit.commit();
                        restartApplication();
                    }
                } else {
                    Toast.makeText(SelectRegionActivity.this, R.string.select_region_toast, Toast.LENGTH_LONG).show();
                }
            }});
    }

    public void restartApplication() {
        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        System.exit(1);
    }

}
