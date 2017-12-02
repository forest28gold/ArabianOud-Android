package com.ecommerce.dell.mcommerce.activity.product;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.ecommerce.dell.mcommerce.R;
import com.ecommerce.dell.mcommerce.activity.payment.ShippingAddressActivity;
import com.ecommerce.dell.mcommerce.activity.login.LoginActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class CartActivity extends ActionBarActivity {

    private LinearLayout container;
    private LinearLayout bottomLayout;
    private TextView grandTotal;

    private int id = 1;
    private int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_cart);

        SharedPreferences ss1 = getSharedPreferences("item", 0);
        int itemsCount = ss1.getInt("count", 0) ;

        SharedPreferences setting = getSharedPreferences("setting", 0);
        String language = setting.getString("language", "");

        if (itemsCount > 0) {

            setContentView(R.layout.activity_cart);

            Button checkout = (Button) findViewById(R.id.checkout);
            grandTotal = (TextView) findViewById(R.id.grandTotal);
            container = (LinearLayout)findViewById(R.id.container);
            bottomLayout = (LinearLayout)findViewById(R.id.bottomLayout);
            boolean emptyCart = true;

            ArrayList<String> i = new ArrayList<>();
            for (int index = 1 ; index <= itemsCount ;index++ ){
                int id = ss1.getInt("id" + index, 0);
                if (id != 0) {
                    emptyCart = false;
                    String imagePath = ss1.getString("image" + id, "");
                    String title = ss1.getString("title" + id, "");
                    String price = ss1.getString("price"+id, "");
                    String amount = ss1.getString("amount"+id ,"" );
                    total +=  Float.valueOf(price) * Integer.parseInt(amount);
                    Log.d("Total","--"+total);

                    if (imagePath !="") {

                        if (language.equals("ar")) {
                            addItem_ar(imagePath, title, price, amount , id);
                        } else {
                            addItem(imagePath, title, price, amount, id);
                        }
                    }
                }
            }

            if (emptyCart){
                setContentView(R.layout.emptycart);
                Button explore = (Button) findViewById(R.id.explore);
                explore.setOnClickListener(exploreListener);
            } else {
                grandTotal.setText(String.valueOf(total) + " "+getString(R.string.main_activity_currency));
                bottomLayout.setVisibility(View.VISIBLE);
            }

            checkout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SharedPreferences s = getSharedPreferences("user" , 0);
                    String status = s.getString("status", "");

                    if (status.equals("1")) {
                        Intent i = new Intent(CartActivity.this, ShippingAddressActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Intent i = new Intent(CartActivity.this, LoginActivity.class);
                        i.putExtra("type","cart");
                        startActivity(i);
                    }
                }
            });

        } else {
            setContentView(R.layout.emptycart);
            Button explore = (Button) findViewById(R.id.explore);
            explore.setOnClickListener(exploreListener);
        }
    }


    public void addItem_ar(String imagePath , String titleString ,String priceString,String numString , final int numbersSelectorID) {

        final RelativeLayout content = new RelativeLayout(this);
        ImageView image = new ImageView(this);
        TextView title = new TextView(this);
        LinearLayout num = new LinearLayout(this);
        LinearLayout line = new LinearLayout(this);
        ImageView leftArrow = new ImageView(this);
        final TextView value = new TextView(this);
        ImageView rightArrow = new ImageView(this);

        try {

            TextView price = new TextView(this);
            ImageView delete = new ImageView(this);
            line.setBackgroundColor(Color.GRAY);

            LinearLayout.LayoutParams cotenetParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(pxToDp(70) , pxToDp(70));
            RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams numParams = new RelativeLayout.LayoutParams(pxToDp(75) , pxToDp(25));
            RelativeLayout.LayoutParams priceParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams deleteParams = new RelativeLayout.LayoutParams(pxToDp(20) , pxToDp(70));
            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,1);

            LinearLayout.LayoutParams arrowParams = new LinearLayout.LayoutParams(0 , LinearLayout.LayoutParams.MATCH_PARENT , 1f);
            LinearLayout.LayoutParams valueParams = new LinearLayout.LayoutParams(0 , LinearLayout.LayoutParams.MATCH_PARENT , 1f);

            line.setLayoutParams(lineParams);

            image.setId(id);
            ++id;
            title.setId(id);
            ++id;
            num.setId(id);
            ++id;
            price.setId(id);
            ++id;
            delete.setId(id);
            ++id;

            imageParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

            titleParams.addRule(RelativeLayout.RIGHT_OF, image.getId());
            titleParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

            numParams.addRule(RelativeLayout.RIGHT_OF, image.getId());
            numParams.addRule(RelativeLayout.BELOW, title.getId());

            priceParams.addRule(RelativeLayout.RIGHT_OF, num.getId());
            priceParams.addRule(RelativeLayout.BELOW, title.getId());

            deleteParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            deleteParams.setMargins(pxToDp(10), 0, 0, 0);

            cotenetParams.setMargins(0, 0, 0, pxToDp(1));
            content.setLayoutParams(cotenetParams);

            Picasso.with(this)
                    .load(imagePath)
                    .into(image);
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
            price.setText(priceString + " "+getString(R.string.main_activity_currency));
            price.setTextColor(Color.BLACK);
            price.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

            delete.setImageResource(R.drawable.delete2);
            delete.setAdjustViewBounds(true);
            delete.setLayoutParams(deleteParams);

            content.addView(image);
            content.addView(title);
            content.addView(num);
            content.addView(price);
            content.addView(delete);
            content.setBackgroundColor(Color.WHITE);
            content.setPadding(pxToDp(10), pxToDp(10), pxToDp(10), pxToDp(10));

            container.addView(content);
            container.addView(line);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AlertView(getString(R.string.DeleteTitle),
                            getString(R.string.cartDelete),
                            getString(R.string.DeleteNo),
                            new String[]{getString(R.string.DeleteYes)},
                            null, CartActivity.this, AlertView.Style.Alert, (new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            if(position != AlertView.CANCELPOSITION) {

                                deleteFromSharedpreferences(value.getId());
                                refillActivity();
                            }
                        }
                    })).setCancelable(true).show();
                }
            });

            num.setWeightSum(3);
            num.setOrientation(LinearLayout.HORIZONTAL);

            leftArrow.setImageResource(R.drawable.minus);
            leftArrow.setAdjustViewBounds(true);
            leftArrow.setLayoutParams(arrowParams);
            leftArrow.setBackgroundResource(R.drawable.leftborder);

            rightArrow.setImageResource(R.drawable.plus);
            rightArrow.setAdjustViewBounds(true);
            rightArrow.setLayoutParams(arrowParams);
            rightArrow.setBackgroundResource(R.drawable.rightborder);

            value.setLayoutParams(valueParams);
            value.setGravity(Gravity.CENTER);
            value.setText(numString);
            value.setBackgroundResource(R.drawable.border);
            value.setId(numbersSelectorID);

            num.addView(rightArrow);
            num.addView(value);
            num.addView(leftArrow);

            leftArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Integer.parseInt(value.getText().toString()) > 1) {
                        value.setText(Integer.toString(Integer.parseInt(value.getText().toString()) - 1));
                        modifySharedPreferences("amount" + value.getId(), value.getText().toString());
                        float price =Float.valueOf(getValueFromSharedpreferences("price" + value.getId()));
                        total -= price;
                        grandTotal.setText(total + " "+getString(R.string.main_activity_currency));
                    }
                }
            });

            rightArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Integer.parseInt(value.getText().toString()) < 10) {
                        value.setText(Integer.toString(Integer.parseInt(value.getText().toString()) + 1));
                        modifySharedPreferences("amount" + value.getId(), value.getText().toString());
                        float price =  Float.valueOf(getValueFromSharedpreferences("price"+value.getId()));
                        total += price;
                        grandTotal.setText(total+" "+getString(R.string.main_activity_currency));
                    }
                }
            });
        } catch (Exception ex){
            //Toast.makeText(getApplicationContext() ,"main  "+ ex.toString() , Toast.LENGTH_SHORT).show();
        }
    }

    public void addItem(String imagePath , String titleString ,String priceString,String numString , final int numbersSelectorID) {

        final RelativeLayout content = new RelativeLayout(this);
        ImageView image = new ImageView(this);
        TextView title = new TextView(this);
        LinearLayout num = new LinearLayout(this);
        LinearLayout line = new LinearLayout(this);
        ImageView leftArrow = new ImageView(this);
        final TextView value = new TextView(this);
        ImageView rightArrow = new ImageView(this);

        try {

            TextView price = new TextView(this);
            ImageView details = new ImageView(this);
            ImageView delete = new ImageView(this);
            line.setBackgroundColor(Color.GRAY);


            LinearLayout.LayoutParams cotenetParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(pxToDp(70) , pxToDp(70));
            RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams numParams = new RelativeLayout.LayoutParams(pxToDp(75) , pxToDp(25));
            RelativeLayout.LayoutParams priceParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            RelativeLayout.LayoutParams detailsParams = new RelativeLayout.LayoutParams(pxToDp(20) , pxToDp(70));
            RelativeLayout.LayoutParams deleteParams = new RelativeLayout.LayoutParams(pxToDp(20) , pxToDp(70));
            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,1);

            LinearLayout.LayoutParams arrowParams = new LinearLayout.LayoutParams(0 , LinearLayout.LayoutParams.MATCH_PARENT , 1f);
            LinearLayout.LayoutParams valueParams = new LinearLayout.LayoutParams(0 , LinearLayout.LayoutParams.MATCH_PARENT , 1f);

            line.setLayoutParams(lineParams);

            image.setId(id);
            ++id;
            title.setId(id);
            ++id;
            num.setId(id);
            ++id;
            price.setId(id);
            ++id;
            delete.setId(id);
            ++id;

            imageParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

            titleParams.addRule(RelativeLayout.RIGHT_OF, image.getId());
            titleParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

            numParams.addRule(RelativeLayout.RIGHT_OF, image.getId());
            numParams.addRule(RelativeLayout.BELOW, title.getId());

            priceParams.addRule(RelativeLayout.RIGHT_OF, num.getId());
            priceParams.addRule(RelativeLayout.BELOW, title.getId());

            detailsParams.setMargins(0, 0, 0, 0);
            detailsParams.addRule(RelativeLayout.LEFT_OF, delete.getId());


            deleteParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            deleteParams.setMargins(pxToDp(10), 0, 0, 0);


            cotenetParams.setMargins(0, 0, 0, pxToDp(1));
            content.setLayoutParams(cotenetParams);

            Picasso.with(this)
                    .load(imagePath)
                    .into(image);
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
            price.setText(priceString + " "+getString(R.string.main_activity_currency));
            price.setTextColor(Color.BLACK);
            price.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

            details.setAdjustViewBounds(true);
            details.setLayoutParams(detailsParams);

            delete.setImageResource(R.drawable.delete2);
            delete.setAdjustViewBounds(true);
            delete.setLayoutParams(deleteParams);

            content.addView(image);
            content.addView(title);
            content.addView(num);
            content.addView(price);
            content.addView(delete);
            content.addView(details);
            content.setBackgroundColor(Color.WHITE);
            content.setPadding(pxToDp(10), pxToDp(10), pxToDp(10), pxToDp(10));


            container.addView(content);
            container.addView(line);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AlertView(getString(R.string.DeleteTitle),
                            getString(R.string.cartDelete),
                            getString(R.string.DeleteNo),
                            new String[]{getString(R.string.DeleteYes)},
                            null, CartActivity.this, AlertView.Style.Alert, (new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            if(position != AlertView.CANCELPOSITION) {

                                deleteFromSharedpreferences(value.getId());
                                refillActivity();
                            }
                        }
                    })).setCancelable(true).show();

                }
            });

            num.setWeightSum(3);
            num.setOrientation(LinearLayout.HORIZONTAL);

            leftArrow.setImageResource(R.drawable.minus);
            leftArrow.setAdjustViewBounds(true);
            leftArrow.setLayoutParams(arrowParams);
            leftArrow.setBackgroundResource(R.drawable.leftborder);

            rightArrow.setImageResource(R.drawable.plus);
            rightArrow.setAdjustViewBounds(true);
            rightArrow.setLayoutParams(arrowParams);
            rightArrow.setBackgroundResource(R.drawable.rightborder);

            value.setLayoutParams(valueParams);
            value.setGravity(Gravity.CENTER);
            value.setText(numString);
            value.setBackgroundResource(R.drawable.border);
            value.setId(numbersSelectorID);

            num.addView(leftArrow);
            num.addView(value);
            num.addView(rightArrow);

            leftArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Integer.parseInt(value.getText().toString()) > 1){
                        value.setText(Integer.toString(Integer.parseInt(value.getText().toString()) - 1));
                        modifySharedPreferences("amount" + value.getId(), value.getText().toString());
                        float price =Float.valueOf(getValueFromSharedpreferences("price" + value.getId()));
                        total -= price;
                        grandTotal.setText(total + " "+getString(R.string.main_activity_currency));
                    }

                }
            });

            rightArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Integer.parseInt(value.getText().toString()) < 10){
                        value.setText(Integer.toString(Integer.parseInt(value.getText().toString()) + 1));
                        modifySharedPreferences("amount" + value.getId(), value.getText().toString());
                        float price =  Float.valueOf(getValueFromSharedpreferences("price"+value.getId()));
                        total += price;
                        grandTotal.setText(total+" "+getString(R.string.main_activity_currency));
                    }
                }
            });

        } catch (Exception ex){
            //Toast.makeText(getApplicationContext() ,"main  "+ ex.toString() , Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteFromSharedpreferences(int id) {

        SharedPreferences ss1 = getSharedPreferences("item" , 0);
        for (int x=1 ; x<= ss1.getInt("count" , 0) ; x++){
            if (ss1.getInt("id"+x , 0) == id){

                SharedPreferences.Editor editor = ss1.edit();
                editor.remove("id" + x);
                editor.remove("title" + id);
                editor.remove("image" + id);
                editor.remove("price" + id);
                editor.remove("amount" + id);

                editor.apply();
                editor.commit();
                break;
            }
        }
    }


    public void refillActivity() {

        SharedPreferences ss1 = getSharedPreferences("item", 0);
        SharedPreferences.Editor edit = ss1.edit();
        int itemsCount = ss1.getInt("count", 0);
        int totalPrice = 0;
        total=0;

        container.removeAllViews();
        boolean emptyCart = true;

        try {
            ArrayList<String> i = new ArrayList<>();
            for (int index = 1; index <= itemsCount; index++) {

                int idPreference = ss1.getInt("id" + index, 0);

                if (idPreference != 0) {
                    emptyCart = false;
                    String imagePath = ss1.getString("image" + idPreference, "");
                    String title = ss1.getString("title" + idPreference, "");
                    String price = ss1.getString("price" + idPreference, "");
                    String amount = ss1.getString("amount" + idPreference, "");

                    try {
                        totalPrice += (Float.valueOf(price) * Integer.parseInt(amount));
                        total = totalPrice;
                        Log.d("Total Price",String.valueOf(totalPrice)+"Total variable "+String.valueOf(total));

                    } catch(NumberFormatException ex){
                        System.err.println("Ilegal input");
                    }
                    if (imagePath != "") {
                        addItem(imagePath, title, price, amount, idPreference);
                    }
                }
            }

            if (emptyCart) {
                setContentView(R.layout.emptycart);
                Button explore = (Button) findViewById(R.id.explore);
                explore.setOnClickListener(exploreListener);
            } else {
                grandTotal.setText(String.valueOf(totalPrice) + " "+getString(R.string.main_activity_currency));

                Log.d("totalAfterDelete",String.valueOf(total));
                bottomLayout.setVisibility(View.VISIBLE);
            }
        } catch (Exception ex) {
            // Toast.makeText(getApplicationContext(), "fill : " + ex.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    public void modifySharedPreferences(String key , String value) {
        SharedPreferences ss = getSharedPreferences("item", 0);
        SharedPreferences.Editor edit = ss.edit();
        edit.putString(key , value);
        edit.commit();
    }

    public String getValueFromSharedpreferences(String key) {
        SharedPreferences ss = getSharedPreferences("item", 0);
        String value = ss.getString(key, "");
        return value;
    }

    public int pxToDp(int px) {
        final float scale = getBaseContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (px * scale + 0.5f);
        return pixels;
    }

    View.OnClickListener exploreListener =  new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

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