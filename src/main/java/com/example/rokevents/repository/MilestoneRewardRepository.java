package com.example.rokevents.repository;

import com.example.rokevents.entity.MilestoneReward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MilestoneRewardRepository extends JpaRepository<MilestoneReward, Long> {
    List<MilestoneReward> findByEventId(Long eventId);
}