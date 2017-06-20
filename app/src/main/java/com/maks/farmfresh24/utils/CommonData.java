package com.maks.farmfresh24.utils;

import java.util.ArrayList;

/**
 * Created by Dell on 13/02/2016.
 */
public class CommonData {

    public static ArrayList<String> addToCartList;

    public static ArrayList<String> getAddToCartList() {
        return addToCartList;
    }

    public static void setAddToCartList(ArrayList<String> addToCartList) {
        CommonData.addToCartList = addToCartList;
    }
}
