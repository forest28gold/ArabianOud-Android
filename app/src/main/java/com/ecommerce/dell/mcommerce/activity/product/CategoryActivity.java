package com.ecommerce.dell.mcommerce.activity.product;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.ecommerce.dell.mcommerce.R;
import com.ecommerce.dell.mcommerce.adapter.ProductAdapter;
import com.ecommerce.dell.mcommerce.jsonparser.JsonFunctions;
import com.ecommerce.dell.mcommerce.models.ProductData;
import com.ecommerce.dell.mcommerce.utility.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class CategoryActivity extends ActionBarActivity {

    public SVProgressHUD mSVProgressHUD;
    private ProductAdapter productAdapter;
    GetProductsTask getProductsTask;
    ArrayList<ProductData> productDetailsArray = new ArrayList<ProductData>();

    Menu optionMenu;
    MenuItem cartCount;
    MenuItem wishCount;
    GridView gridView;

    int cartItems, wishItems;
    int reaiCartCount, reaiWishCount;
    String category, categoryName, mTitle;
    String language;
    int limit, flimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        onInitSetSharePreference();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSVProgressHUD = new SVProgressHUD(this);

        Intent intent = getIntent();
        category = intent.getStringExtra("categoryID");
        categoryName = intent.getStringExtra("categoryName");
        mTitle = intent.getStringExtra("mTitle");

        EditText search = (EditText) findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CategoryActivity.this, SearchActivity.class);
                i.putExtra("category", "");
                startActivity(i);
            }
        });

        gridView = (GridView) findViewById(R.id.products_grid);
        productAdapter = new ProductAdapter(CategoryActivity.this, productDetailsArray);
        gridView.setAdapter(productAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ProductData pDetailsObjCategory = (ProductData) productDetailsArray.get(position);

                Intent myIntent = new Intent(CategoryActivity.this, DetailsActivity.class);
                myIntent.putExtra("name", pDetailsObjCategory.getProductName());
                myIntent.putExtra("price", " " + pDetailsObjCategory.getProductPrice());
                myIntent.putExtra("description", pDetailsObjCategory.getProductDescreiption());
                myIntent.putExtra("sale", pDetailsObjCategory.getProductPercentage());
                myIntent.putExtra("discount", pDetailsObjCategory.getProductDiscount());
                myIntent.putExtra("features", "");
                myIntent.putExtra("fromServer", "yes");
                myIntent.putExtra("image", pDetailsObjCategory.getProductImage());
                myIntent.putExtra("id", pDetailsObjCategory.getProductID());
                myIntent.putExtra("code",pDetailsObjCategory.getpSKU());
                myIntent.putExtra("size",pDetailsObjCategory.getpSize());
                myIntent.putExtra("type",pDetailsObjCategory.getpType());
                myIntent.putExtra("origins",pDetailsObjCategory.getpOrigin());
                myIntent.putExtra("fragrance",pDetailsObjCategory.getpFragrance());
                myIntent.putExtra("URL",pDetailsObjCategory.getpURL());
                startActivity(myIntent);
            }
        });

        limit=1;
        flimit=0;

        if (UtilityClass.isInternetAvailable(getApplicationContext())) {

            SharedPreferences setting = getSharedPreferences("setting", 0);
            language = setting.getString("language", "");

            getProductsTask = new GetProductsTask();
            getProductsTask.execute(category, language);

        } else {
            new AlertView(getString(R.string.alert),
                    getString(R.string.alert_network_connection_failed), null,
                    new String[]{getString(R.string.ok)}, null, CategoryActivity.this,
                    AlertView.Style.Alert, null).show();
        }

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
        actionBar.setTitle(mTitle);
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

        restoreActionBar();
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
            return true;
        } else if (id == R.id.cart) {
            Intent myIntent = new Intent(CategoryActivity.this, CartActivity.class);
            startActivity(myIntent);
            return true;
        } else if (id == R.id.wish) {
            Intent myIntent = new Intent(CategoryActivity.this, WishListActivity.class);
            startActivity(myIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class GetProductsTask extends AsyncTask<String, Void, ArrayList<ProductData>> {

        JsonFunctions jsonFunctions = new JsonFunctions();
        JSONObject result = new JSONObject();
        String error = "";

        //--------------- Edit --------------------
        private ArrayList<ProductData> getProductsDataFromJSON(JSONObject productsJSONObj) throws JSONException {

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
                String pURL = getProductInfo.getString(getProductURL);
                String PSKU = getProductInfo.getString(productSKU);
                String PSize = getProductInfo.getString(getProductSize);
                String PType = getProductInfo.getString(getProductType);
                String POrigins = getProductInfo.getString(getProductOrigins);
                String PFragrance = getProductInfo.getString(getProductFragrance);

                productDetailsArray.add( new ProductData(pName, pID, pRealPrice, pDiscount, pDescreiption, pImage, pPercentage, pURL, PSKU, PSize, PType, POrigins, PFragrance));
            }
            return productDetailsArray;
        }

        //--------------- Edit --------------------
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);
            Log.d("getProductsTask", "getProductsTask");
        }

        @Override
        protected ArrayList<ProductData> doInBackground(String... params) {

            try {
                result = jsonFunctions.getCategoryProducts(params[0], String.valueOf(limit), params[1]);
            } catch (Exception e) {
                Log.d("ResultJsonParsing",e.toString());
            }

            Log.d("LIMIT",""+String.valueOf(limit));

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
                cancel(true);
            }
            try {

            } catch (Exception e) {
                Log.e("error", e.getMessage(), e);
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(ArrayList<ProductData> AProducts) {
            final ArrayList<ProductData> products = AProducts;
            super.onPostExecute(products);

            mSVProgressHUD.dismiss();

            if(productAdapter == null) {
                productAdapter = new ProductAdapter(CategoryActivity.this, products);
                gridView.setAdapter(productAdapter);
            } else {
                productAdapter.setProductDetailsArr(products);
                productAdapter.notifyDataSetChanged();
            }

            if (products != null) {
                Log.d("LimitPost", String.valueOf(limit));
                gridView.setOnScrollListener(new AbsListView.OnScrollListener() {

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                        ActionBar actionBar = getSupportActionBar();

                        if(firstVisibleItem > visibleItemCount) {
                            if (actionBar.isShowing()){
                                actionBar.hide();
                            }
                        }
                        if(firstVisibleItem < visibleItemCount) {
                            //scroll up
                            if (!actionBar.isShowing()){
                                actionBar.show();
                            }
                        }

                        if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                            try {
                                limit = Integer.parseInt((products.get(products.size() -1)).getProductID());
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                            Log.d("First Visible", String.valueOf(firstVisibleItem));
                            Log.d("Count", String.valueOf(visibleItemCount));
                            Log.d("Total", String.valueOf(totalItemCount));

                            if (flimit != limit) {
                                flimit = limit; //to stop using last element in fatching last products more than one time.
                                Log.d("limit", String.valueOf(limit));

                                if (UtilityClass.isInternetAvailable(getApplicationContext())) {
                                    getProductsTask = new GetProductsTask();
                                    getProductsTask.execute(category, language);
                                }

                            } else {
                                Log.d("FLimit", ">>" + "END");
                            }

                        } else {
//                            progressDialog.dismiss();
                        }

                    }

                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {

                    }

                });
            }
        }
    }

}
