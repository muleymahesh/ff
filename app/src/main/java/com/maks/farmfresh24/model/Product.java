package com.maks.farmfresh24.model;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.maks.farmfresh24.utils.Constants;


import java.io.Serializable;
import java.util.List;

/**
 * Created by maks on 7/2/16.
 */
public class Product  implements Serializable
{
    @SerializedName("p_id")
    String p_id;
    @SerializedName("product_name")
    String product_name;
    @SerializedName("img_url")
    String img_url;
    @SerializedName("short_desc")
    String short_desc;
    @SerializedName("long_desc")
    String long_desc;
    @SerializedName("brand_id")
    String brand_id;
    @SerializedName("cat_id")
    String cat_id;
    @SerializedName("sub_cat_id")
    String sub_cat_id;
    @SerializedName("offer_id")
    String offer_id;
    @SerializedName("mrp")
    String mrp;
    @SerializedName("weight")
    String weight;
    @SerializedName("size")
    String size;
    @SerializedName("status")
    String status;
    @SerializedName("expiry_date")
    String expiry_date;
    @SerializedName("brand_name")
    String brand_name;
    @SerializedName("cat_name")
    String cat_name;
    @SerializedName("sub_name")
    String sub_name;
    @SerializedName("offer_name")
    String offer_name;
    @SerializedName("per_discount")
    String per_discount;
    @SerializedName("stock")
    String stock;
    @SerializedName("is_fav")
    String isFav;

    @SerializedName("imgs")
    List<ProductImage> imgs;
    public List<ProductImage> getImgs() {
        return imgs;
    }

    public void setImgs(List<ProductImage> imgs) {
        this.imgs = imgs;
        Log.e("img",imgs.toString());
    }

    public String getIsFav() {
        return isFav;
    }

    public void setIsFav(String isFav) {
        this.isFav = isFav;
    }

    public String getP_id() {
        return p_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getImg_url() {
        return Constants.PRODUCT_IMG_PATH+img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getShort_desc() {
        return short_desc;
    }

    public void setShort_desc(String short_desc) {
        this.short_desc = short_desc;
    }

    public String getLong_desc() {
        return long_desc;
    }

    public void setLong_desc(String long_desc) {
        this.long_desc = long_desc;
    }

    public String getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(String brand_id) {
        this.brand_id = brand_id;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getSub_cat_id() {
        return sub_cat_id;
    }

    public void setSub_cat_id(String sub_cat_id) {
        this.sub_cat_id = sub_cat_id;
    }

    public String getOffer_id() {
        return offer_id;
    }

    public void setOffer_id(String offer_id) {
        this.offer_id = offer_id;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    public String getSub_name() {
        return sub_name;
    }

    public void setSub_name(String sub_name) {
        this.sub_name = sub_name;
    }

    public String getOffer_name() {
        return offer_name;
    }

    public void setOffer_name(String offer_name) {
        this.offer_name = offer_name;
    }

    public String getPer_discount() {return per_discount;}

    public void setPer_discount(String per_discount) {
        if(per_discount!=null)
        this.per_discount = per_discount;
    else
            this.per_discount =""+0;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }
}
