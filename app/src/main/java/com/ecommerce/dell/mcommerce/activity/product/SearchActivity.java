package com.ecommerce.dell.mcommerce.activity.product;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Timer;
import java.util.TimerTask;

public class SearchActivity extends ActionBarActivity {

    public SVProgressHUD mSVProgressHUD;
    ArrayList<ProductData> productDetailsArray = new ArrayList<>();
    private ProductAdapter productAdapter;
    private Handler handler = new Handler();

    GridView gridView;
    AutoCompleteTextView autoCompleteTextView;

    String s1="", s2="";
    String id, wrd;
    String category, searchWord;
    int limit = 1;
    int flimit =0;

    private Timer timer = new Timer();
    private final long DELAY = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_search);

        mSVProgressHUD = new SVProgressHUD(SearchActivity.this);

        gridView = (GridView) findViewById(R.id.products_grid);

        productDetailsArray.clear();

        productAdapter = new ProductAdapter(SearchActivity.this, productDetailsArray);
        productAdapter.setProductDetailsArr(productDetailsArray);
        productAdapter.notifyDataSetChanged();
        gridView.setAdapter(productAdapter);

        Intent i = getIntent();
        category = i.getStringExtra("category");

        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autocomplete);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timer != null)
                    timer.cancel();
            }

            @Override
            public void afterTextChanged(Editable s) {

                final String strKey = s.toString();

                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        onSearchCategory(strKey);
                    }
                }, DELAY);
            }
        });

        autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);

                    if (autoCompleteTextView.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.EnterSearchKey), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    return true;
                }
                return false;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProductData pDetailsObjCategory = (ProductData) productDetailsArray.get(position);

                Intent myIntent = new Intent(SearchActivity.this, DetailsActivity.class);
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
                Log.d("Extra", "" + pDetailsObjCategory.getProductPrice() + "-- " + pDetailsObjCategory.getProductDescreiption() + "---" + pDetailsObjCategory.getProductImage());
                SearchActivity.this.startActivity(myIntent);
            }
        });
    }

    public void onSearchCategory(String strKey) {

        Log.d("=======", "search key : " + strKey);

        productDetailsArray.clear();

        if (UtilityClass.isInternetAvailable(getApplicationContext())) {

            if (strKey.length() > 0) {

                limit = 1; flimit = 0;
                id = category;
                wrd = strKey;

                if (s1.equals(limit+"") && s2.equals(wrd)){

                } else {
                    s1 = limit + "";
                    s2 = wrd;

                    new SearchProducts().execute();
                    searchWord = strKey;
                }
            } else {

                searchWord = ""; wrd = "";
                limit = 1; flimit = 0;
                s1 = ""; s2 = "";

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        productAdapter.setProductDetailsArr(productDetailsArray);
                        productAdapter.notifyDataSetChanged();

                        ActionBar actionBar = getSupportActionBar();
                        actionBar.show();
                    }
                });
            }
        }
    }

    public class SearchProducts extends AsyncTask<Void, Void, ArrayList<ProductData>> {

        JSONObject result = new JSONObject();
        String error = "";
        JsonFunctions jsonFunctions = new JsonFunctions();

        private ArrayList<ProductData> getProductsDataFromJSON(JSONObject productsJSONObj) throws JSONException {

            ArrayList<ProductData> productDetailsArray = new ArrayList<>();

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

                productDetailsArray.add(i, new ProductData(pName, pID, pRealPrice, pDiscount, pDescreiption, pImage, pPercentage, PURL,
                        PSKU, PSize, PType, POrigins, PFragrance));
            }
            return productDetailsArray;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected ArrayList<ProductData> doInBackground(Void... params) {

            try {
                result = jsonFunctions.searchAllProducts(id, limit+"", wrd);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (result == null) {
                error = "null json object";
                return null;
            }
            try {
                if (result.getInt("success") == 1) {
                    return getProductsDataFromJSON(result);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;

        }


        @Override
        protected void onPostExecute( ArrayList<ProductData> AProducts) {
            //final ArrayList<productDetails> products = AProducts;
            super.onPostExecute(AProducts);

            mSVProgressHUD.dismiss();

            if (AProducts != null) {
                productDetailsArray.addAll(AProducts);
            } else {
                return;
            }

            productAdapter.setProductDetailsArr(productDetailsArray);
            productAdapter.notifyDataSetChanged();

            if (productDetailsArray != null) {

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

                            Log.e("size",productDetailsArray.size()+"");
                            Log.e("p",productDetailsArray.toString());
                            try {
                                if (productDetailsArray.size() > 0) {
                                    limit = Integer.parseInt((productDetailsArray.get(productDetailsArray.size() - 1)).getProductID());
                                    Log.d("LimitPost", String.valueOf(limit));
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Log.d("First Visible", String.valueOf(firstVisibleItem));
                            Log.d("Count", String.valueOf(visibleItemCount));
                            Log.d("Total", String.valueOf(totalItemCount));

                            if (flimit != limit) {
                                flimit = limit; //to stop using last element in fatching last products more than one time.
                                Log.d("limit", String.valueOf(limit));

                                if (UtilityClass.isInternetAvailable(getApplicationContext())) {
                                    Log.d("SearchWord", searchWord);
                                    id = category;
                                    wrd = searchWord;
                                    if (s1.equals(limit+"") && s2.equals(wrd)){

                                    } else {
                                        s1 = limit+"";
                                        s2 = wrd;

                                        if (!s2.equals("")) {
                                            mSVProgressHUD = new SVProgressHUD(SearchActivity.this);
                                            mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);

                                            new SearchProducts().execute();
                                        }
                                    }
                                }

                            } else {
                                Log.d("FLimit", ">>" + "END");
                            }

                        }
                    }

                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {

                    }

                });

            }
        }

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