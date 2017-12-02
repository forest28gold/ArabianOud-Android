package com.ecommerce.dell.mcommerce.utility;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ecommerce.dell.mcommerce.models.Global;

public class DBService extends SQLiteOpenHelper {

    private final static int DATABASE_VERSION = 3;
    private final static String DATABASE_NAME = Global.LOCAL_DB_NAME;

    public void onCreate(SQLiteDatabase db)
    {
        String sql_order = "CREATE TABLE [" + Global.LOCAL_TABLE_ORDERS + "] ("
                + "[id] AUTOINC,"
                + "[" + Global.LOCAL_FIELD_ORDER_ID + "] TEXT NOT NULL ON CONFLICT FAIL,"
                + "[" + Global.LOCAL_FIELD_EMAIL + "] TEXT ,"
                + "[" + Global.LOCAL_FIELD_PRODUCT_NAME + "] TEXT ,"
                + "[" + Global.LOCAL_FIELD_PRODUCT_AMOUNT + "] TEXT ,"
                + "[" + Global.LOCAL_FIELD_PRODUCT_SKU + "] TEXT ,"
                + "[" + Global.LOCAL_FIELD_PRODUCT_PRICE + "] TEXT ,"
                + "[" + Global.LOCAL_FIELD_PRODUCT_IMAGE + "] TEXT )";

        db.execSQL(sql_order);
    }

    public DBService(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        String sql_order = "drop table if exists [" + Global.LOCAL_TABLE_ORDERS + "]";
        db.execSQL(sql_order);

        onCreate(db);
    }

    public void execSQL(String sql, Object[] args)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql, args);
    }

    public Cursor query(String sql, String[] args)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, args);
        return cursor;
    }


}
