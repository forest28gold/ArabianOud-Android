package com.ecommerce.dell.mcommerce.activity.payment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ecommerce.dell.mcommerce.R;

public class PaymentConfirmationActivity extends ActionBarActivity {

    TextView ordernumber;
    String num, date, shipping, total, strEmail, username, address, telephone, paymentMethod, discount, discountPercent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_confirmation);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_payment_confirmation);

        ordernumber = (TextView)findViewById(R.id.ordernumber);
        ordernumber.setText(ordernumber.getText() + " " + getIntent().getStringExtra("orderID"));

        num = getIntent().getStringExtra("orderID");
        date = getIntent().getStringExtra("orderDate");
        shipping = getIntent().getStringExtra("shipping");
        total = getIntent().getStringExtra("totalCost");
        strEmail = getIntent().getStringExtra("email");
        username = getIntent().getStringExtra("shipmentUsername");
        address = getIntent().getStringExtra("shipmentAddress");
        telephone = getIntent().getStringExtra("shipmentTelephone");
        paymentMethod = getIntent().getStringExtra("paymentMethod");
        discount = getIntent().getStringExtra("discount");
        discountPercent = getIntent().getStringExtra("discountPercent");

        if (getIntent().getStringExtra("orderID" ).equals("The requested Payment Method is not available.")){
            Toast.makeText(this,getResources().getString(R.string.NetworkError),Toast.LENGTH_LONG).show();
        }

        Button paymentHome = (Button) findViewById(R.id.paymentHome);
        Button orderDetails = (Button) findViewById(R.id.orderDetails);

        paymentHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        orderDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentConfirmationActivity.this , OrderDetailsActivity.class);
                intent.putExtra("orderID", num);
                intent.putExtra("orderDate", date);
                intent.putExtra("shipping", shipping);
                intent.putExtra("totalCost", total);
                intent.putExtra("email", strEmail);
                intent.putExtra("shipmentUsername", username);
                intent.putExtra("shipmentAddress", address);
                intent.putExtra("shipmentTelephone", telephone);
                intent.putExtra("paymentMethod", paymentMethod);
                intent.putExtra("discount", discount);
                intent.putExtra("discountPercent", discountPercent);
                startActivity(intent);
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
