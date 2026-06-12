package com.example.rokevents.entity;

import javax.persistence.*;

@Entity
public class MilestoneReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long milestone;
    private Long quantity;

    @ManyToOne
    private Event event;

    @ManyToOne
    private Item item;

    public MilestoneReward() {
    }

    public MilestoneReward(Event event, Item item, Long milestone, Long quantity) {
        this.event = event;
        this.item = item;
        this.milestone = milestone;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public Event getEvent() {
        return event;
    }

    public Item getItem() {
        return item;
    }

    public Long getMilestone() {
        return milestone;
    }

    public Long getQuantity() {
        return quantity;
    }
}