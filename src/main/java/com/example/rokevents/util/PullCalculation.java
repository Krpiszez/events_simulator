package com.example.rokevents.util;

public class PullCalculation {

    private final long totalPulls;
    private final long leftoverGems;

    public PullCalculation(long totalPulls, long leftoverGems) {
        this.totalPulls = totalPulls;
        this.leftoverGems = leftoverGems;
    }

    public long getTotalPulls() {
        return totalPulls;
    }

    public long getLeftoverGems() {
        return leftoverGems;
    }
}