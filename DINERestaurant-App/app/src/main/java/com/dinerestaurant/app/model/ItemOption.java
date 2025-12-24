package com.dinerestaurant.app.model;

public class ItemOption {
    private int optionId;
    private String optionName;
    private double extraPrice;
    private boolean isSelected;

    public ItemOption(int optionId, String optionName, double extraPrice) {
        this.optionId = optionId;
        this.optionName = optionName;
        this.extraPrice = extraPrice;
        this.isSelected = false;
    }

    // Getters and Setters
    public int getOptionId() { return optionId; }
    public String getOptionName() { return optionName; }
    public double getExtraPrice() { return extraPrice; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}