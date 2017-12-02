package com.ecommerce.dell.mcommerce.models;

import com.ecommerce.dell.mcommerce.utility.DBService;

import java.util.ArrayList;


public class Global {

	public static ArrayList<StoreData> storeDataArrayList = new ArrayList<StoreData>();
	public static String strShippingAddress = "";
	public static String strShipmentUsername = "";
	public static String strShipmentAddress = "";
	public static String strShipmentTelephone = "";
	public static DBService dbService = null;

	public static String strCouponCode = "";
	public static float discountPercent = 0.0f;

	public static final String PAYMENT_CASH = "cash";
	public static final String PAYMENT_BANK = "bank";
	public static final String PAYMENT_PAYPAL = "paypal";
	public static final String PAYMENT_VISA_CARD = "VisaCard";
	public static final String PAYMENT_MASTER_CARD = "MasterCard";

	/*
	 * Local DB Info.
	 */

	public static final String LOCAL_DB_NAME                            = "ara-----------b";
	public static final String LOCAL_TABLE_ORDERS                       = "or----------le";

	public static final String LOCAL_FIELD_ORDER_ID                     = "orderID";
	public static final String LOCAL_FIELD_EMAIL                        = "email";
	public static final String LOCAL_FIELD_PRODUCT_NAME                 = "productName";
	public static final String LOCAL_FIELD_PRODUCT_AMOUNT               = "productAmount";
	public static final String LOCAL_FIELD_PRODUCT_SKU                  = "productSKU";
	public static final String LOCAL_FIELD_PRODUCT_PRICE                = "productPrice";
	public static final String LOCAL_FIELD_PRODUCT_IMAGE                = "productImage";


}
