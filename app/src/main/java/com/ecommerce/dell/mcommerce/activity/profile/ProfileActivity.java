package com.ecommerce.dell.mcommerce.activity.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.ecommerce.dell.mcommerce.R;

import java.net.URLDecoder;

public class ProfileActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_profile);

        TextView myProfile = (TextView) findViewById(R.id.storedAddress);
        TextView orders = (TextView) findViewById(R.id.orders);
        TextView logout = (TextView) findViewById(R.id.logout);
        TextView email = (TextView) findViewById(R.id.email);
        TextView telephone = (TextView) findViewById(R.id.birthdate);
        TextView address = (TextView) findViewById(R.id.gender);
        TextView name = (TextView) findViewById(R.id.name);

        SharedPreferences s = getSharedPreferences("user" , 0);
        String fnameString = s.getString("firstname", "");
        String snameString = s.getString("lastname", "");
        final String emailString = s.getString("email" , "");
        String telephoneString = s.getString("telephone" , "");
        String addressString = URLDecoder.decode(s.getString("address" , ""));

        name.setText(fnameString + " "+ snameString);
        email.setText(email.getText().toString() + "          " + emailString);
        address.setText(address.getText().toString() + "      " + addressString);
        telephone.setText(telephone.getText().toString() + "   " + telephoneString);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertView(getString(R.string.alert),
                        getString(R.string.profile_logout_dialog),
                        getString(R.string.DeleteNo),
                        new String[]{getString(R.string.DeleteYes)},
                        null, ProfileActivity.this, AlertView.Style.Alert, (new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        if(position != AlertView.CANCELPOSITION) {

                            SharedPreferences s = getSharedPreferences("user", 0);
                            SharedPreferences.Editor edit = s.edit();

                            edit.clear();
                            edit.putString("status", "0");
                            edit.commit();

                            finish();
                        }
                    }
                })).setCancelable(true).show();
            }
        });

        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(ProfileActivity.this, ProfileShippingAdressesActivity.class);
                startActivity(myIntent);
            }
        });

        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences order = getSharedPreferences("order" ,0);
                if ( order.getInt("count" , 0) > 0){
                    Intent orderdetails = new Intent(ProfileActivity.this , OrdersActivity.class);
                    startActivity(orderdetails);
                } else
                    Toast.makeText(getBaseContext() , getResources().getString(R.string.noOrderFound) , Toast.LENGTH_SHORT).show();
            }
        });
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
