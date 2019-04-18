package com.veggfarm.android.model;

/**
 * created by Ashish Rawat
 */


public class ReqInfo {
    public String name;
    public String email;
    public String address;
    public String pincode;
    public String phoneNumber;
    public String uId;

    public ReqInfo() {
    }

    public ReqInfo(String name, String email, String address, String pincode, String phoneNumber, String uId) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.pincode = pincode;
        this.phoneNumber = phoneNumber;
        this.uId = uId;
    }
}