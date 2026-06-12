package com.example.rokevents.util.impl;

import com.example.rokevents.dto.ConsolidatedItem;
import com.example.rokevents.util.EventSimulationHandler;
import com.example.rokevents.util.PullCalculation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HolyKnightsTreasureSimulationHandler implements EventSimulationHandler {

    private static final long MAX_PULLS = 180;

    private static final long FREE_PULLS = 2;          // 1 free pull per day x 2 days
    private static final long DISCOUNTED_PULLS = 2;    // 1 discounted pull per day x 2 days

    private static final long DISCOUNTED_PULL_COST = 300;
    private static final long SINGLE_PULL_COST = 600;
    private static final long FIVE_PULL_COST = 2400;

    @Override
    public PullCalculation calculatePulls(long gemAmount) {
        long pulls = FREE_PULLS;
        long remainingGems = gemAmount;

        long discountedPulls = Math.min(DISCOUNTED_PULLS, remainingGems / DISCOUNTED_PULL_COST);
        pulls += discountedPulls;
        remainingGems -= discountedPulls * DISCOUNTED_PULL_COST;

        long remainingPullCapacity = MAX_PULLS - pulls;

        long fivePullBundles = Math.min(remainingPullCapacity / 5, remainingGems / FIVE_PULL_COST);
        pulls += fivePullBundles * 5;
        remainingGems -= fivePullBundles * FIVE_PULL_COST;

        remainingPullCapacity = MAX_PULLS - pulls;

        long singlePulls = Math.min(remainingPullCapacity, remainingGems / SINGLE_PULL_COST);
        pulls += singlePulls;
        remainingGems -= singlePulls * SINGLE_PULL_COST;

        return new PullCalculation(pulls, remainingGems);
    }

    @Override
    public long calculateRequiredGems(long requestedPulls) {
        if (requestedPulls > MAX_PULLS) {
            throw new IllegalArgumentException("Holy Knight's Treasure has a maximum of 180 pulls.");
        }

        if (requestedPulls <= FREE_PULLS) {
            return 0;
        }

        long remainingPulls = requestedPulls - FREE_PULLS;
        long requiredGems = 0;

        long discountedPulls = Math.min(DISCOUNTED_PULLS, remainingPulls);
        requiredGems += discountedPulls * DISCOUNTED_PULL_COST;
        remainingPulls -= discountedPulls;

        long fivePullBundles = remainingPulls / 5;
        requiredGems += fivePullBundles * FIVE_PULL_COST;
        remainingPulls -= fivePullBundles * 5;

        requiredGems += remainingPulls * SINGLE_PULL_COST;

        return requiredGems;
    }

    @Override
    public List<ConsolidatedItem> calculateMilestoneRewards(long totalPulls) {
        Map<String, Long> rewards = new HashMap<>();

        if (totalPulls >= 10) {
            rewards.merge("Conquest Blueprint Fragment Choice Chest", 5L, Long::sum);
        }

        if (totalPulls >= 25) {
            rewards.merge("Blueprint Fragments Choice Chest (Includes Engineering)", 10L, Long::sum);
            rewards.merge("24-Hour Generic Speedup", 1L, Long::sum);
            rewards.merge("8-Hour Building Speedup", 3L, Long::sum);
            rewards.merge("8-Hour Training Speedup", 3L, Long::sum);
            rewards.merge("8-Hour Research Speedup", 3L, Long::sum);
        }

        if (totalPulls >= 45) {
            rewards.merge("Conquest Blueprint Fragment Choice Chest", 6L, Long::sum);
        }

        if (totalPulls >= 70) {
            rewards.merge("Equipment Material Choice Chest", 12L, Long::sum);
            rewards.merge("500k Food", 5L, Long::sum);
            rewards.merge("500k Wood", 5L, Long::sum);
            rewards.merge("375k Stone", 5L, Long::sum);
            rewards.merge("200k Gold", 5L, Long::sum);
        }

        if (totalPulls >= 100) {
            rewards.merge("Conquest Blueprint Fragment Choice Chest", 9L, Long::sum);
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
