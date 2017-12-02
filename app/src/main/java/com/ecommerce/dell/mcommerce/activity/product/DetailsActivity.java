package com.ecommerce.dell.mcommerce.activity.product;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ecommerce.dell.mcommerce.R;
import com.squareup.picasso.Picasso;


public class DetailsActivity extends ActionBarActivity  {

    ImageView share;
    FrameLayout shareFly;

    Menu optionMenu;
    MenuItem cartCount;
    MenuItem wishCount;
    int cartItems, wishItems;
    int reaiCartCount, reaiWishCount;

    String pNAme, pImage, pPrice, pDiscount, pSale, pDescription, pFeatures, pID;
    String ProductURL, ProductCode, ProductSize, ProductType, ProductOrigins, ProductFragrance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        onInitSetSharePreference();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            pID = extras.getString("id");
            pNAme = extras.getString("name");
            pImage = extras.getString("image");
            pPrice = extras.getString("price");
            pDiscount = extras.getString("discount");
            pSale = extras.getString("sale");
            pDescription = extras.getString("description");
            pFeatures = extras.getString("features");
            ProductURL=extras.getString("URL");
            ProductCode=extras.getString("code");
            ProductSize = extras.getString("size");
            ProductType= extras.getString("type");
            ProductOrigins = extras.getString("origins");
            ProductFragrance = extras.getString("fragrance");

            Picasso.with(this).load(pImage).into(((ImageView) findViewById(R.id.image)));

            ((TextView) findViewById(R.id.title)).setText(pNAme);
            ((TextView) findViewById(R.id.description)).setText(pDescription);

            if (Integer.parseInt(pSale) == 0) {
                ((TextView) findViewById(R.id.price)).setText(pPrice + " " + getResources().getString(R.string.main_activity_currency));
            } else {
                ((TextView) findViewById(R.id.price)).setText(pDiscount + " " + getResources().getString(R.string.main_activity_currency));
            }

            ((TextView) findViewById(R.id.price)).setText(pDiscount + " " + getResources().getString(R.string.main_activity_currency));

            ((TextView) findViewById(R.id.code)).setText(ProductCode);
            ((TextView) findViewById(R.id.size)).setText(ProductSize);
            ((TextView) findViewById(R.id.type)).setText(ProductType);
            ((TextView) findViewById(R.id.origins)).setText(ProductOrigins);
            ((TextView) findViewById(R.id.fragrance)).setText(ProductFragrance);

        }

        setTitle(pNAme);
        Button cart_btn = (Button) findViewById(R.id.cart);
        Button wishlist_btn = (Button) findViewById(R.id.wishlist);
        Button buy_btn = (Button) findViewById(R.id.buy);

        cart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Integer.parseInt(pSale)>0){
                    saveInSharedPreference("item", pImage, pNAme, pDiscount, "1", Integer.parseInt(pID), "");
                } else {
                    saveInSharedPreference("item", pImage, pNAme, pPrice, "1", Integer.parseInt(pID), "");
                }

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.itemaddedTocart), Toast.LENGTH_SHORT).show();
                SharedPreferences carts = getSharedPreferences("item", 0);
                cartItems = carts.getInt("count", 0);

                reaiCartCount = 0;
                for (int i = 1; i <= cartItems; i++) {
                    if (carts.getInt("id" + i, 0) != 0) {
                        reaiCartCount++;
                    }
                }
                cartCount.setTitle(String.valueOf(reaiCartCount));
            }
        });

        buy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Sale",pSale);

                if (Integer.parseInt(pSale)>0){
                    saveInSharedPreference("item", pImage, pNAme, pDiscount, "1", Integer.parseInt(pID), "");
                } else {
                    saveInSharedPreference("item", pImage, pNAme, pPrice, "1", Integer.parseInt(pID), "");
                }

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.itemaddedTocart), Toast.LENGTH_SHORT).show();

                SharedPreferences carts = getSharedPreferences("item", 0);
                cartItems = carts.getInt("count", 0);

                reaiCartCount = 0;
                for (int i1 = 1; i1 <= cartItems; i1++) {
                    if (carts.getInt("id" + i1, 0) != 0) {
                        reaiCartCount++;
                    }
                }
                cartCount.setTitle(String.valueOf(reaiCartCount));

                Intent i = new Intent(new Intent(DetailsActivity.this , CartActivity.class));
                startActivityForResult(i, 1);
                finish();
            }
        });

        wishlist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveInSharedPreference("wishlist", pImage, pNAme, pDiscount, "1", Integer.parseInt(pID), "");
                SharedPreferences wish = getSharedPreferences("wishlist" , 0);
                wishItems = wish.getInt("count" , 0);
                Toast.makeText(DetailsActivity.this,getResources().getString(R.string.itemAddedToWishList), Toast.LENGTH_LONG).show();

                reaiWishCount=0;
                for (int i = 1 ;i <=wishItems ; i++)
                {
                    if (wish.getInt("id"+i , 0)!=0)
                    {
                        reaiWishCount ++;

                    }}

                wishCount.setTitle(String.valueOf(reaiWishCount));
            }
        });

        shareFly = (FrameLayout) findViewById(R.id.shareFly);
        ImageView facebook = (ImageView)findViewById(R.id.facebook);
        ImageView twitter = (ImageView)findViewById(R.id.twitter);
        ImageView pinterest = (ImageView)findViewById(R.id.pinterest);
        ImageView whatsapp = (ImageView)findViewById(R.id.whatsapp);
        ImageView gmail = (ImageView)findViewById(R.id.gmail);
        share = (ImageView)findViewById(R.id.share);

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                Log.e("urlshare",ProductURL);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://shop.arabianoud.com/index.php/" + ProductURL.substring(43, ProductURL.length()));
                sendIntent.setType("text/plain");
                sendIntent.setPackage("com.facebook.katana");
                if (isPackageExisted("com.facebook.katana"))
                    startActivity(sendIntent);
                else
                    Toast.makeText(getBaseContext() , "Faceebook is not installed" , Toast.LENGTH_LONG).show();
            }
        });

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://shop.arabianoud.com/index.php/" + ProductURL.substring(43, ProductURL.length()));
                sendIntent.setType("text/plain");
                sendIntent.setPackage("com.whatsapp");
                if (isPackageExisted("com.whatsapp"))
                    startActivity(sendIntent);
                else
                    Toast.makeText(getBaseContext() , "Whatsapp is not installed" , Toast.LENGTH_LONG).show();
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://shop.arabianoud.com/index.php/" + ProductURL.substring(43, ProductURL.length()));
                sendIntent.setType("text/plain");
                sendIntent.setPackage("com.twitter.android");
                if (isPackageExisted("com.twitter.android"))
                    startActivity(sendIntent);
                else
                    Toast.makeText(getBaseContext() , "Twitter is not installed" , Toast.LENGTH_LONG).show();
            }
        });

        pinterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://shop.arabianoud.com/index.php/" + ProductURL.substring(43, ProductURL.length()));
                sendIntent.setType("text/plain");
                sendIntent.setPackage("com.pinterest");
                if (isPackageExisted("com.pinterest"))
                    startActivity(sendIntent);
                else
                    Toast.makeText(getBaseContext() , "Pinterest is not installed" , Toast.LENGTH_LONG).show();
            }
        });

        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://shop.arabianoud.com/index.php/" + ProductURL.substring(43, ProductURL.length()));
                sendIntent.setType("text/plain");
                sendIntent.setPackage("com.google.android.gm");
                if (isPackageExisted("com.google.android.gm"))
                    startActivity(sendIntent);
                else
                    Toast.makeText(getBaseContext() , "Gmail is not installed" , Toast.LENGTH_LONG).show();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareFly.setVisibility(View.VISIBLE);
            }
        });

        shareFly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareFly.setVisibility(View.INVISIBLE);
            }
        });

    }


    public void onInitSetSharePreference() {

        SharedPreferences cart = getSharedPreferences("item", 0);
        cartItems = cart.getInt("count", 0);
        reaiCartCount = 0;
        for (int i = 1; i <= cartItems; i++) {
            if (cart.getInt("id" + i, 0) != 0) {
                reaiCartCount++;
            }
        }

        SharedPreferences wish = getSharedPreferences("wishlist", 0);
        wishItems = wish.getInt("count", 0);
        reaiWishCount = 0;
        for (int i = 1; i <= wishItems; i++) {
            if (wish.getInt("id" + i, 0) != 0) {
                reaiWishCount++;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        onInitSetSharePreference();

        if (cartCount!=null)
            cartCount.setTitle(String.valueOf(reaiCartCount));
        if (wishCount!=null)
            wishCount.setTitle(String.valueOf(reaiWishCount));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        optionMenu = menu;
        cartCount = optionMenu.findItem(R.id.cartcount);
        wishCount = optionMenu.findItem(R.id.wishcount);

        cartCount.setTitle(String.valueOf(reaiCartCount));
        wishCount.setTitle(String.valueOf(reaiWishCount));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home) {
            finish();
        } else if (id == R.id.cart) {
            Intent myIntent = new Intent(DetailsActivity.this, CartActivity.class);
            startActivity(myIntent);
            finish();
            return true;

        } else if (id == R.id.wish) {
            Intent myIntent = new Intent(DetailsActivity.this, WishListActivity.class);
            startActivity(myIntent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isPackageExisted(String targetPackage){
        PackageManager pm=getPackageManager();
        try {
            PackageInfo info=pm.getPackageInfo(targetPackage,PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    public void saveInSharedPreference(String sharedPreferencesName, String imageBitmap, String title, String price, String amount, int id, String cartTitle) {

        String imagePath = "";

        if (sharedPreferencesName == "item") {
            imagePath = imageBitmap;
        } else {
            imagePath = imageBitmap;
        }

        boolean itemExists = false;
        //save data to shared preferences
        SharedPreferences ss = getSharedPreferences(sharedPreferencesName, 0);
        SharedPreferences.Editor edit = ss.edit();
        int itemsCount = ss.getInt("count", 0);

        if (itemsCount == 0) {
            ++itemsCount;
            edit.putString("image" + id, imagePath);
            edit.putString("title" + id, title);
            edit.putString("price" + id, price);
            if (sharedPreferencesName == "item") {

                edit.putString("amount" + id, amount);

            } else if (sharedPreferencesName == "wishlist") {
                edit.putString("cartTitle" + id, cartTitle);
                wishCount.setTitle(String.valueOf(Integer.parseInt(wishCount.getTitle().toString()) + 1));
            }
            edit.putInt("id" + itemsCount, id);
            edit.putInt("count", itemsCount);
            edit.commit();


        } else {
            for (int index = 1; index <= itemsCount; index++) {
                int ids = ss.getInt("id" + index, 0);
                if (ids == id) {

                    if (sharedPreferencesName == "item") {
                        edit.putString("amount" + id, String.valueOf(Integer.parseInt(ss.getString("amount" + id, "")) + 1));
                        edit.commit();
                    }

                    itemExists = true;
                    break;
                }
            }

            if (!itemExists) {
                ++itemsCount;
                edit.putString("image" + id, imagePath);
                edit.putString("title" + id, title);
                edit.putString("price" + id, price);

                if (sharedPreferencesName == "item") {
                    edit.putString("amount" + id, amount);

                } else if (sharedPreferencesName == "wishlist") {
                    edit.putString("cartTitle" + id, cartTitle);
                    wishCount.setTitle(String.valueOf(Integer.parseInt(wishCount.getTitle().toString()) + 1));
                }

                edit.putInt("id" + itemsCount, id);
                edit.putInt("count", itemsCount);
                edit.commit();
            }

        }

    }

}


