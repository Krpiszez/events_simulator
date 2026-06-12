package com.example.rokevents.dto;

public class SimulationRequest {
    private Long eventId;
    private Long gemAmount;
    private Long pullCount;

    public Long getPullCount() {
        return pullCount;
    }

    public SimulationRequest(Long pullCount, Long gemAmount, Long eventId) {
        this.pullCount = pullCount;
        this.gemAmount = gemAmount;
        this.eventId = eventId;
    }

    public void setPullCount(Long pullCount) {
        this.pullCount = pullCount;
    }

    public SimulationRequest() {}

    public SimulationRequest(Long eventId, Long gemAmount) {
        this.eventId = eventId;
        this.gemAmount = gemAmount;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getGemAmount() {
        return gemAmount;
    }

    public void setGemAmount(Long gemAmount) {
        this.gemAmount = gemAmount;
    }
}
