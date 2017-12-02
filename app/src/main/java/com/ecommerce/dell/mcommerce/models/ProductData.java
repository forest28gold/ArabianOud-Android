package com.ecommerce.dell.mcommerce.models;

public class ProductData {

    private String pName="";
    private String pID="";
    private String pPrice="";
    private String pDiscount="";
    private String pDescription="";
    private String pImage="";
    private String pPercentage="";
    private String pURL = "";
    private String pSKU = "";
    private String pSize = "";
    private String pType="";
    private String pOrigin = "";
    private String pFragrance = "";

    public String getpSKU() {
        return pSKU;
    }

    public void setpSKU(String pSKU) {
        this.pSKU = pSKU;
    }

    public String getpSize() {
        return pSize;
    }

    public void setpSize(String pSize) {
        this.pSize = pSize;
    }

    public String getpType() {
        return pType;
    }

    public void setpType(String pType) {
        this.pType = pType;
    }

    public String getpOrigin() {
        return pOrigin;
    }

    public void setpOrigin(String pPerfumefor) {
        this.pOrigin = pPerfumefor;
    }

    public String getpFragrance() {
        return pFragrance;
    }

    public void setpFragrance(String pFragrance) {
        this.pFragrance = pFragrance;
    }

    public String getpURL() {
        return pURL;
    }

    public void setpURL(String pURL) {
        this.pURL = pURL;
    }



    public ProductData() {

    }
    public ProductData(String pName, String pID, String pPrice, String pDiscount, String pDescription, String pImage, String pPercentage, String PURL, String pSKU , String pSize , String pType,
                       String Origin, String pFragrance ) {
        this.pName = pName;
        this.pID = pID;
        this.pPrice = String.valueOf(Float.parseFloat(pPrice));
        this.pDiscount = String.valueOf(Float.parseFloat(pDiscount));
        this.pDescription = pDescription;
        this.pImage = pImage;
        this.pPercentage = pPercentage;
        this.pURL = PURL;
        this.pSKU = pSKU;
        this.pSize = pSize;
        this.pType = pType;
        this.pOrigin = Origin;
        this.pFragrance = pFragrance;


    }

    public void setproductName(String name)
    {
        this.pName=name;
    }
    public void setProductID(String id){
        this.pID=id;
    }
    public void setProductPrice(String price){
        this.pPrice=price;
    }
    public void setProductDiscount(String discount){
        this.pDiscount=discount;
    }
    public void setProductDescription(String description){
        this.pDescription=description;
    }
    public void setProductImage(String image){
        this.pImage=image;
    }
    public void setProductPercentage(String percentage){
        this.pPercentage=percentage;
    }

    public String getProductName(){return this.pName;}
    public String getProductID(){return this.pID;}
    public String getProductPrice(){return this.pPrice;}
    public String getProductDiscount(){return this.pDiscount;}
    public String getProductDescreiption(){return this.pDescription;}
    public String getProductImage(){return this.pImage;}
    public String getProductPercentage(){return this.pPercentage;}
}
