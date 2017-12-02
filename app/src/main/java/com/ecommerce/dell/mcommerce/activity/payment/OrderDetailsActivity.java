package com.ecommerce.dell.mcommerce.activity.payment;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ecommerce.dell.mcommerce.R;
import com.ecommerce.dell.mcommerce.models.Global;
import com.ecommerce.dell.mcommerce.models.OrderData;
import com.squareup.picasso.Picasso;

public class OrderDetailsActivity extends ActionBarActivity {

    LinearLayout item;
    TextView txt_sub_total;
    String num, date, shipping, total, strEmail, username, address, telephone, paymentMethod, discount, discountPercent;
    int id = 1;
    float sub = 0;
    float subTotal = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_order_details);

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

        item = (LinearLayout) findViewById(R.id.item);

        TextView txt_order_number = (TextView)findViewById(R.id.textView_order_number);
        TextView txt_order_date = (TextView)findViewById(R.id.textView_order_date);
        TextView txt_payment_method = (TextView)findViewById(R.id.textView_payment_method);
        TextView txt_user_name = (TextView)findViewById(R.id.user);
        TextView txt_address = (TextView)findViewById(R.id.address);
        TextView txt_telephone = (TextView)findViewById(R.id.telephone);
        txt_sub_total = (TextView)findViewById(R.id.subtotal);
        TextView txt_shipping = (TextView)findViewById(R.id.shipping);
        TextView txt_grand_total = (TextView)findViewById(R.id.grandtotal);
        TextView txt_discount = (TextView)findViewById(R.id.discounttext);
        TextView txt_discount_total = (TextView)findViewById(R.id.discounttotal);

        txt_order_number.setText(getResources().getString(R.string.txt_order_number) + "  " + num);
        txt_order_date.setText(getResources().getString(R.string.txt_order_date) + "  " + date);
        txt_payment_method.setText(getResources().getString(R.string.txt_payment_method) + "   " + onFormattedPaymentMethod(paymentMethod));

        txt_user_name.setText(username);
        txt_address.setText(address);
        txt_telephone.setText(telephone);

        if (shipping.equals("Free")) {
            shipping = getResources().getString(R.string.orderfree);
        }

        txt_shipping.setText(shipping);
        txt_grand_total.setText(total + " " + getResources().getString(R.string.main_activity_currency));

        onCheckUserDBData(num, strEmail);

        if (discount == null) {
            txt_discount.setText(getString(R.string.str_discount));
            txt_discount_total.setText("- 0.00" + " " + getResources().getString(R.string.main_activity_currency));
        } else if (discount.equals("")) {
            txt_discount.setText(getString(R.string.str_discount));
            txt_discount_total.setText("- 0.00" + " " + getResources().getString(R.string.main_activity_currency));
        } else {
            float discountCost = subTotal * (Float.valueOf(discountPercent) / 100);
            txt_discount.setText((getString(R.string.str_discount) + " (" + discount + ")"));
            txt_discount_total.setText("- " + String.format("%.02f", discountCost) + " " + getResources().getString(R.string.main_activity_currency));
        }
    }

    public void onCheckUserDBData(String orderID, String strEmail) {

        int count = 0;

        String sql = "select * from " + Global.LOCAL_TABLE_ORDERS + " where " + Global.LOCAL_FIELD_ORDER_ID + "='" + orderID + "' and " + Global.LOCAL_FIELD_EMAIL+ "='" + strEmail + "'";
        Cursor cursor = Global.dbService.query(sql, null);
        startManagingCursor(cursor);
        if (cursor != null) {
            int nums = cursor.getCount();
            if (nums > 0) {
                while (cursor.moveToNext()) {

                    count++;

                    String strOrderID = cursor.getString(1);
                    String strUserEmail = cursor.getString(2);
                    String strProductName = cursor.getString(3);
                    String strProductAmount = cursor.getString(4);
                    String strProductSKU = cursor.getString(5);
                    String strProductPrice = cursor.getString(6);
                    String strProductImage = cursor.getString(7);

                    OrderData orderData = new OrderData();
                    orderData.setOrderID(strOrderID);
                    orderData.setEmail(strUserEmail);
                    orderData.setProductName(strProductName);
                    orderData.setProductAmount(strProductAmount);
                    orderData.setProductSKU(strProductSKU);
                    orderData.setProductPrice(strProductPrice);
                    orderData.setProductImage(strProductImage);

                    subTotal += Float.valueOf(strProductAmount) * Float.valueOf(strProductPrice);

                    addItem(strProductImage, strProductName, strProductPrice, strProductAmount, count, count);
                }

                txt_sub_total.setText(String.format("%.02f", subTotal) + " " + getResources().getString(R.string.main_activity_currency));

                stopManagingCursor(cursor);
                cursor.close();
                return;
            }
        }
    }

    public String onFormattedPaymentMethod(String strMethod) {

        if (strMethod.equals(Global.PAYMENT_CASH)) {
            strMethod = getResources().getString(R.string.payment_cash);
        } else if (strMethod.equals(Global.PAYMENT_BANK)) {
            strMethod = getResources().getString(R.string.payment_bank);
        } else if (strMethod.equals(Global.PAYMENT_PAYPAL)) {
            strMethod = getResources().getString(R.string.payment_paypal);
        } else if (strMethod.equals(Global.PAYMENT_VISA_CARD)) {
            strMethod = getResources().getString(R.string.payment_card_visa);
        } else if (strMethod.equals(Global.PAYMENT_MASTER_CARD)) {
            strMethod = getResources().getString(R.string.payment_card_master);
        }
        return strMethod;
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

    public int pxToDp(int px) {
        final float scale = getBaseContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (px * scale + 0.5f);

        return pixels;
    }

    public void addItem(String imagePath, String titleString, String priceString, String numString, final int numbersSelectorID, int shipmentNumber) {

        final RelativeLayout content = new RelativeLayout(this);
        final ImageView image = new ImageView(this);
        TextView title = new TextView(this);
        LinearLayout num = new LinearLayout(this);
        LinearLayout line = new LinearLayout(this);
        ImageView leftArrow = new ImageView(this);
        final TextView value = new TextView(this);
        ImageView rightArrow = new ImageView(this);

        TextView shipmentNum = new TextView(this);
        LinearLayout shipItem = new LinearLayout(this);
        LinearLayout deliveryTaxes = new LinearLayout(this);
        TextView delivery = new TextView(this);
        TextView taxes = new TextView(this);
        LinearLayout shippinglayout = new LinearLayout(this);

        LinearLayout.LayoutParams shipItemParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams deliveryTaxesParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams shippinglayoutparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams deliveryParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams taxesParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        delivery.setGravity(Gravity.CENTER_VERTICAL);
        delivery.setPadding(pxToDp(10), pxToDp(10), pxToDp(10), pxToDp(10));
        taxes.setPadding(pxToDp(10), pxToDp(10), pxToDp(10), pxToDp(10));
        shipItemParams.setMargins(0, 0, 0, pxToDp(15));

        shipItem.setLayoutParams(shipItemParams);
        deliveryTaxes.setLayoutParams(deliveryTaxesParams);
        shippinglayout.setLayoutParams(shippinglayoutparams);
        delivery.setLayoutParams(deliveryParams);
        taxes.setLayoutParams(taxesParams);

        shipItem.setOrientation(LinearLayout.VERTICAL);
        shipItem.setPadding(0, 0, 0, pxToDp(6));
        deliveryTaxes.setOrientation(LinearLayout.HORIZONTAL);
        shippinglayout.setOrientation(LinearLayout.HORIZONTAL);

        deliveryTaxes.addView(delivery);
        deliveryTaxes.addView(taxes);
        shipItem.addView(shippinglayout);
        shipItem.addView(content);

        shipItem.setBackgroundColor(Color.parseColor("#d7d6d5"));

        delivery.setText(R.string.checkout_standard_delivery);
        delivery.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

        if (sub > 200.0) {
            taxes.setText(getResources().getString(R.string.taxes));
        } else {
            taxes.setText("20.00 " + getString(R.string.main_activity_currency));
        }

        taxes.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        taxes.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

        content.setBackgroundColor(Color.WHITE);


        try {

            TextView price = new TextView(this);
            ImageView details = new ImageView(this);
            final ImageView delete = new ImageView(this);
            line.setBackgroundColor(Color.GRAY);


            LinearLayout.LayoutParams cotenetParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(pxToDp(60), pxToDp(60));
            RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams numParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            RelativeLayout.LayoutParams priceParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams detailsParams = new RelativeLayout.LayoutParams(pxToDp(20), pxToDp(70));
            RelativeLayout.LayoutParams deleteParams = new RelativeLayout.LayoutParams(pxToDp(20), pxToDp(70));
            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
            LinearLayout.LayoutParams shipmentNumParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


            LinearLayout.LayoutParams arrowParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
            LinearLayout.LayoutParams valueParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);


            line.setLayoutParams(lineParams);


            image.setId(id);
            ++id;
            title.setId(id);
            ++id;
            num.setId(id);
            ++id;
            price.setId(id);
            ++id;
            details.setId(id);
            ++id;
            delete.setId(id);
            ++id;


            shipmentNum.setLayoutParams(shipmentNumParams);
            shipmentNum.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            shipmentNum.setText(getString(R.string.checkout_shipment) + " " + shipmentNumber + "");
            item.addView(shipmentNum);

            imageParams.addRule(RelativeLayout.RIGHT_OF, num.getId());

            titleParams.addRule(RelativeLayout.RIGHT_OF, image.getId());
            titleParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

            numParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

            priceParams.addRule(RelativeLayout.RIGHT_OF, image.getId());
            priceParams.addRule(RelativeLayout.BELOW, title.getId());


            valueParams.gravity = Gravity.CENTER;

            cotenetParams.setMargins(0, 0, 0, pxToDp(1));
            content.setLayoutParams(cotenetParams);

            Context context = this;
            Picasso.with(context).load(imagePath).into(image);
            image.setAdjustViewBounds(true);
            imageParams.setMargins(0, 0, pxToDp(10), 0);
            image.setLayoutParams(imageParams);

            title.setLayoutParams(titleParams);
            title.setText(titleString);
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            title.setPadding(0, 0, 0, pxToDp(10));
            title.setMaxLines(1);
            title.setEllipsize(TextUtils.TruncateAt.END);

            numParams.setMargins(0, 0, pxToDp(10) + pxToDp(10), 0);
            num.setLayoutParams(numParams);

            price.setLayoutParams(priceParams);
            price.setText(priceString + " " + getString(R.string.main_activity_currency));
            price.setTextColor(Color.BLACK);

            price.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);


            content.addView(image);
            content.addView(title);
            content.addView(num);
            content.addView(price);

            content.setBackgroundColor(Color.WHITE);
            content.setPadding(pxToDp(10), pxToDp(10), pxToDp(10), pxToDp(10));


            item.addView(shipItem);


            num.setOrientation(LinearLayout.HORIZONTAL);

            value.setLayoutParams(valueParams);
            value.setGravity(Gravity.CENTER_VERTICAL);
            value.setText(numString + "x");
            value.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            value.setId(numbersSelectorID);

            num.addView(value);

        } catch (Exception ex) {

        }

    }
}
