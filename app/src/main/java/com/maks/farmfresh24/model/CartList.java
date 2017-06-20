package com.maks.farmfresh24.model;

import java.util.ArrayList;

/**
 * Created by Dell on 11/03/2016.
 */
public class CartList {
    private ArrayList<ShoppingCart> arrayListCart;
    private static CartList data = null;
    public static synchronized CartList getInstance() {

        if (data == null) {
            data = new CartList();
        }
        return data;

    }


    public ArrayList<ShoppingCart> getArrayListCart() {
        return arrayListCart;
    }

    public void setArrayListCart(ArrayList<ShoppingCart> arrayListCart) {
        this.arrayListCart = arrayListCart;
    }
}
