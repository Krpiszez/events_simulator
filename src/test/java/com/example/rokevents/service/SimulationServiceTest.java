package com.example.rokevents.service;

import com.example.rokevents.dto.SimulationRequest;
import com.example.rokevents.dto.SimulationResponse;
import com.example.rokevents.entity.Event;
import com.example.rokevents.entity.Item;
import com.example.rokevents.entity.ItemDropRate;
import com.example.rokevents.repository.EventRepository;
import com.example.rokevents.repository.ItemRepository;
import com.example.rokevents.repository.ItemDropRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SimulationServiceTest {

    @Autowired
    private SimulationService simulationService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemDropRateRepository itemDropRateRepository;

    @BeforeEach
    void setUp() {
        itemDropRateRepository.deleteAll();
        itemRepository.deleteAll();
        eventRepository.deleteAll();
    }

    @Test
    void testSimulationWithZeroGems() {
        Event event = eventRepository.save(new Event("Test Event", "Test Description"));
        Item item1 = itemRepository.save(new Item("Test Item", "Test"));
        itemDropRateRepository.save(new ItemDropRate(event, item1, 100.0));
        
        SimulationRequest request = new SimulationRequest(event.getId(), 0L);
        SimulationResponse response = simulationService.simulate(request);

        assertEquals(event.getId(), response.getEventId());
        assertEquals(0L, response.getTotalPulls());
        assertEquals(0L, response.getGemSpent());
        assertTrue(response.getResults().stream().allMatch(r -> r.getCount() == 0L));
    }

    @Test
    void testSimulationProbabilities() {
        Event event = eventRepository.save(new Event("Probability Test", "Test Description"));
        Item item1 = itemRepository.save(new Item("Common Item", "Common"));
        Item item2 = itemRepository.save(new Item("Rare Item", "Rare"));
        
        itemDropRateRepository.save(new ItemDropRate(event, item1, 50.0));
        itemDropRateRepository.save(new ItemDropRate(event, item2, 50.0));

        SimulationRequest request = new SimulationRequest(event.getId(), 40000L);
        SimulationResponse response = simulationService.simulate(request);

        assertEquals(event.getId(), response.getEventId());
        assertEquals(50000L, response.getTotalPulls());
        assertEquals(40000L, response.getGemSpent());
        
        List<String> itemNames = response.getResults().stream()
                .map(r -> r.getItemName())
                .toList();
        assertTrue(itemNames.contains("Common Item"));
        assertTrue(itemNames.contains("Rare Item"));
        
        double commonPercentage = response.getResults().stream()
                .filter(r -> r.getItemName().equals("Common Item"))
                .findFirst()
                .map(r -> r.getPercentage())
                .orElse(0.0);
        
        assertTrue(commonPercentage > 40 && commonPercentage < 60, 
                   "Percentage should be around 50% for a 50-50 distribution");
    }

    @Test
    void testSimulationWithInvalidEventId() {
        SimulationRequest request = new SimulationRequest(999L, 8000L);
        
        assertThrows(IllegalArgumentException.class, () -> {
            simulationService.simulate(request);
        });
    }

    @Test
    void testSimulationResultsSum() {
        Event event = eventRepository.save(new Event("Sum Test", "Test Description"));
        Item item1 = itemRepository.save(new Item("Item 1", "Test"));
        Item item2 = itemRepository.save(new Item("Item 2", "Test"));
        Item item3 = itemRepository.save(new Item("Item 3", "Test"));
        
        itemDropRateRepository.save(new ItemDropRate(event, item1, 50.0));
        itemDropRateRepository.save(new ItemDropRate(event, item2, 30.0));
        itemDropRateRepository.save(new ItemDropRate(event, item3, 20.0));

        SimulationRequest request = new SimulationRequest(event.getId(), 40000L);
        SimulationResponse response = simulationService.simulate(request);

        double totalPercentage = response.getResults().stream()
                .mapToDouble(r -> r.getPercentage())
                .sum();
        
        assertEquals(100.0, totalPercentage, 0.01);
    }

    @Test
    void testSimulationSortingByPercentage() {
        Event event = eventRepository.save(new Event("Sort Test", "Test Description"));
        Item item1 = itemRepository.save(new Item("High Item", "Test"));
        Item item2 = itemRepository.save(new Item("Low Item", "Test"));
        
        itemDropRateRepository.save(new ItemDropRate(event, item1, 80.0));
        itemDropRateRepository.save(new ItemDropRate(event, item2, 20.0));

        SimulationRequest request = new SimulationRequest(event.getId(), 8000L);
        SimulationResponse response = simulationService.simulate(request);

        for (int i = 0; i < response.getResults().size() - 1; i++) {
            assertTrue(response.getResults().get(i).getPercentage() >= 
                      response.getResults().get(i + 1).getPercentage());
        }
    }

    @Test
    void testSimulationTotalCounts() {
        Event event = eventRepository.save(new Event("Count Test", "Test Description"));
        Item item1 = itemRepository.save(new Item("Item A", "Test"));
        
        itemDropRateRepository.save(new ItemDropRate(event, item1, 100.0));

        long gemAmount = 40000L;
        SimulationRequest request = new SimulationRequest(event.getId(), gemAmount);
        SimulationResponse response = simulationService.simulate(request);

        long totalCount = response.getResults().stream()
                .mapToLong(r -> r.getCount())
                .sum();
        
        assertEquals(response.getTotalPulls(), totalCount);
    }
}
