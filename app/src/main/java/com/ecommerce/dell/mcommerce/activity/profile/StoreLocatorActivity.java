package com.ecommerce.dell.mcommerce.activity.profile;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.ecommerce.dell.mcommerce.R;
import com.ecommerce.dell.mcommerce.jsonparser.JsonFunctions;
import com.ecommerce.dell.mcommerce.models.Global;
import com.ecommerce.dell.mcommerce.models.StoreData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StoreLocatorActivity extends ActionBarActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location location;
    private Spinner spinnerCountry, spinnerCity, spinnerBranch;
    private List<Marker> listMarker;
    private List<StoreData> listBranch;
    private List<String> listCountry, arrayCity, arrayBranch;
    public SVProgressHUD mSVProgressHUD;
    private String strSelectedCountry = "";
    private String strSelectedCity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_locator);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_store_locator);

        setUpMapIfNeeded();

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        location = locationManager.getLastKnownLocation(provider);

        DisplayMetrics mec = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mec);
        int scrWid = mec.widthPixels;

        spinnerCountry = (Spinner) findViewById(R.id.spinner_country);
        spinnerCity = (Spinner) findViewById(R.id.spinner_city);
        spinnerBranch = (Spinner) findViewById(R.id.spinner_branch);
        spinnerCountry.setDropDownWidth(scrWid * 3 / 4);
        spinnerCity.setDropDownWidth(scrWid * 3 / 4);
        spinnerBranch.setDropDownWidth(scrWid * 3 / 4);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (location != null) {

            double lat = location.getLatitude();
            double lng = location.getLongitude();
            LatLng latLng = new LatLng(lat, lng);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

        }
//        else {
//            new AlertView(getString(R.string.storeLocator_alertTitle),
//                    getString(R.string.storeLocator_alertMessage), null,
//                    new String[]{getString(R.string.ok)}, null, StoreLocatorActivity.this,
//                    AlertView.Style.Alert, new OnItemClickListener() {
//                @Override
//                public void onItemClick(Object o, int position) {
//                    if(position != AlertView.CANCELPOSITION) {
//                        finish();
//                    }
//                }
//            }).show();
//        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        if (location != null) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            LatLng latLng = new LatLng(lat,lng);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {

        if (Global.storeDataArrayList == null || Global.storeDataArrayList.size() == 0) {
            new getLocations().execute();
        } else {
            onParseStoreLocatoresData(Global.storeDataArrayList);
        }
    }

    public class getLocations extends AsyncTask<String, Void, ArrayList<StoreData>> {

        JSONObject result = new JSONObject();
        String error = "";
        JsonFunctions jsonFunctions = new JsonFunctions();

        private ArrayList<StoreData> getProductsDataFromJSON(JSONObject productsJSONObj) throws JSONException {

            ArrayList<StoreData> locationDetailses = new ArrayList<StoreData>();
            final String name = "name";
            final String display_address = "displayaddress";
            final String zip_code = "zipcode";
            final String city = "city";
            final String state = "state";
            final String country_id = "country_id";
            final String phone = "phone";
            final String lat = "lat";
            final String lng = "long";

            JSONArray locationArray = productsJSONObj.getJSONArray("Locations");
            for (int i = 0; i < locationArray.length(); i++) {
                JSONObject getProductInfo = locationArray.getJSONObject(i);
                String locname = getProductInfo.getString(name);
                String locAdd = getProductInfo.getString(display_address);
                String loczipcode = getProductInfo.getString(zip_code);
                String loc_city = getProductInfo.getString(city);
                String locState = getProductInfo.getString(state);
                String locCountryId = getProductInfo.getString(country_id);
                String locPhone = getProductInfo.getString(phone);
                String locLat = getProductInfo.getString(lat);
                String locLng = getProductInfo.getString(lng);
                locationDetailses.add(i, new StoreData(locname, locAdd, loczipcode, loc_city, locState, locCountryId, locPhone,locLat, locLng));
            }
            return locationDetailses;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSVProgressHUD = new SVProgressHUD(StoreLocatorActivity.this);
            mSVProgressHUD.showWithStatus(getString(R.string.please_wait), SVProgressHUD.SVProgressHUDMaskType.Clear);
        }

        @Override
        protected ArrayList<StoreData> doInBackground(String... params) {
            result = jsonFunctions.getLocations();
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
        protected void onPostExecute(ArrayList<StoreData> locations) {
            super.onPostExecute(locations);
            mSVProgressHUD.dismiss();
            try {
                Global.storeDataArrayList = locations;
                onParseStoreLocatoresData(Global.storeDataArrayList);
            } catch (Exception ex) {
                Log.d("Proxxxxx", ex.toString());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public String onGetFormattedCountry(String strCountry) {

        if (strCountry.equals("SA")) {
            return getResources().getString(R.string.country_sa);
        } else if (strCountry.equals("BH")) {
            return getResources().getString(R.string.country_bh);
        } else if (strCountry.equals("OM")) {
            return getResources().getString(R.string.country_om);
        } else if (strCountry.equals("QA")) {
            return getResources().getString(R.string.country_qa);
        } else if (strCountry.equals("AE")) {
            return getResources().getString(R.string.country_ae);
        } else if (strCountry.equals("FR")) {
            return getResources().getString(R.string.country_fr);
        } else if (strCountry.equals("EG")) {
            return getResources().getString(R.string.country_eg);
        } else if (strCountry.equals("JO")) {
            return getResources().getString(R.string.country_jo);
        } else if (strCountry.equals("IQ")) {
            return getResources().getString(R.string.country_iq);
        } else if (strCountry.equals("MY")) {
            return getResources().getString(R.string.country_my);
        } else if (strCountry.equals("GB")) {
            return getResources().getString(R.string.country_gb);
        } else if (strCountry.equals("KW")) {
            return getResources().getString(R.string.country_kw);
        } else if (strCountry.equals("PL")) {
            return getResources().getString(R.string.country_pl);
        } else {
            return strCountry;
        }
    }

    public void onParseStoreLocatoresData(final ArrayList<StoreData> locations) {

        mMap.clear();

        listMarker = new ArrayList<>();
        listBranch = new ArrayList<>();

        listCountry = new ArrayList<>();
        ArrayList<String> arrayCountry = new ArrayList<>();
        arrayCountry.add(getResources().getString(R.string.storeLocator_country));

        arrayCity = new ArrayList<>();
        arrayCity.add(getResources().getString(R.string.storeLocator_city));

        arrayBranch = new ArrayList<>();
        arrayBranch.add(getResources().getString(R.string.storeLocator_branch));

        for (int i = 0; i < locations.size(); i++) {

            LatLng sydney = new LatLng(Double.parseDouble(locations.get(i).getLat()), Double.parseDouble(locations.get(i).getLng()));
            Marker marker = mMap.addMarker(new MarkerOptions().position(sydney).title(locations.get(i).getName()).snippet("Phone : "+locations.get(i).getPhone()));

            if (!listCountry.contains(locations.get(i).getCountry_id())) {
                String country = locations.get(i).getCountry_id();
                listCountry.add(country);
                arrayCountry.add(onGetFormattedCountry(country));
            }

            if (!arrayCity.contains(locations.get(i).getCity())) {
                arrayCity.add(locations.get(i).getCity());
            }

            arrayBranch.add(locations.get(i).getName());
            listMarker.add(marker);
            listBranch.add(locations.get(i));
        }

        ArrayAdapter<String> adapterCountry = new ArrayAdapter<String>(StoreLocatorActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayCountry);
        spinnerCountry.setAdapter(adapterCountry);

        ArrayAdapter<String> adapterCity = new ArrayAdapter<String>(StoreLocatorActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayCity);
        spinnerCity.setAdapter(adapterCity);

        ArrayAdapter<String> adapterBranch = new ArrayAdapter<String>(StoreLocatorActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayBranch);
        spinnerBranch.setAdapter(adapterBranch);

        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    if (position == 0) {

                    } else {
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (int i = 0; i < listBranch.size(); i++) {
                            StoreData storeData = listBranch.get(i);
                            if (listCountry.get(position - 1).equals(storeData.getCountry_id())) {
                                builder.include(listMarker.get(i).getPosition());
                            }
                        }
                        LatLngBounds bounds = builder.build();
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 14));

                        onParseCountryStoresData(listCountry.get(position - 1));
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    if (position == 0) {

                    } else {
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (int i = 0; i < listBranch.size(); i++) {
                            StoreData storeData = listBranch.get(i);
                            if (arrayCity.get(position).equals(storeData.getCity())) {
                                builder.include(listMarker.get(i).getPosition());
                            }
                        }
                        LatLngBounds bounds = builder.build();
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 14));

                        onParseCityStoresData(arrayCity.get(position));
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    if (position == 0) {

                    } else {

                        for (int i = 0; i < listBranch.size(); i++) {
                            StoreData storeData = listBranch.get(i);
                            if (!strSelectedCity.equals("")) {
                                if (arrayBranch.get(position).equals(storeData.getName()) && strSelectedCity.equals(storeData.getCity())) {

                                    double lat = Double.parseDouble(listBranch.get(i).getLat());
                                    double lng = Double.parseDouble(listBranch.get(i).getLng());
                                    LatLng latLng = new LatLng(lat, lng);

                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                                }
                            } else {
                                if (arrayBranch.get(position).equals(storeData.getName())) {

                                    double lat = Double.parseDouble(listBranch.get(i).getLat());
                                    double lng = Double.parseDouble(listBranch.get(i).getLng());
                                    LatLng latLng = new LatLng(lat, lng);

                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                                }
                            }
                        }
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void onParseCountryStoresData(String strCountry) {

        strSelectedCountry = strCountry;
        strSelectedCity = "";
        mMap.clear();

        arrayCity = new ArrayList<>();
        arrayCity.add(getResources().getString(R.string.storeLocator_city));

        arrayBranch = new ArrayList<>();
        arrayBranch.add(getResources().getString(R.string.storeLocator_branch));

        for (int i = 0; i < Global.storeDataArrayList.size(); i++) {

            if (Global.storeDataArrayList.get(i).getCountry_id().equals(strSelectedCountry)) {

                LatLng sydney = new LatLng(Double.parseDouble(Global.storeDataArrayList.get(i).getLat()), Double.parseDouble(Global.storeDataArrayList.get(i).getLng()));
                mMap.addMarker(new MarkerOptions().position(sydney).title(Global.storeDataArrayList.get(i).getName()).snippet("Phone : "+Global.storeDataArrayList.get(i).getPhone()));

                if (!arrayCity.contains(Global.storeDataArrayList.get(i).getCity())) {
                    arrayCity.add(Global.storeDataArrayList.get(i).getCity());
                }

                arrayBranch.add(Global.storeDataArrayList.get(i).getName());
            }
        }

        ArrayAdapter<String> adapterCity = new ArrayAdapter<String>(StoreLocatorActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayCity);
        spinnerCity.setAdapter(adapterCity);

        ArrayAdapter<String> adapterBranch = new ArrayAdapter<String>(StoreLocatorActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayBranch);
        spinnerBranch.setAdapter(adapterBranch);

    }

    public void onParseCityStoresData(String strCity) {

        strSelectedCity = strCity;
        mMap.clear();

        arrayBranch = new ArrayList<>();
        arrayBranch.add(getResources().getString(R.string.storeLocator_branch));

        for (int i = 0; i < Global.storeDataArrayList.size(); i++) {

            if (strSelectedCountry.equals("")) {
                if (strCity.equals(Global.storeDataArrayList.get(i).getCity())) {

                    LatLng sydney = new LatLng(Double.parseDouble(Global.storeDataArrayList.get(i).getLat()), Double.parseDouble(Global.storeDataArrayList.get(i).getLng()));
                    mMap.addMarker(new MarkerOptions().position(sydney).title(Global.storeDataArrayList.get(i).getName()).snippet("Phone : "+Global.storeDataArrayList.get(i).getPhone()));

                    arrayBranch.add(Global.storeDataArrayList.get(i).getName());
                }
            } else {
                if (strCity.equals(Global.storeDataArrayList.get(i).getCity()) && strSelectedCountry.equals(Global.storeDataArrayList.get(i).getCountry_id())) {

                    LatLng sydney = new LatLng(Double.parseDouble(Global.storeDataArrayList.get(i).getLat()), Double.parseDouble(Global.storeDataArrayList.get(i).getLng()));
                    mMap.addMarker(new MarkerOptions().position(sydney).title(Global.storeDataArrayList.get(i).getName()).snippet("Phone : "+Global.storeDataArrayList.get(i).getPhone()));

                    arrayBranch.add(Global.storeDataArrayList.get(i).getName());
                }
            }
        }

        ArrayAdapter<String> adapterBranch = new ArrayAdapter<String>(StoreLocatorActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayBranch);
        spinnerBranch.setAdapter(adapterBranch);

    }
}