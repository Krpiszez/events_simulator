package com.example.rokevents.dto;

import java.util.List;

public class SimulationResponse {
    private Long eventId;
    private String eventName;
    private Long totalPulls;
    private Long gemSpent;
    private Long leftoverGems;
    private List<ItemResult> results;
    private List<ExpectedItem> expectedItems;
    private List<ConsolidatedItem> consolidatedItems;
    private List<ConsolidatedItem> milestoneRewards;

    public SimulationResponse() {}

    public SimulationResponse(
            Long eventId,
            String eventName,
            Long totalPulls,
            Long gemSpent,
            Long leftoverGems,
            List<ItemResult> results,
            List<ExpectedItem> expectedItems,
            List<ConsolidatedItem> consolidatedItems,
            List<ConsolidatedItem> milestoneRewards
    ) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.totalPulls = totalPulls;
        this.gemSpent = gemSpent;
        this.leftoverGems = leftoverGems;
        this.results = results;
        this.expectedItems = expectedItems;
        this.consolidatedItems = consolidatedItems;
        this.milestoneRewards = milestoneRewards;
    }

    public List<ConsolidatedItem> getMilestoneRewards() {
        return milestoneRewards;
    }

    public void setMilestoneRewards(List<ConsolidatedItem> milestoneRewards) {
        this.milestoneRewards = milestoneRewards;
    }

    public Long getLeftoverGems() {
        return leftoverGems;
    }

    public void setLeftoverGems(Long leftoverGems) {
        this.leftoverGems = leftoverGems;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Long getTotalPulls() {
        return totalPulls;
    }

    public void setTotalPulls(Long totalPulls) {
        this.totalPulls = totalPulls;
    }

    public Long getGemSpent() {
        return gemSpent;
    }

    public void setGemSpent(Long gemSpent) {
        this.gemSpent = gemSpent;
    }

    public List<ItemResult> getResults() {
        return results;
    }

    public void setResults(List<ItemResult> results) {
        this.results = results;
    }

    public List<ExpectedItem> getExpectedItems() {
        return expectedItems;
    }

    public void setExpectedItems(List<ExpectedItem> expectedItems) {
        this.expectedItems = expectedItems;
    }

    public List<ConsolidatedItem> getConsolidatedItems() {
        return consolidatedItems;
    }

    public void setConsolidatedItems(List<ConsolidatedItem> consolidatedItems) {
        this.consolidatedItems = consolidatedItems;
    }
}
