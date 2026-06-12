package com.example.rokevents.util;

import com.example.rokevents.dto.ConsolidatedItem;

import java.util.List;

public interface EventSimulationHandler {

    PullCalculation calculatePulls(long gemAmount);

    long calculateRequiredGems(long requestedPulls);

    List<ConsolidatedItem> calculateMilestoneRewards(long totalPulls);
}
