package com.maks.farmfresh24.dbutils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.maks.farmfresh24.model.Address;
import com.maks.farmfresh24.model.ShoppingCart;

import java.util.ArrayList;

/**
 * Created by Dell on 21/02/2016.
 */
public class SQLiteUtil {

    public static final String PRODUCT_ID="p_id";
    public static final String PRODUCT_BLOB="product";
    public static final String QUANTITY="quantity";
    public static final String KEY_DATA="id";



    public void insert(ShoppingCart shoppingCart,Context context){
        Gson gson = new Gson();

        ArrayList<ShoppingCart> arr =  getData(context);

        boolean exist = false;
        String id = "null";
        for (ShoppingCart sh:arr) {
            if(sh.getProduct_id().equals(shoppingCart.getProduct_id())){
                int q1 = Integer.parseInt(shoppingCart.getQuantity());
                int q2 = Integer.parseInt(sh.getQuantity());
                shoppingCart.setQuantity(""+(q1+q2));

                exist=true;
                id = sh.getId();

//                if((q1+q2)> (Integer.parseInt(shoppingCart.getProduct().getStock()))){
//                    Toast.makeText(context , "Quantity exceeds stock", Toast.LENGTH_SHORT).show();
//                return;
//                }
            }
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(PRODUCT_ID, shoppingCart.getProduct_id());
        contentValues.put(PRODUCT_BLOB, gson.toJson(shoppingCart).getBytes());
        contentValues.put(QUANTITY,shoppingCart.getQuantity());
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(exist){
            contentValues.put("id",id);
            db.insertWithOnConflict("tbl_cart",null,contentValues,SQLiteDatabase.CONFLICT_REPLACE);
        }else{
            db.insert("tbl_cart", "", contentValues);
        }
        db.close();

    }

    public void addAddress(Address address, Context context){

        ContentValues contentValues = new ContentValues();
        contentValues.put("fname",address.getFname());
        contentValues.put("lname",address.getLname());
        contentValues.put("phone",address.getPhone());
        contentValues.put("email",address.getEmail());
        contentValues.put("area", address.getArea());
        contentValues.put("addr",address.getAddr());
        contentValues.put("zipcode", address.getZipcode());
        contentValues.put("landmark", address.getLandmark());

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL("delete from tbl_address");

        db.insert("tbl_address", "", contentValues);
        db.close();

    }



    public ArrayList<Address> getAddressList(Context ctx){
        DBHelper dbHelper = new DBHelper(ctx);
        ArrayList<Address> arr = new ArrayList<Address>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor=db.rawQuery("select * from tbl_address", null);
        if (cursor!=null && cursor.moveToFirst()) {
            do {

                    Address address = new Address();
                address.setId(cursor.getInt(0));
                address.setFname(cursor.getString(1));
                address.setLname(cursor.getString(2));
                address.setEmail(cursor.getString(3));
                address.setPhone(cursor.getString(4));
                address.setArea(cursor.getString(5));
                address.setAddr(cursor.getString(6));
                address.setLandmark(cursor.getString(7));
                address.setZipcode(cursor.getString(8));

                arr.add(address);

            }while (cursor.moveToNext());
        }

        return arr;
    }


    public ArrayList<ShoppingCart> getData(Context ctx){
        DBHelper dbHelper = new DBHelper(ctx);
        ArrayList<ShoppingCart> arr = new ArrayList<ShoppingCart>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor=db.rawQuery("select * from tbl_cart", null);
        if (cursor!=null && cursor.moveToFirst()) {
            do {

                byte[] blob = cursor.getBlob(cursor.getColumnIndex(PRODUCT_BLOB));
                String json = new String(blob);
                Gson gson = new Gson();
                ShoppingCart shoppingCarts = gson.fromJson(json, ShoppingCart.class);
                shoppingCarts.setId(""+cursor.getInt(0));
                arr.add(shoppingCarts);
                Log.e("TAG","Shopping cart "+shoppingCarts.getQuantity() + " "+shoppingCarts.getId());

            }while (cursor.moveToNext());
        }
        db.close();
        return arr;
    }

    public ShoppingCart getCartItem(Context ctx, String p_id){
        DBHelper dbHelper = new DBHelper(ctx);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ShoppingCart shoppingCarts=null;
        Cursor cursor=db.rawQuery("select * from tbl_cart where p_id=?", new String[]{p_id});
        if (cursor!=null && cursor.moveToFirst()) {

                byte[] blob = cursor.getBlob(cursor.getColumnIndex(PRODUCT_BLOB));
                String json = new String(blob);
                Gson gson = new Gson();
                shoppingCarts = gson.fromJson(json, ShoppingCart.class);
                shoppingCarts.setId(""+cursor.getInt(0));
                shoppingCarts.setProduct_id(p_id);

        }
        db.close();
        return shoppingCarts;
    }


    public void deleteCartItem(String id,Context context){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from tbl_cart where id="+id);
        db.close();

    }


    public void modifyCartData(String id, String quantity, Context context){

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

            try {
                String sql = "update tbl_cart set quantity=? where id=?";
                Log.e("sql",sql);
                db.execSQL(sql,new String[]{quantity,id});

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                db.close();
            }
    }

    public void emptyCart(Context context){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from tbl_cart ");
        db.close();

    }

}
