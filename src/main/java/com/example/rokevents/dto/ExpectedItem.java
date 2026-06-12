package com.example.rokevents.dto;

public class ExpectedItem {
    private String itemName;
    private Long expectedCount;

    public ExpectedItem() {}

    public ExpectedItem(String itemName, Long expectedCount) {
        this.itemName = itemName;
        this.expectedCount = expectedCount;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Long getExpectedCount() {
        return expectedCount;
    }

    public void setExpectedCount(Long expectedCount) {
        this.expectedCount = expectedCount;
    }
}
