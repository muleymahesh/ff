package com.maks.farmfresh24.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by maks on 12/6/16.
 */
public class ProductImage implements Serializable{

    @SerializedName("id")
    String id;
    @SerializedName("p_id")
    String p_id;
    @SerializedName("img_url")
    String img_url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getP_id() {
        return p_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}
