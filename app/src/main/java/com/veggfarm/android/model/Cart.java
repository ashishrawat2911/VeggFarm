package com.veggfarm.android.model;

/**
 * created by Ashish Rawat
 */

public class Cart {

    private Double cost;
    private int itemId;
    private String itemName;
    private String itemImage;
    private Double marketPrice;
    private int totalNumber;
    private String unit;

    public Cart() {
    }

    public Cart(Double cost, int itemId, String itemName, String itemImage, Double marketPrice, int totalNumber, String unit) {
        this.cost = cost;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemImage = itemImage;
        this.marketPrice = marketPrice;
        this.totalNumber = totalNumber;
        this.unit = unit;
    }

    public Double getCost() {
        return cost;
    }

    public int getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemImage() {
        return itemImage;
    }

    public Double getMarketPrice() {
        return marketPrice;
    }

    public int getTotalNumber() {
        return totalNumber;
    }

    public String getUnit() {
        return unit;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public void setMarketPrice(Double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public void setTotalNumber(int totalNumber) {
        this.totalNumber = totalNumber;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}