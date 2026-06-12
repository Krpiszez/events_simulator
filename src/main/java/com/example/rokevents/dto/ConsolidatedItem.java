package com.example.rokevents.dto;

public class ConsolidatedItem {
    private String itemName;
    private Long totalQuantity;

    public ConsolidatedItem() {}

    public ConsolidatedItem(String itemName, Long totalQuantity) {
        this.itemName = itemName;
        this.totalQuantity = totalQuantity;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Long getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Long totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
}
