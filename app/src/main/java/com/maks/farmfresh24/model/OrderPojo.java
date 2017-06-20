package com.maks.farmfresh24.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class OrderPojo {

    @SerializedName("o_id")
    @Expose
    private String oId;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("shipping_type")
    @Expose
    private String shippingType;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("order_status")
    @Expose
    private String order_status;
    @SerializedName("details")
    @Expose
    private ArrayList<OrderDetail> details = new ArrayList<OrderDetail>();

    /**
     * @return The oId
     */
    public String getOId() {
        return oId;
    }

    /**
     * @param oId The o_id
     */
    public void setOId(String oId) {
        this.oId = oId;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    /**
     * @return The amount
     */
    public String getAmount() {
        return amount;
    }

    /**
     * @param amount The amount
     */
    public void setAmount(String amount) {
        this.amount = amount;
    }

    /**
     * @return The shippingType
     */
    public String getShippingType() {
        return shippingType;
    }

    /**
     * @param shippingType The shipping_type
     */
    public void setShippingType(String shippingType) {
        this.shippingType = shippingType;
    }

    /**
     * @return The date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date The date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return The details
     */
    public List<OrderDetail> getDetails() {
        return details;
    }

    /**
     * @param details The details
     */
    public void setDetails(ArrayList<OrderDetail> details) {
        this.details = details;
    }

}