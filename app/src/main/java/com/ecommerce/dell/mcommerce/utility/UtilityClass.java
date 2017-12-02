package com.ecommerce.dell.mcommerce.utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.bigkoo.alertview.AlertView;
import com.ecommerce.dell.mcommerce.R;
import com.ecommerce.dell.mcommerce.models.Global;

import java.io.File;
import java.util.ArrayList;

public class UtilityClass extends Activity {

    public  UtilityClass() {

    }

    public static boolean isInternetAvailable(Context _context) {

        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void onShowNetworkConnectionError(Context _context) {

        new AlertView(_context.getString(R.string.alert),
                _context.getString(R.string.alert_network_connection_failed), null,
                new String[]{_context.getString(R.string.ok)}, null, _context,
                AlertView.Style.Alert, null).show();
    }

    public static void insertOrderDBData(String orderID, String email, String name, String amount, String sku, String price, String image) {

        String sql_order = "insert into " + Global.LOCAL_TABLE_ORDERS + " ("
                + Global.LOCAL_FIELD_ORDER_ID + ", "
                + Global.LOCAL_FIELD_EMAIL + ", "
                + Global.LOCAL_FIELD_PRODUCT_NAME + ", "
                + Global.LOCAL_FIELD_PRODUCT_AMOUNT + ", "
                + Global.LOCAL_FIELD_PRODUCT_SKU + ", "
                + Global.LOCAL_FIELD_PRODUCT_PRICE + ", "
                + Global.LOCAL_FIELD_PRODUCT_IMAGE
                + ") values(?,?,?,?,?,?,?)";
        Object[] args_order = new Object[] { orderID, email, name, amount, sku, price, image};
        Global.dbService.execSQL(sql_order, args_order);
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

    public ArrayList<Integer> counter() {
        int cartItems , wishItems;
        ArrayList<Integer> values = new ArrayList<>();

        SharedPreferences cart = getSharedPreferences("item", 0);
        cartItems = cart.getInt("count" , 0);
        SharedPreferences wish = getSharedPreferences("wishlist" , 0);
        wishItems = wish.getInt("count" , 0);

        values.add(cartItems);
        values.add(wishItems);

        return  values;
    }

    public  void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                    Log.i("TAG", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");
                }
            }
        }
    }

}
