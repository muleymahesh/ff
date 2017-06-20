package com.farmfresh24.delivery.model;

/**
 * Created by maks on 16/6/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OrderDetail implements Serializable {

    @SerializedName("qty")
    @Expose
    private String qty;


    @SerializedName("img_url")
    @Expose
    private String imgUrl;

    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("mrp")
    @Expose
    private String mrp;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    /**
     * @return The qty
     */
    public String getQty() {
        return qty;
    }

    /**
     * @param qty The qty
     */
    public void setQty(String qty) {
        this.qty = qty;
    }

    /**
     * @return The productName
     */
    public String getProductName() {
        return productName;
    }

    /**
     * @param productName The product_name
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * @return The mrp
     */
    public String getMrp() {
        return mrp;
    }

    /**
     * @param mrp The mrp
     */
    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

}
