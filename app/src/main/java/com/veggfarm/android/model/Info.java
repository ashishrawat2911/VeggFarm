package com.veggfarm.android.model;

/**
 * created by Ashish Rawat
 */


public class Info {
    private String name;
    private String email;
    private String address;
    private String pincode;
    private String phoneNumber;

    public Info() {
    }

    public Info(String name, String email, String address, String pincode, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.pincode = pincode;
        this.phoneNumber = phoneNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getPincode() {
        return pincode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }


}
