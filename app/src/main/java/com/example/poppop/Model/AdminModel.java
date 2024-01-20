package com.example.poppop.Model;

public class AdminModel {
    String name, adminId;

    public AdminModel() {
    }

    public AdminModel(String name, String adminId) {
        this.name = name;
        this.adminId = adminId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }
}
