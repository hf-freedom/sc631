package com.vehicle.controller;

import com.vehicle.entity.Statistics;
import com.vehicle.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "*")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping
    public ResponseEntity<Statistics> getStatistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }
}
