package com.dinerestaurant.app.model;

public class UserAddress {

    private Integer addressId;
    private String label;
    private String addressText;
    private Double latitude;
    private Double longitude;
    private Boolean isDefault;
    private User user;

    // ===== GETTERS =====
    public Integer getAddressId() {
        return addressId;
    }

    public String getLabel() {
        return label;
    }

    public String getAddressText() {
        return addressText;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public User getUser() {
        return user;
    }

    // ===== SETTERS =====
    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setAddressText(String addressText) {
        this.addressText = addressText;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
