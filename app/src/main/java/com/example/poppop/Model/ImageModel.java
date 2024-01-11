package com.example.poppop.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageModel implements Parcelable {
    String url, name;

    public ImageModel() {
    }

    public ImageModel(String url, String ref) {
        this.url = url;
        this.name = ref;
    }

    protected ImageModel(Parcel in) {
        url = in.readString();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageModel> CREATOR = new Creator<ImageModel>() {
        @Override
        public ImageModel createFromParcel(Parcel in) {
            return new ImageModel(in);
        }

        @Override
        public ImageModel[] newArray(int size) {
            return new ImageModel[size];
        }
    };

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
