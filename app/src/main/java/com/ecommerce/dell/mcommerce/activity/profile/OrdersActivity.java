package com.ecommerce.dell.mcommerce.activity.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecommerce.dell.mcommerce.R;
import com.ecommerce.dell.mcommerce.activity.payment.OrderDetailsActivity;

public class OrdersActivity extends ActionBarActivity {

    private LinearLayout parent;
    String strUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_order_details);

        parent = (LinearLayout) findViewById(R.id.parent);

        SharedPreferences ss = getSharedPreferences("user", 0);
        strUserEmail = ss.getString("email", "");

        SharedPreferences order = getSharedPreferences("order" ,0);
        String num;
        String total;
        String date;
        String ship;
        String email;
        String username;
        String address;
        String telephone;
        String paymentMethod;
        String couponName;
        String discountPercent;

        int count = order.getInt("count" ,0);

        for (int x = 0; x < count; x++) {

            num = order.getString("order"+(x+1), "");
            total = order.getString("total"+(x+1), "");
            date = order.getString("date"+(x+1), "");
            ship = order.getString("ship"+(x+1), "");
            email = order.getString("email"+(x+1), "");
            username = order.getString("shipmentname"+(x+1), "");
            address = order.getString("shipmentaddress"+(x+1), "");
            telephone = order.getString("shipmenttelephone"+(x+1), "");
            paymentMethod = order.getString("paymentmethod"+(x+1), "");
            couponName = order.getString("discount"+(x+1), "");
            discountPercent = order.getString("discountPercent"+(x+1), "0");

            if (ship.equals("Free")) {
                ship = getResources().getString(R.string.orderfree);
            } else {
                ship = ship + " " + getResources().getString(R.string.main_activity_currency);
            }

            if (strUserEmail.equals(email)) {
                addItem(num, date, ship, total, username, address, telephone, paymentMethod, couponName, discountPercent);
            }
        }
    }

    public int pxToDp(int px) {
        final float scale = getBaseContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (px * scale + 0.5f);

        return pixels;
    }

    public void addItem(final String num , final String date , final String shipping , final String totlal, final String username, final String address, final String telephone, final String paymentMethod, final String strCouponName, final String strDiscountPercent) {

        LinearLayout container = new LinearLayout(this);
        LinearLayout container1 = new LinearLayout(this);
        LinearLayout container2 = new LinearLayout(this);
        LinearLayout container3 = new LinearLayout(this);
        LinearLayout container4 = new LinearLayout(this);

        TextView ordNum = new TextView(this);
        TextView ordAdd = new TextView(this);
        TextView ordShip = new TextView(this);
        TextView total = new TextView(this);

        TextView ordNum1 = new TextView(this);
        TextView ordAdd1 = new TextView(this);
        TextView ordShip1 = new TextView(this);
        TextView total1 = new TextView(this);

        LinearLayout.LayoutParams containerLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT , LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams containerLayoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT , LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams containerLayoutParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT , LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams containerLayoutParams3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT , LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams containerLayoutParams4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT , LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams ordNumLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams ordAddLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams ordShipLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams totalLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT , LinearLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout.LayoutParams ordNumLayoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT , LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams ordAddLayoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT , LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams ordShipLayoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT , LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams totalLayoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT , LinearLayout.LayoutParams.WRAP_CONTENT);

        container.setLayoutParams(containerLayoutParams);
        container.setBackgroundColor(Color.WHITE);
        containerLayoutParams.setMargins(0, 0, 0, pxToDp(10));

        container1.setLayoutParams(containerLayoutParams1);
        container2.setLayoutParams(containerLayoutParams2);
        container3.setLayoutParams(containerLayoutParams3);
        container4.setLayoutParams(containerLayoutParams4);

        containerLayoutParams1.setMargins(pxToDp(5), pxToDp(5), pxToDp(5), pxToDp(5));
        containerLayoutParams2.setMargins(pxToDp(5),pxToDp(5),pxToDp(5),pxToDp(5));
        containerLayoutParams3.setMargins(pxToDp(5),pxToDp(5),pxToDp(5),pxToDp(5));
        containerLayoutParams4.setMargins(pxToDp(5),pxToDp(5),pxToDp(5),pxToDp(5) );

        ordNum.setLayoutParams(ordNumLayoutParams);
        ordAdd.setLayoutParams(ordAddLayoutParams);
        ordShip.setLayoutParams(ordShipLayoutParams);
        total.setLayoutParams(totalLayoutParams);

        ordNum1.setLayoutParams(ordNumLayoutParams1);
        ordAdd1.setLayoutParams(ordAddLayoutParams1);
        ordShip1.setLayoutParams(ordShipLayoutParams1);
        total1.setLayoutParams(totalLayoutParams1);

        ordNum.setText(getResources().getString(R.string.orderNo) + " ");
        ordNum.setTextColor(Color.WHITE);
        ordAdd.setText(getResources().getString(R.string.orderDate) + " ");
        ordShip.setText(getResources().getString(R.string.orderShipping) + " ");
        total.setText(getResources().getString(R.string.ordertotal) + " ");

        ordNum1.setText(num);
        ordNum1.setTextColor(Color.WHITE);
        ordAdd1.setText(date);
        ordShip1.setText(shipping);
        total1.setText(totlal + " " + getResources().getString(R.string.main_activity_currency));

        ordAdd1.setTextColor(Color.BLACK);
        ordShip1.setTextColor(Color.BLACK);
        total1.setTextColor(Color.BLACK);

        container.setOrientation(LinearLayout.VERTICAL);
        container1.setOrientation(LinearLayout.HORIZONTAL);
        container1.setPadding(pxToDp(5) ,pxToDp(5) ,pxToDp(5) ,pxToDp(5) );
        container2.setOrientation(LinearLayout.HORIZONTAL);
        container3.setOrientation(LinearLayout.HORIZONTAL);
        container4.setOrientation(LinearLayout.HORIZONTAL);

        container1.setBackgroundColor(Color.parseColor("#585858"));

        container.addView(container1);
        container.addView(container2);
        container.addView(container3);
        container.addView(container4);

        container1.addView(ordNum);
        container1.addView(ordNum1);

        container2.addView(ordAdd);
        container2.addView(ordAdd1);

        container3.addView(ordShip);
        container3.addView(ordShip1);

        container4.addView(total);
        container4.addView(total1);

        parent.addView(container, 0);

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrdersActivity.this , OrderDetailsActivity.class);
                intent.putExtra("orderID", num);
                intent.putExtra("orderDate", date);
                intent.putExtra("shipping", shipping);
                intent.putExtra("totalCost", totlal);
                intent.putExtra("email", strUserEmail);
                intent.putExtra("shipmentUsername", username);
                intent.putExtra("shipmentAddress", address);
                intent.putExtra("shipmentTelephone", telephone);
                intent.putExtra("paymentMethod", paymentMethod);
                intent.putExtra("discount", strCouponName);
                intent.putExtra("discountPercent", strDiscountPercent);
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
