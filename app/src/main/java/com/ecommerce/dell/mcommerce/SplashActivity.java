package com.ecommerce.dell.mcommerce;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.ecommerce.dell.mcommerce.activity.MainActivity;
import com.ecommerce.dell.mcommerce.activity.SelectRegionActivity;
import com.ecommerce.dell.mcommerce.models.Global;
import com.ecommerce.dell.mcommerce.utility.DBService;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Global.dbService = new DBService(this);

        SharedPreferences setting = getSharedPreferences("setting", 0);
        final String language = setting.getString("language", "");

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = null;
                if (language.equals("ar")) {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else if (language.equals("en")) {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else if (language.equals("")) {
                    intent = new Intent(SplashActivity.this, SelectRegionActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 1000);
    }
}