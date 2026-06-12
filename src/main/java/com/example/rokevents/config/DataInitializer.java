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

            if (!eventRepository.findByName("Holy Knight's Treasure").isPresent()) {
                Event holyKnightsTreasure = new Event(
                        "Holy Knight's Treasure",
                        "Two-day weapon equipment event with conquest blueprint rewards"
                );

                Event savedEvent = eventRepository.save(holyKnightsTreasure);
                Item shieldFragment1 = itemRepository.save(new Item("Legendary Blueprint Fragment x1", "Shield of the Eternal Empire blueprint fragment"));
                Item shieldFragment5 = itemRepository.save(new Item("Legendary Blueprint Fragment x5", "Shield of the Eternal Empire blueprint fragments"));
                Item shieldFragment10 = itemRepository.save(new Item("Legendary Blueprint Fragment x10", "Shield of the Eternal Empire blueprint fragments"));
                Item level4ResourceChest = itemRepository.save(new Item("Level 4 \"Pick One\" Resource Chest x15", "Level 4 pick one resource chest"));
                Item speedup3h = itemRepository.save(new Item("3-Hour Speedup x5", "Generic 3-hour speedup"));
                Item equipmentMaterialChest1 = itemRepository.save(new Item("Equipment Material Choice Chest x1", "Equipment material choice chest"));
                Item equipmentMaterialChest3 = itemRepository.save(new Item("Equipment Material Choice Chest x3", "Equipment material choice chest"));
                Item equipmentMaterialChest2 = itemRepository.save(new Item("Equipment Material Choice Chest x2", "Equipment material choice chest"));
                Item researchSpeedup8h = itemRepository.save(new Item("8-Hour Research Speedup x2", "8-hour research speedup"));
                Item buildingSpeedup8h = itemRepository.save(new Item("8-Hour Building Speedup x2", "8-hour building speedup"));
                Item blueprintChoiceChest1 = itemRepository.save(new Item("Blueprint Fragments Choice Chest (Includes Engineering) Chest x2", "Blueprint fragments choice chest including engineering"));
                Item blueprintChoiceChest2 = itemRepository.save(new Item("Blueprint Fragments Choice Chest (Includes Engineering) Pants x2", "Blueprint fragments choice chest including engineering"));
                Item blueprintChoiceChest3 = itemRepository.save(new Item("Blueprint Fragments Choice Chest (Includes Engineering) Weapon x2", "Blueprint fragments choice chest including engineering"));
                Item trainingSpeedup8h = itemRepository.save(new Item("8-Hour Training Speedup x2", "8-hour training speedup"));
                Item speedup15h = itemRepository.save(new Item("15-Hour Speedup x2", "Generic 15-hour speedup"));

                itemDropRateRepository.save(new ItemDropRate(savedEvent, shieldFragment1, 4.838));
                itemDropRateRepository.save(new ItemDropRate(savedEvent, shieldFragment5, 1.935));
                itemDropRateRepository.save(new ItemDropRate(savedEvent, shieldFragment10, 0.752));
                itemDropRateRepository.save(new ItemDropRate(savedEvent, level4ResourceChest, 17.204));
                itemDropRateRepository.save(new ItemDropRate(savedEvent, speedup3h, 12.903));
                itemDropRateRepository.save(new ItemDropRate(savedEvent, equipmentMaterialChest1, 9.677));
                itemDropRateRepository.save(new ItemDropRate(savedEvent, equipmentMaterialChest3, 7.526));
                itemDropRateRepository.save(new ItemDropRate(savedEvent, researchSpeedup8h, 7.526));
                itemDropRateRepository.save(new ItemDropRate(savedEvent, equipmentMaterialChest2, 7.526));
                itemDropRateRepository.save(new ItemDropRate(savedEvent, buildingSpeedup8h, 6.451));
                itemDropRateRepository.save(new ItemDropRate(savedEvent, blueprintChoiceChest1, 5.376));
                itemDropRateRepository.save(new ItemDropRate(savedEvent, blueprintChoiceChest2, 5.376));
                itemDropRateRepository.save(new ItemDropRate(savedEvent, blueprintChoiceChest3, 5.376));
                itemDropRateRepository.save(new ItemDropRate(savedEvent, trainingSpeedup8h, 4.301));
                itemDropRateRepository.save(new ItemDropRate(savedEvent, speedup15h, 3.225));

                Item conquestBlueprintChest = itemRepository.save(new Item("Conquest Blueprint Fragment Choice Chest", "Conquest blueprint fragment choice chest"));
                Item milestoneBlueprintChest = itemRepository.save(new Item("Blueprint Fragments Choice Chest (Includes Engineering) Weapon", "Blueprint fragments choice chest including engineering"));
                Item milestoneSpeedup24h = itemRepository.save(new Item("24-Hour Generic Speedup", "24-hour generic speedup"));
                Item milestoneBuilding8h = itemRepository.save(new Item("8-Hour Building Speedup", "8-hour building speedup"));
                Item milestoneTraining8h = itemRepository.save(new Item("8-Hour Training Speedup", "8-hour training speedup"));
                Item milestoneResearch8h = itemRepository.save(new Item("8-Hour Research Speedup", "8-hour research speedup"));
                Item milestoneEquipmentMaterialChest = itemRepository.save(new Item("Equipment Material Choice Chest", "Equipment material choice chest"));
                Item food500k = itemRepository.save(new Item("500k Food", "500k food resource"));
                Item wood500k = itemRepository.save(new Item("500k Wood", "500k wood resource"));
                Item stone375k = itemRepository.save(new Item("375k Stone", "375k stone resource"));
                Item gold200k = itemRepository.save(new Item("200k Gold", "200k gold resource"));

                milestoneRewardRepository.save(new MilestoneReward(savedEvent, conquestBlueprintChest, 10L, 5L));

                milestoneRewardRepository.save(new MilestoneReward(savedEvent, milestoneBlueprintChest, 25L, 10L));
                milestoneRewardRepository.save(new MilestoneReward(savedEvent, milestoneSpeedup24h, 25L, 1L));
                milestoneRewardRepository.save(new MilestoneReward(savedEvent, milestoneBuilding8h, 25L, 3L));
                milestoneRewardRepository.save(new MilestoneReward(savedEvent, milestoneTraining8h, 25L, 3L));
                milestoneRewardRepository.save(new MilestoneReward(savedEvent, milestoneResearch8h, 25L, 3L));

                milestoneRewardRepository.save(new MilestoneReward(savedEvent, conquestBlueprintChest, 45L, 6L));

                milestoneRewardRepository.save(new MilestoneReward(savedEvent, milestoneEquipmentMaterialChest, 70L, 12L));
                milestoneRewardRepository.save(new MilestoneReward(savedEvent, food500k, 70L, 5L));
                milestoneRewardRepository.save(new MilestoneReward(savedEvent, wood500k, 70L, 5L));
                milestoneRewardRepository.save(new MilestoneReward(savedEvent, stone375k, 70L, 5L));
                milestoneRewardRepository.save(new MilestoneReward(savedEvent, gold200k, 70L, 5L));

                milestoneRewardRepository.save(new MilestoneReward(savedEvent, conquestBlueprintChest, 100L, 9L));
            }
        };
    }
}