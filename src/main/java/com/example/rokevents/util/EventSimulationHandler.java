package com.example.rokevents.util;

public interface EventSimulationHandler {

    PullCalculation calculatePulls(long gemAmount);

    long calculateRequiredGems(long requestedPulls);

    List<ConsolidatedItem> calculateMilestoneRewards(long totalPulls);
}
