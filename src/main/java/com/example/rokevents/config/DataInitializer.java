package com.example.rokevents.config;

import com.example.rokevents.entity.Event;
import com.example.rokevents.entity.Item;
import com.example.rokevents.entity.ItemDropRate;
import com.example.rokevents.entity.MilestoneReward;
import com.example.rokevents.repository.EventRepository;
import com.example.rokevents.repository.ItemRepository;
import com.example.rokevents.repository.ItemDropRateRepository;
import com.example.rokevents.repository.MilestoneRewardRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initializeData(EventRepository eventRepository,
                                            ItemRepository itemRepository,
                                            ItemDropRateRepository itemDropRateRepository,
                                            MilestoneRewardRepository milestoneRewardRepository) {
        return args -> {
            if (!eventRepository.findByName("Esmeralda's House").isPresent()) {
                Event esmeraldasHouse = new Event("Esmeralda's House", "A mystical house with valuable treasures");
                Event savedEvent = eventRepository.save(esmeraldasHouse);

                Item item1 = itemRepository.save(new Item("8-Hour Healing Speedup x4", "Speed up healing by 4 times for 8 hours"));
                Item item2 = itemRepository.save(new Item("8-Hour Training Speedup x4", "Speed up training by 4 times for 8 hours"));
                Item item3 = itemRepository.save(new Item("Blueprint Fragment Choice Chest (Includes Engineering) x4", "Choice chest with blueprint fragments"));
                Item item4 = itemRepository.save(new Item("Blueprint Fragment Choice Chest Helmet (Includes Engineering) x4", "Choice chest with helmet blueprint fragments"));
                Item item5 = itemRepository.save(new Item("Legendary Equipment Material Choice Chest x3", "Choice chest with legendary materials"));
                Item item6 = itemRepository.save(new Item("Legendary Equipment Material Choice Chest x1", "Single legendary material chest"));
                Item item7 = itemRepository.save(new Item("Epic Equipment Material Choice Chest x3", "Choice chest with epic materials"));
                Item item8 = itemRepository.save(new Item("Epic Equipment Material Choice Chest x2", "Choice chest with epic materials"));
                Item item9 = itemRepository.save(new Item("Epic Equipment Material Choice Chest x1", "Single epic material chest"));
                Item item10 = itemRepository.save(new Item("Level 4 \"Pick One\" Resource Chest x5", "Chest containing level 4 resources"));

                double totalRate = 15.432 + 15.432 + 8.23 + 8.23 + 1.234 + 2.057 + 4.115 + 8.23 + 12.345 + 24.691;
                double normalizer = 100.0 / totalRate;

                itemDropRateRepository.save(new ItemDropRate(savedEvent, item1, 15.432 * normalizer));
                itemDropRateRepository.save(new ItemDropRate(savedEvent, item2, 15.432 * normalizer));
                itemDropRateRepository.save(new ItemDropRate(savedEvent, item3, 8.23 * normalizer));
                itemDropRateRepository.save(new ItemDropRate(savedEvent, item4, 8.23 * normalizer));
                itemDropRateRepository.save(new ItemDropRate(savedEvent, item5, 1.234 * normalizer));
                itemDropRateRepository.save(new ItemDropRate(savedEvent, item6, 2.057 * normalizer));
                itemDropRateRepository.save(new ItemDropRate(savedEvent, item7, 4.115 * normalizer));
                itemDropRateRepository.save(new ItemDropRate(savedEvent, item8, 8.23 * normalizer));
                itemDropRateRepository.save(new ItemDropRate(savedEvent, item9, 12.345 * normalizer));
                itemDropRateRepository.save(new ItemDropRate(savedEvent, item10, 24.691 * normalizer));

                Item speedup15h = itemRepository.save(new Item("15-Hour Generic Speedup", "15 hour speedup"));
                Item wood500k = itemRepository.save(new Item("500k Wood", "500k wood resource"));
                Item food500k = itemRepository.save(new Item("500k Food", "500k food resource"));
                Item stone375k = itemRepository.save(new Item("375k Stone", "375k stone resource"));
                Item gold200k = itemRepository.save(new Item("200k Gold", "200k gold resource"));
                Item speedup24h = itemRepository.save(new Item("24-Hour Generic Speedup", "24 hour speedup"));
                Item building8h = itemRepository.save(new Item("8-Hour Building Speedup", "8 hour building speedup"));
                Item training8h = itemRepository.save(new Item("8-Hour Training Speedup", "8 hour training speedup"));
                Item research8h = itemRepository.save(new Item("8-Hour Research Speedup", "8 hour research speedup"));
                Item armyExpansion4h = itemRepository.save(new Item("4-Hour Advanced Army Expansion", "4 hour advanced army expansion"));
                Item epicMaterialChest = itemRepository.save(new Item("Epic Equipment Material Choice Chest", "Epic equipment material choice chest"));
                Item legendaryMaterialChest = itemRepository.save(new Item("Legendary Equipment Material Choice Chest", "Legendary equipment material choice chest"));

                milestoneRewardRepository.save(new MilestoneReward(savedEvent, epicMaterialChest, 10L, 2L));
                milestoneRewardRepository.save(new MilestoneReward(savedEvent, speedup15h, 10L, 3L));

                milestoneRewardRepository.save(new MilestoneReward(savedEvent, epicMaterialChest, 20L, 3L));
                milestoneRewardRepository.save(new MilestoneReward(savedEvent, wood500k, 20L, 8L));
                milestoneRewardRepository.save(new MilestoneReward(savedEvent, food500k, 20L, 8L));
                milestoneRewardRepository.save(new MilestoneReward(savedEvent, stone375k, 20L, 8L));
                milestoneRewardRepository.save(new MilestoneReward(savedEvent, gold200k, 20L, 8L));

                milestoneRewardRepository.save(new MilestoneReward(savedEvent, legendaryMaterialChest, 40L, 1L));
                milestoneRewardRepository.save(new MilestoneReward(savedEvent, epicMaterialChest, 40L, 3L));
                milestoneRewardRepository.save(new MilestoneReward(savedEvent, speedup24h, 40L, 2L));

                milestoneRewardRepository.save(new MilestoneReward(savedEvent, legendaryMaterialChest, 70L, 2L));
                milestoneRewardRepository.save(new MilestoneReward(savedEvent, speedup24h, 70L, 3L));
                milestoneRewardRepository.save(new MilestoneReward(savedEvent, building8h, 70L, 5L));
                milestoneRewardRepository.save(new MilestoneReward(savedEvent, training8h, 70L, 5L));
                milestoneRewardRepository.save(new MilestoneReward(savedEvent, research8h, 70L, 5L));

                milestoneRewardRepository.save(new MilestoneReward(savedEvent, armyExpansion4h, 100L, 2L));
                milestoneRewardRepository.save(new MilestoneReward(savedEvent, legendaryMaterialChest, 100L, 3L));
            }
        };
    }
}