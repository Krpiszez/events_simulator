package com.example.rokevents.util.impl;

import com.example.rokevents.dto.ConsolidatedItem;
import com.example.rokevents.util.EventSimulationHandler;
import com.example.rokevents.util.PullCalculation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EsmeraldaHouseSimulationHandler implements EventSimulationHandler {

    @Override
    public PullCalculation calculatePulls(long gemAmount) {
        long pulls = 2;
        long remainingGems = gemAmount;

        long discountedPulls = Math.min(2, remainingGems / 400);
        pulls += discountedPulls;
        remainingGems -= discountedPulls * 400;

        long fivePullBundles = remainingGems / 3600;
        pulls += fivePullBundles * 5;
        remainingGems -= fivePullBundles * 3600;

        long singlePulls = remainingGems / 800;
        pulls += singlePulls;
        remainingGems -= singlePulls * 800;

        return new PullCalculation(pulls, remainingGems);
    }

    @Override
    public long calculateRequiredGems(long requestedPulls) {
        if (requestedPulls <= 2) {
            return 0;
        }

        long remainingPulls = requestedPulls;
        long requiredGems = 0;

        remainingPulls -= 2;

        long discountedPulls = Math.min(2, remainingPulls);
        requiredGems += discountedPulls * 400;
        remainingPulls -= discountedPulls;

        long fivePullBundles = remainingPulls / 5;
        requiredGems += fivePullBundles * 3600;
        remainingPulls -= fivePullBundles * 5;

        requiredGems += remainingPulls * 800;

        return requiredGems;
    }

    @Override
    public List<ConsolidatedItem> calculateMilestoneRewards(long totalPulls) {
        Map<String, Long> rewards = new HashMap<>();

        if (totalPulls >= 10) {
            rewards.merge("Epic Equipment Material Choice Chest", 2L, Long::sum);
            rewards.merge("15-Hour Generic Speedup", 3L, Long::sum);
        }

        if (totalPulls >= 20) {
            rewards.merge("Epic Equipment Material Choice Chest", 3L, Long::sum);
            rewards.merge("500k Wood", 8L, Long::sum);
            rewards.merge("500k Food", 8L, Long::sum);
            rewards.merge("375k Stone", 8L, Long::sum);
            rewards.merge("200k Gold", 8L, Long::sum);
        }

        if (totalPulls >= 40) {
            rewards.merge("Legendary Equipment Material Choice Chest", 1L, Long::sum);
            rewards.merge("Epic Equipment Material Choice Chest", 3L, Long::sum);
            rewards.merge("24-Hour Generic Speedup", 2L, Long::sum);
        }

        if (totalPulls >= 70) {
            rewards.merge("Legendary Equipment Material Choice Chest", 2L, Long::sum);
            rewards.merge("24-Hour Generic Speedup", 3L, Long::sum);
            rewards.merge("8-Hour Building Speedup", 5L, Long::sum);
            rewards.merge("8-Hour Training Speedup", 5L, Long::sum);
            rewards.merge("8-Hour Research Speedup", 5L, Long::sum);
        }

        if (totalPulls >= 100) {
            rewards.merge("4-Hour Advanced Army Expansion", 2L, Long::sum);
            rewards.merge("Legendary Equipment Material Choice Chest", 3L, Long::sum);
        }

        return toConsolidatedList(rewards);
    }

    private List<ConsolidatedItem> toConsolidatedList(Map<String, Long> rewards) {
        List<ConsolidatedItem> result = new ArrayList<>();

        for (Map.Entry<String, Long> entry : rewards.entrySet()) {
            result.add(new ConsolidatedItem(entry.getKey(), entry.getValue()));
        }

        result.sort((a, b) -> Long.compare(b.getTotalQuantity(), a.getTotalQuantity()));
        return result;
    }
}
