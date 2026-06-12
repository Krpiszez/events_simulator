package com.example.rokevents.entity;

import javax.persistence.*;

@Entity
@Table(name = "item_drop_rates")
public class ItemDropRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private Double dropRate;

    public ItemDropRate() {}

    public ItemDropRate(Event event, Item item, Double dropRate) {
        this.event = event;
        this.item = item;
        this.dropRate = dropRate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Double getDropRate() {
        return dropRate;
    }

    public void setDropRate(Double dropRate) {
        this.dropRate = dropRate;
    }
}
