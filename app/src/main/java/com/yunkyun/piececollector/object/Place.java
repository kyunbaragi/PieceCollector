package com.yunkyun.piececollector.object;

import com.google.gson.annotations.SerializedName;

/**
 * Created by YunKyun on 2017-07-29.
 */

public class Place {
    @SerializedName("_id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;
    @SerializedName("area_code")
    private int areaCode;
    @SerializedName("local_code")
    private int localCode;
    @SerializedName("image_path")
    private String imagePath;
    @SerializedName("type")
    private int type;
    @SerializedName("cat1")
    private String cat1;
    @SerializedName("cat2")
    private String cat2;
    @SerializedName("cat3")
    private String cat3;
    @SerializedName("collection_id")
    private int collectionID;
    private int visited;

    public Place() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(int areaCode) {
        this.areaCode = areaCode;
    }

    public int getLocalCode() {
        return localCode;
    }

    public void setLocalCode(int localCode) {
        this.localCode = localCode;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCat1() {
        return cat1;
    }

    public void setCat1(String cat1) {
        this.cat1 = cat1;
    }

    public String getCat2() {
        return cat2;
    }

    public void setCat2(String cat2) {
        this.cat2 = cat2;
    }

    public String getCat3() {
        return cat3;
    }

    public void setCat3(String cat3) {
        this.cat3 = cat3;
    }

    public int getCollectionID() {
        return collectionID;
    }

    public void setCollectionID(int collectionID) {
        this.collectionID = collectionID;
    }

    public int getVisited() {
        return visited;
    }

    public void setVisited(int visited) {
        this.visited = visited;
    }

    @Override
    public String toString() {
        return "Place{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", areaCode=" + areaCode +
                ", localCode=" + localCode +
                ", imagePath='" + imagePath + '\'' +
                ", type=" + type +
                ", cat1='" + cat1 + '\'' +
                ", cat2='" + cat2 + '\'' +
                ", cat3='" + cat3 + '\'' +
                ", collectionID=" + collectionID +
                ", visited=" + visited +
                '}';
    }
}
