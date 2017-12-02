package com.ecommerce.dell.mcommerce.jsonparser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonFunctions {

    JsonParser jsonparser ;
    String URL ;
    String URL2;

    public JsonFunctions()
    {
        jsonparser = new JsonParser();
        URL = "https://shop.arabianoud.com/indexFun_V2.php";
        URL2 = "https://shop.arabianoud.com/index.php/customer/account/resetpassword/";
    }

    public JSONObject getCategories()
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "GetCategories"));
        JSONObject json = jsonparser.makeHttpRequest(URL , params);
        return json;
    }

    public JSONObject getProducts(String category)
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "GetProducts"));
        params.add(new BasicNameValuePair("category", category));
        JSONObject json = jsonparser.makeHttpRequest(URL , params);
        return json;
    }

    public JSONObject getProductByID(String id)
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "GetProductByID"));
        params.add(new BasicNameValuePair("id", id));
        JSONObject json = jsonparser.makeHttpRequest(URL , params);
        return json;
    }

    public JSONObject login(String email , String password)
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "login"));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonparser.makeHttpRequest(URL , params);
        return json;
    }

    public JSONObject register(String fname ,String sname , String email   , String password )
    {
        JSONObject json = null;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "register"));
        params.add(new BasicNameValuePair("firstname", fname));
        params.add(new BasicNameValuePair("lastname", sname));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        json = jsonparser.makeHttpRequest(URL , params);
        return json;

    }

    public JSONObject resetPassword(String Email)
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "forgetPassword"));
        params.add(new BasicNameValuePair("email", Email));
        JSONObject json = jsonparser.makeHttpRequest(URL2 , params);
        return json;
    }

    public JSONObject uploadImage(String imageString , String name  )
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "uploadImage"));
        params.add(new BasicNameValuePair("imageString", imageString));
        params.add(new BasicNameValuePair("name", name));
        JSONObject json = jsonparser.makeHttpRequest(URL , params);
        return json;
    }

    public JSONObject saveImage(String name)
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "saveImage"));
        params.add(new BasicNameValuePair("name", name));
        JSONObject json = jsonparser.makeHttpRequest(URL , params);
        return json;
    }

    public JSONObject deleteProfile(String email)
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "deleteProfile"));
        params.add(new BasicNameValuePair("email", email));
        JSONObject json = jsonparser.makeHttpRequest(URL , params);
        return json;
    }

    public JSONObject getMainCategories()
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "getMainCategories"));
        JSONObject json = jsonparser.makeHttpRequest(URL , params);
        return json;
    }

    public JSONObject getSubcategories(String id)
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "getSubcategories"));
        params.add(new BasicNameValuePair("id", id));
        JSONObject json = jsonparser.makeHttpRequest(URL , params);
        return json;
    }

    public JSONObject getLocations()
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "locations"));
        JSONObject json = jsonparser.makeHttpRequest(URL , params);
        return json;
    }

    public JSONObject getCategoryProducts(String id , String limit,String language)
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "getCategoryProducts"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("startID", limit));
        params.add(new BasicNameValuePair("language", language));
        JSONObject json = jsonparser.makeHttpRequest(URL , params);
        return json;
    }

    public JSONObject getOccassionProducts(String id , String limit)
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "getCategoryProducts"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("startID", limit));
        JSONObject json = jsonparser.makeHttpRequest(URL , params);
        return json;
    }

    public JSONObject DefaultShippingBillingAddress(String email , String is_default ,String fname ,String lname ,String stname ,String stnum ,String addinfo ,String city ,String company ,String zip ,String country ,String tel ,String fax )
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "setDefaultShippingBillingAddress"));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("is_default", is_default));
        params.add(new BasicNameValuePair("fname", fname));
        params.add(new BasicNameValuePair("lname", lname));
        params.add(new BasicNameValuePair("stname", stname));
        params.add(new BasicNameValuePair("stnum", stnum));
        params.add(new BasicNameValuePair("addinfo", addinfo));
        params.add(new BasicNameValuePair("city", city));
        params.add(new BasicNameValuePair("company", company));
        params.add(new BasicNameValuePair("zip", zip));
        params.add(new BasicNameValuePair("country", country));
        params.add(new BasicNameValuePair("tel", tel));
        params.add(new BasicNameValuePair("fax", fax));
        JSONObject json = jsonparser.makeHttpRequest(URL , params);
        return json;
    }

    public JSONObject getAllAddresses(String email )
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "getAllAddresses"));
        params.add(new BasicNameValuePair("email", email));
        JSONObject json = jsonparser.makeHttpRequest(URL , params);
        return json;
    }

    public JSONObject addToCart( String ids , String qtys ,String email )
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "addtocart"));
        params.add(new BasicNameValuePair("ids", ids));
        params.add(new BasicNameValuePair("qtys", qtys));
        params.add(new BasicNameValuePair("email", email));
        JSONObject json = jsonparser.makeHttpRequest(URL , params);
        return json;
    }

    public JSONObject deleteCart( String email )
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "deleteCart"));
        params.add(new BasicNameValuePair("email", email));
        JSONObject json = jsonparser.makeHttpRequest(URL , params);
        return json;
    }

    public JSONObject checkout( String email, String total, String address, String couponCode )
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "checkout"));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("total", total));
        params.add(new BasicNameValuePair("address", address));
        params.add(new BasicNameValuePair("couponCode", couponCode));
        JSONObject json = jsonparser.makeHttpRequest(URL , params);
        return json;
    }

    public JSONObject checkout_BankTransfer( String email, String total, String address, String couponCode )
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "BankTransfer"));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("total", total));
        params.add(new BasicNameValuePair("address", address));
        params.add(new BasicNameValuePair("couponCode", couponCode));
        JSONObject json = jsonparser.makeHttpRequest(URL , params);
        return json;
    }

    public JSONObject checkout_gate2play( String email, String total, String address, String couponCode )
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "creditCard"));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("total", total));
        params.add(new BasicNameValuePair("address", address));
        params.add(new BasicNameValuePair("couponCode", couponCode));
        JSONObject json = jsonparser.makeHttpRequest(URL , params);
        return json;
    }

    public JSONObject checkout_paypal( String email, String total, String address, String couponCode )
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "paypal"));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("total", total));
        params.add(new BasicNameValuePair("address", address));
        params.add(new BasicNameValuePair("couponCode", couponCode));
        JSONObject json = jsonparser.makeHttpRequest(URL , params);
        return json;
    }

    public JSONObject getCountries( )
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "getCountries"));
        JSONObject json = jsonparser.makeHttpRequest(URL , params);
        return json;
    }

    public JSONObject searchCategoryProducts(String id , String limit , String searchKey)
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "searchCategoryProducts"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("startID", limit));
        params.add(new BasicNameValuePair("searchKey", searchKey));
        JSONObject json = jsonparser.makeHttpRequest(URL , params);
        return json;
    }

    public JSONObject searchAllProducts(String id , String limit , String searchKey)
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "searchAllProducts"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("startID", limit));
        params.add(new BasicNameValuePair("searchKey", searchKey));
        JSONObject json = jsonparser.makeHttpRequest(URL , params);
        return json;
    }

    public JSONObject autocomplete(String id , String limit , String searchKey)
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "autocomplete"));
        params.add(new BasicNameValuePair("id", id));
        params.add(new BasicNameValuePair("startID", limit));
        params.add(new BasicNameValuePair("searchKey", searchKey));
        JSONObject json = jsonparser.makeHttpRequest(URL , params);
        return json;
    }

    public JSONObject productData(String id)
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "productData"));
        params.add(new BasicNameValuePair("id", id));
        JSONObject json = jsonparser.makeHttpRequest(URL , params);
        return json;
    }

    public JSONObject setCouponCode(String couponCode)
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "setCouponCode"));
        params.add(new BasicNameValuePair("couponCode", couponCode));
        JSONObject json = jsonparser.makeHttpRequest(URL , params);
        return json;
    }

    public JSONObject creditcardCheckout(String amount)
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "creditcardCheckout"));
        params.add(new BasicNameValuePair("amount", amount));
        JSONObject json = jsonparser.makeHttpRequest(URL , params);
        return json;
    }

    public JSONObject getPaymentStatus(String id)
    {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", "getPaymentStatus"));
        params.add(new BasicNameValuePair("id", id));
        JSONObject json = jsonparser.makeHttpRequest(URL , params);
        return json;
    }

}
