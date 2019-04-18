package com.veggfarm.android.model;

import java.util.List;

/**
 * created by Ashish Rawat
 */

public class OrderItem {
    public Address address;
    public Double orderAmount;
    public String orderNumber;
    public String orderStatus;
    public String orderTime;
    public int totalNumber;
    public List<Cart> cartList;
    public String paymentMode;
    public String uID;


    public OrderItem() {
    }

    public OrderItem(Address address, Double orderAmount, String orderNumber, String orderStatus, String orderTime, int totalNumber, List<Cart> cartList, String paymentMode, String uID) {
        this.address = address;
        this.orderAmount = orderAmount;
        this.orderNumber = orderNumber;
        this.orderStatus = orderStatus;
        this.orderTime = orderTime;
        this.totalNumber = totalNumber;
        this.cartList = cartList;
        this.paymentMode = paymentMode;
        this.uID = uID;
    }

    public Address getAddress() {
        return address;
    }

    public Double getOrderAmount() {
        return orderAmount;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public int getTotalNumber() {
        return totalNumber;
    }

    public List<Cart> getCartList() {
        return cartList;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public String getuID() {
        return uID;
    }
    public static class Address {
        public String name;
        public String address;
        public String pincode;
        public String phoneNumber;

        public Address() {
        }

        public Address(String name, String address, String pincode, String phoneNumber) {
            this.name = name;
            this.address = address;
            this.pincode = pincode;
            this.phoneNumber = phoneNumber;
        }
    }

}