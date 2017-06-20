package com.maks.farmfresh24.model;

/**
 * Created by maks on 11/6/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HomepageDTO {

    @SerializedName("result")
    String result;
    @SerializedName("responseCode")
    String responseCode;
    @SerializedName("data")
    List<BannerPojo> data;
    @SerializedName("new_data")
//    List<Product> new_data;
            List<Category> new_data;
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public List<BannerPojo> getData() {
        return data;
    }

    public void setData(List<BannerPojo> data) {
        this.data = data;
    }

    public List<Category> getNew_data() {
        return new_data;
    }

    public void setNew_data(List<Category> new_data) {
        this.new_data = new_data;
    }
}