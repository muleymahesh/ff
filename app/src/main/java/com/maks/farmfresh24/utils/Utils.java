package com.maks.farmfresh24.utils;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.Log;

/**
 * Created by Dell on 11/02/2016.
 */
public class Utils {

    public static Typeface setLatoFont(Activity activity) {
        Typeface tf = Typeface.createFromAsset(activity.getAssets(), Constants.LatoFont);
        return tf;
    }

    public static Typeface setLatoFontBold(Activity activity) {
        Typeface tf = Typeface.createFromAsset(activity.getAssets(), Constants.LatoFontBold);
        return tf;
    }

    public static Typeface setLatoFontLight(Activity activity) {
        Typeface tf = Typeface.createFromAsset(activity.getAssets(), Constants.LatoFontBlackLight);
        return tf;
    }
    public static Typeface setLatoFontHairLine(Activity activity) {
        Typeface tf = Typeface.createFromAsset(activity.getAssets(), Constants.LatoFontHairline);
        return tf;
    }

    public static String discountPrice(String strprice, String disc) {

        double price = Double.parseDouble(strprice);

        int dis = Integer.parseInt(disc);
        Log.e(""+price, ""+dis);

        double oneper = price/100;

        double rs1 = price - (oneper*dis);

        Log.e("final price",""+rs1);

        return ""+ Math.round(rs1);
    }
}
