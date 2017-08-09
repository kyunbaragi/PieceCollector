package com.yunkyun.piececollector.object;

import com.google.gson.annotations.SerializedName;

/**
 * Created by YunKyun on 2017-08-03.
 */

public class Record {
    @SerializedName("_id")
    private Long id;
    @SerializedName("title")
    private String title;
    @SerializedName("image_path")
    private String imagePath;
    @SerializedName("memo")
    private String memo;
    @SerializedName("created")
    private String created;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", memo='" + memo + '\'' +
                ", created='" + created + '\'' +
                '}';
    }
}
