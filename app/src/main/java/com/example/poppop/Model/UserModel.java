package com.example.poppop.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UserModel implements Parcelable {
    String userId, name, profile, gender, bio, fcmToken, horoscopeSign, photoUrl;
    Integer age, numOfImages;
    GeoPoint currentLocation;
    List<String> liked_list;
    List<String> disliked_list;
    List<String> swiped_list;
    List<String> interests;
    List<ImageModel> image_list;
    Boolean isPremium;

    protected UserModel(Parcel in) {
        userId = in.readString();
        name = in.readString();
        profile = in.readString();
        gender = in.readString();
        bio = in.readString();
        fcmToken = in.readString();
        horoscopeSign = in.readString();
        if (in.readByte() == 0) {
            age = null;
        } else {
            age = in.readInt();
        }
        liked_list = in.createStringArrayList();
        interests = in.createStringArrayList();
        disliked_list = in.createStringArrayList();
        swiped_list = in.createStringArrayList();
        image_list = in.createTypedArrayList(ImageModel.CREATOR);
        byte tmpIsPremium = in.readByte();
        isPremium = tmpIsPremium == 0 ? null : tmpIsPremium == 1;
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    public List<ImageModel> getImage_list() {
        return image_list;
    }

    public void setImage_list(List<ImageModel> image_list) {
        this.image_list = image_list;
    }

    public Integer getNumOfImages() {
        return numOfImages;
    }

    public void setNumOfImages(Integer numOfImages) {
        this.numOfImages = numOfImages;
    }

    public Boolean getPremium() {
        return isPremium;
    }

    public void setPremium(Boolean premium) {
        isPremium = premium;
    }

    public UserModel() {
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public UserModel(String userId, String name, String profile) {
        this.userId = userId;
        this.name = name;
        this.profile = profile;
    }

    public UserModel(String name,int age, String photoUrl) {
        this.age = age;
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getHoroscopeSign() {
        return horoscopeSign;
    }

    public void setHoroscopeSign(String horoscopeSign) {
        this.horoscopeSign = horoscopeSign;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public GeoPoint getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(GeoPoint currentLocation) {
        this.currentLocation = currentLocation;
    }

    public List<String> getLiked_list() {
        return liked_list;
    }

    public void setLiked_list(List<String> liked_list) {
        this.liked_list = liked_list;
    }

    public List<String> getDisliked_list() {
        return disliked_list;
    }

    public void setDisliked_list(List<String> disliked_list) {
        this.disliked_list = disliked_list;
    }

    public List<String> getSwiped_list() {
        return swiped_list;
    }

    public void setSwiped_list(List<String> swiped_list) {
        this.swiped_list = swiped_list;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(name);
        dest.writeString(profile);
        dest.writeString(gender);
        dest.writeString(bio);
        dest.writeString(fcmToken);
        dest.writeString(horoscopeSign);
        if (age == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(age);
        }
        if (numOfImages == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(numOfImages);
        }
        dest.writeStringList(liked_list);
        dest.writeStringList(interests);
        dest.writeStringList(disliked_list);
        dest.writeStringList(swiped_list);
        dest.writeTypedList(image_list);
        dest.writeByte((byte) (isPremium == null ? 0 : isPremium ? 1 : 2));
    }
}
