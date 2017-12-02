package com.ecommerce.dell.mcommerce.activity.product;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.ecommerce.dell.mcommerce.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class WishListActivity extends ActionBarActivity {

    private LinearLayout content;

    private ArrayList<String> images = new ArrayList<>();
    private ArrayList<ArrayList<String>> details =  new ArrayList<>();
    private int globalID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_wish_list);

        SharedPreferences ss1 = getSharedPreferences("wishlist", 0);
        int itemsCount = ss1.getInt("count", 0) ;

        if (itemsCount > 0) {

            setContentView(R.layout.activity_wish_list);

            content = (LinearLayout)findViewById(R.id.content);
            boolean emptyCart = true;

            try {
                for (int index = 1 ; index <= itemsCount ;index++ ){

                    int id = ss1.getInt("id" + index, 0);

                    if (id != 0) {

                        emptyCart = false;
                        final String imagePath = ss1.getString("image" + id, "");
                        images.add(imagePath);
                        final String title = ss1.getString("title" + id, "");
                        final String price = ss1.getString("price"+id, "");
                        final String cartTitle = ss1.getString("cartTitle"+id, "");
                        final String ids = String.valueOf(id);
                        details.add(new ArrayList<String>(){{add(title);add(price);add(ids);add(cartTitle);}});

                    }

                }

                if (emptyCart){
                    setContentView(R.layout.emptywishlist);
                    Button explore = (Button) findViewById(R.id.explore);
                    explore.setOnClickListener(exploreListener);
                } else {
                    fillActivity(details, images);
                }

            } catch (Exception ex){
                Toast.makeText(getApplicationContext(), "oncreate : " + ex.toString(), Toast.LENGTH_SHORT).show();
            }
        } else {
            setContentView(R.layout.emptywishlist);
            Button explore = (Button) findViewById(R.id.explore);
            explore.setOnClickListener(exploreListener);
        }

    }

    public void fillActivity(ArrayList<ArrayList<String>> productsData , final ArrayList<String> productsImages) {

        try {
            content.removeAllViews();

            LinearLayout row  = new LinearLayout(this);
            LinearLayout block = new LinearLayout(this);
            LinearLayout deleteAndCart = new LinearLayout(this);
            LinearLayout line = new LinearLayout(this);
            ImageView image;
            TextView comment;
            TextView price ;
            ImageView cart  , delete ;

            LinearLayout.LayoutParams rowLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT ,LinearLayout.LayoutParams.WRAP_CONTENT  );
            row.setWeightSum(2f);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setLayoutParams(rowLayout);

            LinearLayout.LayoutParams blockLayout = new LinearLayout.LayoutParams(0 ,LinearLayout.LayoutParams.FILL_PARENT , 1f );
            blockLayout.setMargins(5, 5, 5, 5);
            block.setBackgroundColor(Color.WHITE);

            LinearLayout.LayoutParams deleteAndCartLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT ,pxToDp(50));
            deleteAndCart.setWeightSum(2f);
            deleteAndCart.setOrientation(LinearLayout.HORIZONTAL);
            deleteAndCart.setLayoutParams(deleteAndCartLayout);

            LinearLayout.LayoutParams cartLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT , 1f );
            cartLayout.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
            cartLayout.setMargins(0,pxToDp(15), 0, pxToDp(15));

            LinearLayout.LayoutParams deleteLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT ,1f );
            deleteLayout.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
            deleteLayout.setMargins(0,pxToDp(15), 0, pxToDp(15));

            LinearLayout.LayoutParams lineLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT ,1  );
            line.setOrientation(LinearLayout.HORIZONTAL);
            lineLayout.setMargins(0,pxToDp(10),0,0);
            line.setLayoutParams(lineLayout);
            line.setBackgroundColor(Color.GRAY);

            content.addView(row);

            for (int x = 0; x < productsData.size(); x++) {

                String bitmapImage =  productsImages.get(x);
                final String stringComment = productsData.get(x).get(0)+"\n";
                final String stringPrice = productsData.get(x).get(1);
                final String stringCartTitle = productsData.get(x).get(3);


                block = new LinearLayout(this);
                block.setOrientation(LinearLayout.VERTICAL);
                block.setLayoutParams(blockLayout);
                blockLayout.gravity = Gravity.FILL_VERTICAL;
                block.setBackgroundColor(Color.WHITE);


                image = new ImageView(this);
                image.setAdjustViewBounds(true);

                comment = new TextView(this);
                comment.setGravity(Gravity.CENTER | Gravity.BOTTOM);
                comment.setBackgroundColor(Color.WHITE);
                comment.setMaxLines(2);
                comment.setEllipsize(TextUtils.TruncateAt.END);
                comment.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                comment.setPadding(2, 20, 2, 0);

                price = new TextView(this);
                price.setGravity(Gravity.CENTER | Gravity.BOTTOM);
                price.setTypeface(null, Typeface.BOLD);
                price.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                price.setTextColor(Color.BLACK);

                cart = new ImageView(this);
                cart.setLayoutParams(cartLayout);
                cart.setId(x);
                cart.setImageResource(R.drawable.graycart);
                cart.setAdjustViewBounds(true);

                delete = new ImageView(this);
                delete.setLayoutParams(deleteLayout);
                delete.setId(x + 1);
                delete.setImageResource(R.drawable.deleteitem);
                delete.setAdjustViewBounds(true);

                deleteAndCart = new LinearLayout(this);
                deleteAndCart.setLayoutParams(deleteAndCartLayout);

                deleteAndCart.addView(cart);
                deleteAndCart.addView(delete);

                line = new LinearLayout(this);
                line.setLayoutParams(lineLayout);
                line.setBackgroundColor(Color.GRAY);

                if (row.getChildCount() > 1){
                    row = new LinearLayout(this);
                    row.setWeightSum(2f);
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    row.setLayoutParams(rowLayout);
                    content.addView(row);

                }

                Picasso.with(this).load(bitmapImage).into(image);
                final  String finalBitmap = bitmapImage;

                globalID = Integer.parseInt(productsData.get(x).get(2));
                image.setId(globalID);
                comment.setText(stringComment);
                price.setText(stringPrice + " " + getString(R.string.main_activity_currency));

                block.addView(image);
                block.addView(comment);
                block.addView(price);
                block.addView(line);
                block.addView(deleteAndCart);

                row.addView(block);

                final int deleteID = image.getId();

                cart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteFromSharedpreferences(deleteID);
                        refillActivity();

                        saveInSharedPreference("item", finalBitmap , stringComment , stringPrice, "1", deleteID);
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.itemaddedTocart), Toast.LENGTH_SHORT).show();
                    }
                });

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new AlertView(getString(R.string.DeleteTitle),
                                getString(R.string.cartDelete),
                                getString(R.string.DeleteNo),
                                new String[]{getString(R.string.DeleteYes)},
                                null, WishListActivity.this, AlertView.Style.Alert, (new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, int position) {
                                if(position != AlertView.CANCELPOSITION) {

                                    deleteFromSharedpreferences(deleteID);
                                    refillActivity();
                                }
                            }
                        })).setCancelable(true).show();
                    }
                });
            }

        }catch (Exception ex){
            Toast.makeText(getApplicationContext() , ex.toString() , Toast.LENGTH_SHORT).show();
        }

    }

    public void saveInSharedPreference( String sharedPreferencesName , String imageBitmap , String title , String price , String amount , int id) {

        String imagePath = imageBitmap;

        boolean itemExists = false;
        //save data to shared preferences
        SharedPreferences ss = getSharedPreferences(sharedPreferencesName, 0);
        SharedPreferences.Editor edit = ss.edit();
        int itemsCount = ss.getInt("count", 0);

        if(itemsCount == 0){
            ++itemsCount;
            edit.putString("image" + id, imagePath);
            edit.putString("title"+id, title);
            edit.putString("price"+id, price);
            edit.putString("amount" + id, amount);
            edit.putInt("id" + itemsCount, id);
            edit.putInt("count", itemsCount);
            edit.commit();
        } else {
            for (int index = 1; index <= itemsCount ; index++) {
                int ids = ss.getInt("id"+index, 0);
                if (ids == id){
                    edit.putString("amount" + id, String.valueOf(Integer.parseInt(ss.getString("amount" + id, ""))+1));
                    edit.commit();
                    itemExists = true;
                    break;
                }
            }

            if (!itemExists){
                ++itemsCount;
                edit.putString("image" + id, imagePath);
                edit.putString("title"+id, title);
                edit.putString("price"+id, price);
                edit.putString("amount" + id, amount);
                edit.putInt("id" + itemsCount, id);
                edit.putInt("count", itemsCount);
                edit.commit();
            }

        }

    }

    public void deleteFromSharedpreferences(int id) {
        SharedPreferences ss1 = getSharedPreferences("wishlist" , 0);
        for (int x=1 ; x<= ss1.getInt("count" , 0) ; x++){
            if (ss1.getInt("id"+x , 0) == id){

                SharedPreferences.Editor editor = ss1.edit();
                editor.remove("id" + x);
                editor.remove("title" + id);
                editor.remove("image" + id);
                editor.remove("price" + id);
                editor.remove("cartTitle" + id);

                editor.apply();
                editor.commit();
                break;
            }
        }
    }

    public void refillActivity() {

        ArrayList<String> image = new ArrayList<>();
        ArrayList<ArrayList<String>> detail = new ArrayList<>();

        SharedPreferences ss1 = getSharedPreferences("wishlist" , 0);
        int itemsCount = ss1.getInt("count", 0) ;

        if (itemsCount > 0) {

            content = (LinearLayout)findViewById(R.id.content);
            boolean emptyCart = true;

            try {
                //ArrayList<String> i = new ArrayList<>();
                for (int index = 1 ; index <= itemsCount ;index++ ){

                    int id = ss1.getInt("id" + index, 0);

                    if (id != 0) {
                        emptyCart = false;
                        final String imagePath = ss1.getString("image" + id, "");
                        image.add(imagePath);
                        final String title = ss1.getString("title" + id, "");
                        final String price = ss1.getString("price"+id, "");
                        final String cartTitle = ss1.getString("cartTitle"+id, "");
                        final String ids = String.valueOf(id);
                        detail.add(new ArrayList<String>(){{add(title);add(price);add(ids);add(cartTitle);}});
                    }

                }

                if (emptyCart){
                    setContentView(R.layout.emptywishlist);
                    Button explore = (Button) findViewById(R.id.explore);
                    explore.setOnClickListener(exploreListener);
                } else {
                    fillActivity(detail, image);
                }

            } catch (Exception ex){
                Toast.makeText(getApplicationContext(), "oncreate : " + ex.toString(), Toast.LENGTH_SHORT).show();
            }
        } else {
            setContentView(R.layout.emptywishlist);
            Button explore = (Button) findViewById(R.id.explore);
            explore.setOnClickListener(exploreListener);
        }

    }

    View.OnClickListener exploreListener =  new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    public int pxToDp(int px) {
        final float scale = getBaseContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (px * scale + 0.5f);

        return pixels;
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
