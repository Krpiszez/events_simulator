package com.example.rokevents.dto;

public class ItemResult {
    private String itemName;
    private Long count;
    private Double percentage;

    public ItemResult() {}

    public ItemResult(String itemName, Long count, Double percentage) {
        this.itemName = itemName;
        this.count = count;
        this.percentage = percentage;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }
}
