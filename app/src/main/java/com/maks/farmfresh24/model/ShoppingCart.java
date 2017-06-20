package com.maks.farmfresh24.model;

import java.io.Serializable;

/**
 * Created by Dell on 13/02/2016.
 */
public class ShoppingCart implements Serializable{
    private String id;
    private String product_id;
   private Product product;
    private String quantity;

    public ShoppingCart() {
    }

    public ShoppingCart(String product_id, Product product, String quantity) {
        this.product_id = product_id;
        this.product = product;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
