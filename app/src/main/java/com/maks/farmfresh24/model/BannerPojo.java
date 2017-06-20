package com.maks.farmfresh24.model;

/**
 * Created by maks on 11/6/16.
 */

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class BannerPojo {

    @SerializedName("banner_id")
    private String bannerId;
    @SerializedName("banner_name")
    private String bannerName;
    @SerializedName("image_path")
    private String imagePath;

    /**
     *
     * @return
     * The bannerId
     */
    public String getBannerId() {
        return bannerId;
    }

    /**
     *
     * @param bannerId
     * The banner_id
     */
    public void setBannerId(String bannerId) {
        this.bannerId = bannerId;
    }

    /**
     *
     * @return
     * The bannerName
     */
    public String getBannerName() {
        return bannerName;
    }

    /**
     *
     * @param bannerName
     * The banner_name
     */
    public void setBannerName(String bannerName) {
        this.bannerName = bannerName;
    }

    /**
     *
     * @return
     * The imagePath
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     *
     * @param imagePath
     * The image_path
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

}