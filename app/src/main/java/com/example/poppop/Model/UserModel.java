package com.example.poppop.Model;

import com.google.firebase.firestore.GeoPoint;

import java.util.List;
import java.util.Map;

public class UserModel {
    String userId, name, profile, gender, bio, fcmToken, horoscopeSign;
    Integer age;
    GeoPoint currentLocation;
    Map<String, Boolean> interests;
    List<String> liked_list, disliked_list, swiped_list;
    Boolean isPremium;

    public Boolean getPremium() {
        return isPremium;
    }

    public void setPremium(Boolean premium) {
        isPremium = premium;
    }

    public UserModel() {
    }

    public UserModel(String userId, String name, String profile) {
        this.userId = userId;
        this.name = name;
        this.profile = profile;
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

    public Map<String, Boolean> getInterests() {
        return interests;
    }

    public void setInterests(Map<String, Boolean> interests) {
        this.interests = interests;
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
}
