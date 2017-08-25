package com.yunkyun.piececollector.object;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by YunKyun on 2017-08-02.
 */

public class Collection implements Parcelable {
    @SerializedName("_id")
    private Long id;
    @SerializedName("title")
    private String title;
    @SerializedName("prologue")
    private String prologue;
    @SerializedName("description")
    private String description;
    @SerializedName("tag")
    private String tag;
    @SerializedName("image_path")
    private String imagePath;

    public Collection(Parcel in) {
        readFromParcel(in);
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrologue() {
        return prologue;
    }

    public void setPrologue(String prologue) {
        this.prologue = prologue;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "Collection{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", prologue='" + prologue + '\'' +
                ", description='" + description + '\'' +
                ", tag='" + tag + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private void readFromParcel(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.prologue = in.readString();
        this.description = in.readString();
        this.tag = in.readString();
        this.imagePath = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(prologue);
        dest.writeString(description);
        dest.writeString(tag);
        dest.writeString(imagePath);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        @Override
        public Collection createFromParcel(Parcel source) {
            return new Collection(source);
        }

        @Override
        public Collection[] newArray(int size) {
            return new Collection[size];
        }
    };
}
