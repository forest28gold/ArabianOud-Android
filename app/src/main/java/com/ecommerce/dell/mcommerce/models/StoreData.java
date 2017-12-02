package com.ecommerce.dell.mcommerce.models;

public class StoreData {

    private String name = "";
    private String displayaddress = "";
    private String zipcode = "";
    private String city = "";
    private String state = "";
    private String country_id = "";
    private String phone = "";
    private String lat = "";
    private String lng = "";


    public StoreData(String lname, String ldisplay, String lzipCode, String lcity, String lstate, String lcountry_id, String lphone , String llat , String llng){

        this.name = lname;
        this.displayaddress = ldisplay;
        this.zipcode = lzipCode;
        this.city = lcity;
        this.state = lstate;
        this.country_id = lcountry_id;
        this.phone = lphone;
        this.lat=llat;
        this.lng = llng;


    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayaddress() {
        return displayaddress;
    }

    public void setDisplayaddress(String displayaddress) {
        this.displayaddress = displayaddress;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
