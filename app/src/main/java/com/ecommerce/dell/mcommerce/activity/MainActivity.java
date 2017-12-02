package com.ecommerce.dell.mcommerce.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.ecommerce.dell.mcommerce.R;
import com.ecommerce.dell.mcommerce.activity.product.CartActivity;
import com.ecommerce.dell.mcommerce.activity.product.CategoryActivity;
import com.ecommerce.dell.mcommerce.activity.product.SearchActivity;
import com.ecommerce.dell.mcommerce.activity.product.WishListActivity;
import com.ecommerce.dell.mcommerce.activity.profile.AboutActivity;
import com.ecommerce.dell.mcommerce.activity.profile.ContactUsActivity;
import com.ecommerce.dell.mcommerce.adapter.RecyclerAdapter;
import com.ecommerce.dell.mcommerce.jsonparser.JsonFunctions;
import com.ecommerce.dell.mcommerce.models.ProductData;
import com.ecommerce.dell.mcommerce.utility.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    public static Context context;

    NavigationDrawerFragment mNavigationDrawerFragment;
    RecyclerView newArrivalRecycler;
    RecyclerAdapter mAdapter;

    public static Menu optionMenu;
    public static MenuItem cartCount;
    public static MenuItem wishCount;

    private ArrayList<ProductData> offersData = new ArrayList<>();

    String language;
    public static int cartItems, reaiCartCount;
    int wishItems, reaiWishCount;
    int arraysize, limit, flimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getBaseContext();

        SharedPreferences setting = getSharedPreferences("setting", 0);
        language = setting.getString("language", "");
        if (!language.equals("ar")) {
            language = "en";
        }
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        onInitSetSharePreference();

        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout)findViewById(R.id.drawer_layout));

        if (!UtilityClass.isInternetAvailable(getApplicationContext())) {

            new AlertView(getString(R.string.alert),
                    getString(R.string.alert_network_connection_failed), null,
                    new String[]{getString(R.string.ok)}, null, MainActivity.this,
                    AlertView.Style.Alert, null).show();
        }

        newArrivalRecycler = (RecyclerView) findViewById(R.id.recycler_view);
        newArrivalRecycler.setHasFixedSize(true);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        newArrivalRecycler.setLayoutManager(layoutManager);
        limit = 1;
        flimit = 0;

        mAdapter = new RecyclerAdapter(offersData, MainActivity.this);
        newArrivalRecycler.setAdapter(mAdapter);

        new getProductsTask().execute("8", "1");

        Button Larrow_bestseller = (Button) findViewById(R.id.left_arrow_bestSellers);
        Button Rarrow_bestseller = (Button) findViewById(R.id.right_arrow_bestSellers);
        Larrow_bestseller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newArrivalRecycler.scrollBy(-500, 0);
            }
        });
        Rarrow_bestseller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newArrivalRecycler.scrollBy(500, 0);
            }
        });

        newArrivalRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(flimit != limit) {
                    flimit = limit;
                    switch (newState) {
                        case RecyclerView.SCROLL_STATE_DRAGGING:
                            new getProductsTask().execute("8", String.valueOf(limit));
                            break;
                    }
                }
            }
        });

        EditText search = (EditText) findViewById(R.id.search);
        Button offers = (Button) findViewById(R.id.a19);
        Button execlusives = (Button) findViewById(R.id.a61);
        Button bestsellers = (Button) findViewById(R.id.a33);
        Button women = (Button) findViewById(R.id.a26);
        Button men = (Button) findViewById(R.id.a27);
        ImageView contactusimage = (ImageView) findViewById(R.id.contactusimage);
        ImageView shippingimage = (ImageView) findViewById(R.id.shippingimage);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SearchActivity.class);
                i.putExtra("category", "");
                startActivity(i);
            }
        });

        offers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, CategoryActivity.class);
                myIntent.putExtra("categoryID", "19");
                myIntent.putExtra("mTitle", getResources().getString(R.string.main_activity_offers));
                startActivity(myIntent);
            }
        });

        execlusives.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, CategoryActivity.class);
                myIntent.putExtra("categoryID", "61");
                myIntent.putExtra("mTitle", getResources().getString(R.string.main_activity_execlusive));
                startActivity(myIntent);
            }
        });


        bestsellers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, CategoryActivity.class);
                myIntent.putExtra("categoryID", "33");
                myIntent.putExtra("mTitle", getResources().getString(R.string.main_activity_best_sellers));
                startActivity(myIntent);
            }
        });


        women.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, CategoryActivity.class);
                myIntent.putExtra("categoryID", "26");
                myIntent.putExtra("mTitle", getResources().getString(R.string.main_activity_women));
                startActivity(myIntent);
            }
        });

        men.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, CategoryActivity.class);
                myIntent.putExtra("categoryID", "27");
                myIntent.putExtra("mTitle", getResources().getString(R.string.main_activity_men));
                startActivity(myIntent);
            }
        });

        contactusimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this , ContactUsActivity.class);
                startActivity(i);
            }
        });

        shippingimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(i);
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

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.main_activity_title);
    }

    @Override
    protected void onResume() {
        super.onResume();

        onInitSetSharePreference();

        if (cartCount != null)
            cartCount.setTitle(String.valueOf(reaiCartCount));
        if (wishCount != null)
            wishCount.setTitle(String.valueOf(reaiWishCount));
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {

            getMenuInflater().inflate(R.menu.main, menu);

            optionMenu = menu;
            cartCount = optionMenu.findItem(R.id.cartcount);
            wishCount = optionMenu.findItem(R.id.wishcount);

            cartCount.setTitle(String.valueOf(reaiCartCount));
            wishCount.setTitle(String.valueOf(reaiWishCount));

            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.cart) {
            Intent myIntent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(myIntent);
            return true;
        } else if (id == R.id.wish) {
            Intent myIntent = new Intent(MainActivity.this, WishListActivity.class);
            startActivity(myIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class getProductsTask extends AsyncTask<String, Void, ArrayList<ProductData>> {

        JSONObject result = new JSONObject();
        String error = "";
        JsonFunctions jsonFunctions = new JsonFunctions();

        private ArrayList<ProductData> getProductsDataFromJSON(JSONObject productsJSONObj) throws JSONException {

            ArrayList<ProductData> productDetailsArr = new ArrayList<ProductData>();

            final String product_image = "image";
            final String product_name = "name";
            final String product_id = "id";
            final String productRealPrice = "price";
            final String getProductDiscount = "discount";
            final String getProductDescreiption = "description";
            final String getProductPercentage = "percentage";
            final String getProductURL = "url";
            final String productSKU = "sku";
            final String getProductSize = "size";
            final String getProductType = "type";
            final String getProductOrigins = "origin";
            final String getProductFragrance = "fragrance";


            JSONArray productsArray = productsJSONObj.getJSONArray("products");

            for (int i = 0; i < productsArray.length(); i++) {
                /*get JSON Object represent product info(Image-Name-Real Price - Discount)*/
                JSONObject getProductInfo = productsArray.getJSONObject(i);
                String pImage = getProductInfo.getString(product_image);
                String pName = getProductInfo.getString(product_name);
                String pID = getProductInfo.getString(product_id);
                String pRealPrice = getProductInfo.getString(productRealPrice);
                String pDiscount = getProductInfo.getString(getProductDiscount);
                String pDescreiption = getProductInfo.getString(getProductDescreiption);
                String pPercentage = getProductInfo.getString(getProductPercentage);
                String PURL = getProductInfo.getString(getProductURL);
                String PSKU = getProductInfo.getString(productSKU);
                String PSize = getProductInfo.getString(getProductSize);
                String PType = getProductInfo.getString(getProductType);
                String POrigins = getProductInfo.getString(getProductOrigins);
                String PFragrance = getProductInfo.getString(getProductFragrance);
                productDetailsArr.add(i, new ProductData(pName, pID, pRealPrice, pDiscount, pDescreiption, pImage, pPercentage, PURL,
                        PSKU, PSize, PType, POrigins, PFragrance));
            }
            return productDetailsArr;
        }
        //--------------- Edit --------------------

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("getProductsTask", "getProductsTask");
        }

        @Override
        protected ArrayList<ProductData> doInBackground(String... params) {
            result = jsonFunctions.getCategoryProducts(params[0], params[1], language);

            if (result == null) {
                error = "null json object";
                return null;
            }
            try {
                if (result.getInt("success") == 1) {
                    return getProductsDataFromJSON(result);
                }
            } catch (Exception ex) {
                error = ex.toString();
                Log.d("getProductsTask", error);
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<ProductData> products) {
            super.onPostExecute(products);

            if (products != null) {
                offersData.addAll(arraysize, products);
                arraysize += products.size();
                limit = Integer.parseInt((products.get(products.size() -1)).getProductID());

                if (offersData.size() > 0) {
                    mAdapter.setProductDetailsArr(offersData);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private long exitTime = 0;

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, R.string.press_again_exit_app, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    public void onBackPressed() {
        doExitApp();
    }

}
