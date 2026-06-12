package com.example.rokevents.dto;

public class MilestoneReward {
    private Long milestone;
    private String itemName;
    private Long quantity;

    public MilestoneReward(Long milestone, String itemName, Long quantity) {
        this.milestone = milestone;
        this.itemName = itemName;
        this.quantity = quantity;
    }

    public Long getMilestone() {
        return milestone;
    }

    public String getItemName() {
        return itemName;
    }

    public Long getQuantity() {
        return quantity;
    }
}