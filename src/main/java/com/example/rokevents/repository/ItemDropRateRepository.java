package com.example.rokevents.repository;

import com.example.rokevents.entity.ItemDropRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemDropRateRepository extends JpaRepository<ItemDropRate, Long> {
    List<ItemDropRate> findByEventId(Long eventId);
}
