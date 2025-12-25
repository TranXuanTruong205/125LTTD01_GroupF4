package com.dinerestaurant.app.model;

public class TableItem {
    private Integer tableId;
    private String tableNumber;
    private Integer capacity;
    private String status; // "available" or "booked"

    // Constructor rỗng cho Gson/Retrofit
    public TableItem() {}

    // Getter Setter
    public Integer getTableId() {
        return tableId;
    }

    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}