package com.example.rokevents.controller;

import com.example.rokevents.dto.SimulationRequest;
import com.example.rokevents.dto.SimulationResponse;
import com.example.rokevents.service.SimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private SimulationService simulationService;

    @PostMapping("/simulate")
    public ResponseEntity<?> simulate(@RequestBody SimulationRequest request) {
        try {
            if (request.getEventId() == null || request.getEventId() <= 0) {
                return ResponseEntity.badRequest().body("Event ID must be a positive number");
            }

            boolean hasGemAmount = request.getGemAmount() != null;
            boolean hasPullCount = request.getPullCount() != null;

            if (!hasGemAmount && !hasPullCount) {
                return ResponseEntity.badRequest().body("Either gem amount or pull count is required");
            }

            if (hasGemAmount && request.getGemAmount() < 0) {
                return ResponseEntity.badRequest().body("Gem amount must be non-negative");
            }

            if (hasPullCount && request.getPullCount() <= 0) {
                return ResponseEntity.badRequest().body("Pull count must be a positive number");
            }

            SimulationResponse response = simulationService.simulate(request);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Simulation failed: " + e.getMessage());
        }
    }
}
