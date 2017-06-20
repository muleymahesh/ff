package com.maks.farmfresh24.dbutils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dell on 17/02/2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    Context context;
    public static final String DB_NAME = "shopping_cart.db";
    public static final int DB_VERSION =1;

    DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table tbl_cart(id INTEGER PRIMARY KEY AUTOINCREMENT, p_id INTEGER, product BLOB,quantity INTEGER);");
        db.execSQL("create table tbl_address(id INTEGER PRIMARY KEY AUTOINCREMENT,fname text,lname text,email text,phone text,area text,addr text, landmark text, zipcode int)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table tbl_cart");
        onCreate(db);
    }
}
