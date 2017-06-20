package com.farmfresh24.delivery.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Dell on 11/03/2016.
 */
public class AppPreferences {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public AppPreferences(Context context) {
        // TODO Auto-generated constructor stub
        sharedPreferences = context.getSharedPreferences("FarmFresh24",
                Context.MODE_PRIVATE);
    }

    public boolean isLogin() {
        return sharedPreferences.getBoolean("login", false);

    }

    public void logout() {
        SharedPreferences.Editor et = sharedPreferences.edit();
        et.clear();
        et.commit();
    }

    public void setLogin(boolean flag) {
        SharedPreferences.Editor et = sharedPreferences.edit();
        et.putBoolean("login", flag);
        et.commit();

    }

    public String getEmail() {
        return sharedPreferences.getString("email", "");
    }

    public void setEmail(String email) {

        SharedPreferences.Editor et = sharedPreferences.edit();
        et.putString("email", email);
        et.commit();
    }

    public String getFname() {
        return sharedPreferences.getString("fname", "Guest");
    }

    public void setFname(String fname) {

        SharedPreferences.Editor et = sharedPreferences.edit();
        et.putString("fname", fname);
        et.commit();
    }


    public String getWalletAmount() {
        return sharedPreferences.getString("wallet", "0");
    }

    public void setWalletAmount(String amount) {

        SharedPreferences.Editor et = sharedPreferences.edit();
        et.putString("wallet", amount);
        et.commit();
    }

//    public boolean getFirstOrderStatus() {
//        return sharedPreferences.getBoolean("isFirstOrder", true);
//    }
//
//    public void setFirstOrderStatus(boolean status) {
//
//        SharedPreferences.Editor et = sharedPreferences.edit();
//        et.putBoolean("isFirstOrder", status);
//        et.commit();
//    }

    public int getOrderCount() {
        return sharedPreferences.getInt("OrderCount", 0);
    }

    public void setOrderCount(int status) {

        SharedPreferences.Editor et = sharedPreferences.edit();
        et.putInt("OrderCount", status);
        et.commit();
    }


    public String getFirstOrderAmount() {
        return sharedPreferences.getString("isFirstAmount", "0");
    }

    public void setFirstOrderAmount(String status) {

        SharedPreferences.Editor et = sharedPreferences.edit();
        et.putString("isFirstAmount", status);
        et.commit();
    }
}