package com.example.rokevents.service;

import com.example.rokevents.dto.*;
import com.example.rokevents.entity.Event;
import com.example.rokevents.entity.ItemDropRate;
import com.example.rokevents.repository.EventRepository;
import com.example.rokevents.repository.ItemDropRateRepository;
import com.example.rokevents.repository.MilestoneRewardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SimulationService {
    private static final int SIMULATION_COUNT = 10000;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ItemDropRateRepository itemDropRateRepository;

    @Autowired
    private MilestoneRewardRepository milestoneRewardRepository;

    public SimulationResponse simulate(SimulationRequest request) {
        long totalPulls;
        long leftoverGems = 0;
        long gemSpent;

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + request.getEventId()));

        EventSimulationHandler handler = getHandlerForEvent(event);

        long totalPulls;
        long leftoverGems = 0;
        long gemSpent;

        if (request.getPullCount() != null && request.getPullCount() > 0) {
            totalPulls = request.getPullCount();
            gemSpent = calculateRequiredGems(totalPulls);
        } else {
            PullCalculation pullCalculation = calculatePulls(request.getGemAmount());

            totalPulls = pullCalculation.getTotalPulls();
            leftoverGems = pullCalculation.getLeftoverGems();
            gemSpent = request.getGemAmount();
        }

        List<ConsolidatedItem> milestoneRewards = calculateMilestoneRewards(totalPulls);

        List<ItemDropRate> dropRates = itemDropRateRepository.findByEventId(event.getId());
        if (dropRates.isEmpty()) {
            throw new IllegalArgumentException("No drop rates configured for event: " + event.getName());
        }

        Map<String, Long> itemCounts = new HashMap<>();
        for (ItemDropRate dropRate : dropRates) {
            itemCounts.put(dropRate.getItem().getName(), 0L);
        }

        Random random = new Random();

        for (int sim = 0; sim < SIMULATION_COUNT; sim++) {
            for (long pull = 0; pull < totalPulls; pull++) {
                String selectedItem = selectItemByDropRate(dropRates, random);
                itemCounts.put(selectedItem, itemCounts.get(selectedItem) + 1);
            }
        }

        long totalDrops = totalPulls * SIMULATION_COUNT;
        List<ItemResult> results = new ArrayList<>();

        for (ItemDropRate dropRate : dropRates) {
            String itemName = dropRate.getItem().getName();
            Long count = itemCounts.get(itemName);

            double percentage = totalDrops == 0
                    ? 0
                    : (count.doubleValue() / totalDrops) * 100;

            results.add(new ItemResult(
                    itemName,
                    count,
                    Math.round(percentage * 10000.0) / 10000.0
            ));
        }

        results.sort((a, b) -> Double.compare(b.getPercentage(), a.getPercentage()));

        List<ExpectedItem> expectedItems = calculateExpectedItems(dropRates, totalPulls);
        List<ConsolidatedItem> consolidatedItems = calculateConsolidatedItems(expectedItems);

        return new SimulationResponse(
                event.getId(),
                event.getName(),
                totalPulls,
                gemSpent,
                leftoverGems,
                results,
                expectedItems,
                consolidatedItems,
                milestoneRewards
        );
    }

    private String selectItemByDropRate(List<ItemDropRate> dropRates, Random random) {
        double randomValue = random.nextDouble() * 100;
        double cumulativeRate = 0;

        for (ItemDropRate dropRate : dropRates) {
            cumulativeRate += dropRate.getDropRate();
            if (randomValue < cumulativeRate) {
                return dropRate.getItem().getName();
            }
        }

        return dropRates.get(dropRates.size() - 1).getItem().getName();
    }

    private List<ExpectedItem> calculateExpectedItems(List<ItemDropRate> dropRates, long totalPulls) {
        List<ExpectedItem> expectedItems = new ArrayList<>();

        for (ItemDropRate dropRate : dropRates) {
            long expectedCount = Math.round((dropRate.getDropRate() / 100.0) * totalPulls);
            if (expectedCount > 0) {
                expectedItems.add(new ExpectedItem(dropRate.getItem().getName(), expectedCount));
            }
        }

        expectedItems.sort((a, b) -> Long.compare(b.getExpectedCount(), a.getExpectedCount()));

        return expectedItems;
    }

    private List<ConsolidatedItem> calculateConsolidatedItems(List<ExpectedItem> expectedItems) {
        Map<String, Long> consolidatedMap = new HashMap<>();

        Pattern pattern = Pattern.compile("^(.+?)\\s+x(\\d+)$");

        for (ExpectedItem item : expectedItems) {
            String itemName = item.getItemName();
            Matcher matcher = pattern.matcher(itemName);

            if (matcher.find()) {
                String baseName = matcher.group(1);
                int quantity = Integer.parseInt(matcher.group(2));
                long totalQuantity = item.getExpectedCount() * quantity;

                consolidatedMap.put(baseName, consolidatedMap.getOrDefault(baseName, 0L) + totalQuantity);
            } else {
                consolidatedMap.put(itemName, consolidatedMap.getOrDefault(itemName, 0L) + item.getExpectedCount());
            }
        }

        List<ConsolidatedItem> consolidated = new ArrayList<>();
        for (Map.Entry<String, Long> entry : consolidatedMap.entrySet()) {
            consolidated.add(new ConsolidatedItem(entry.getKey(), entry.getValue()));
        }

        consolidated.sort((a, b) -> Long.compare(b.getTotalQuantity(), a.getTotalQuantity()));

        return consolidated;
    }

    private PullCalculation calculatePulls(long gemAmount) {
        long pulls = 2; // first 2 pulls free
        long remainingGems = gemAmount;

        // next 2 pulls cost 400 each
        long discountedPulls = Math.min(2, remainingGems / 400);
        pulls += discountedPulls;
        remainingGems -= discountedPulls * 400;

        // unlimited 5-pull bundles at 3600 gems
        long fivePullBundles = remainingGems / 3600;
        pulls += fivePullBundles * 5;
        remainingGems -= fivePullBundles * 3600;

        // leftover gems buy single pulls at 800 gems each
        long singlePulls = remainingGems / 800;
        pulls += singlePulls;
        remainingGems -= singlePulls * 800;

        return new PullCalculation(pulls, remainingGems);
    }

    private static class PullCalculation {
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

    private long calculateRequiredGems(long requestedPulls) {
        if (requestedPulls <= 2) {
            return 0;
        }

        long remainingPulls = requestedPulls;
        long requiredGems = 0;

        // first 2 pulls are free
        remainingPulls -= 2;

        // next 2 pulls cost 400 each
        long discountedPulls = Math.min(2, remainingPulls);
        requiredGems += discountedPulls * 400;
        remainingPulls -= discountedPulls;

        // unlimited 5-pull bundles cost 3600
        long fivePullBundles = remainingPulls / 5;
        requiredGems += fivePullBundles * 3600;
        remainingPulls -= fivePullBundles * 5;

        // leftover single pulls cost 800 each
        requiredGems += remainingPulls * 800;

        return requiredGems;
    }

    private List<ConsolidatedItem> calculateMilestoneRewards(long totalPulls) {
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

        List<ConsolidatedItem> result = new ArrayList<>();

        for (Map.Entry<String, Long> entry : rewards.entrySet()) {
            result.add(new ConsolidatedItem(entry.getKey(), entry.getValue()));
        }

        result.sort((a, b) -> Long.compare(b.getTotalQuantity(), a.getTotalQuantity()));

        return result;
    }

    private EventSimulationHandler getHandlerForEvent(Event event) {
        String eventName = event.getName();

        if ("Esmeralda's House".equalsIgnoreCase(eventName)) {
            return new EsmeraldaHouseSimulationHandler();
        }

        if ("Shield of the Eternal Empire".equalsIgnoreCase(eventName)) {
            return new ShieldOfTheEternalEmpireSimulationHandler();
        }

        throw new IllegalArgumentException("No simulation handler configured for event: " + eventName);
    }
}