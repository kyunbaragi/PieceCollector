package com.yunkyun.piececollector.object;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by YunKyun on 2017-08-03.
 */

public class Record implements Parcelable {
    @SerializedName("_id")
    private Long id;
    @SerializedName("title")
    private String title;
    @SerializedName("image_path")
    private String imagePath;
    @SerializedName("memo")
    private String memo;
    @SerializedName("created")
    private Date created;

    public Record(Parcel in) {
        readFromParcel(in);
    }

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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", memo='" + memo + '\'' +
                ", created='" + created.toString() + '\'' +
                '}';
    }

    private void readFromParcel(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.imagePath = in.readString();
        this.memo = in.readString();
        int year = in.readInt();
        int month = in.readInt();
        int day = in.readInt();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        this.created = new Date(calendar.getTimeInMillis());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(imagePath);
        dest.writeString(memo);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(created);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        dest.writeInt(year);
        dest.writeInt(month);
        dest.writeInt(day);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        @Override
        public Record createFromParcel(Parcel source) {
            return new Record(source);
        }

        @Override
        public Record[] newArray(int size) {
            return new Record[size];
        }
    };
}
