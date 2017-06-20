package com.maks.farmfresh24.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by maks on 7/2/16.
 */
public class ProductDTO implements Serializable
{

    @SerializedName("result")
    String result;
    @SerializedName("responseCode")
    String responseCode;
    @SerializedName("data")
    List<Product> data;

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

    public List<Product> getData() {
        return data;
    }

    public void setData(List<Product> data) {
        this.data = data;
    }
}
