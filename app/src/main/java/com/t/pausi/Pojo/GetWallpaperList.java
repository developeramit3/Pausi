package com.t.pausi.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetWallpaperList {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("wallpaper_image")
    @Expose
    private String wallpaperImage;
    @SerializedName("created_date")
    @Expose
    private String createdDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWallpaperImage() {
        return wallpaperImage;
    }

    public void setWallpaperImage(String wallpaperImage) {
        this.wallpaperImage = wallpaperImage;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
