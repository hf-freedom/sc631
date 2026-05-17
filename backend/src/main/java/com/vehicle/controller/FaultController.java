package com.vehicle.controller;

import com.vehicle.entity.*;
import com.vehicle.service.FaultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/faults")
@CrossOrigin(origins = "*")
public class FaultController {

    @Autowired
    private FaultService faultService;

    @PostMapping
    public ResponseEntity<Fault> submitFault(@RequestBody Fault fault) {
        return ResponseEntity.ok(faultService.submitFault(fault));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Fault> updateFault(@PathVariable String id, @RequestBody Fault fault) {
        Fault updated = faultService.updateFault(id, fault);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fault> getFault(@PathVariable String id) {
        Fault fault = faultService.getFault(id);
        if (fault == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(fault);
    }

    @GetMapping
    public ResponseEntity<List<Fault>> getAllFaults() {
        return ResponseEntity.ok(faultService.getAllFaults());
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<RepairOrder> assignRepair(@PathVariable String id, @RequestBody Map<String, String> body) {
        String repairPersonId = body.get("repairPersonId");
        String stationId = body.get("stationId");
        RepairOrder order = faultService.assignRepair(id, repairPersonId, stationId);
        if (order == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(order);
    }

    @GetMapping("/repair-persons")
    public ResponseEntity<List<RepairPerson>> getRepairPersons() {
        return ResponseEntity.ok(faultService.getAllRepairPersons());
    }

    @GetMapping("/repair-stations")
    public ResponseEntity<List<RepairStation>> getRepairStations() {
        return ResponseEntity.ok(faultService.getAllRepairStations());
    }

    @GetMapping("/repair-orders")
    public ResponseEntity<List<RepairOrder>> getRepairOrders() {
        return ResponseEntity.ok(faultService.getAllRepairOrders());
    }

    @GetMapping("/repair-orders/{orderId}")
    public ResponseEntity<RepairOrder> getRepairOrder(@PathVariable String orderId) {
        RepairOrder order = faultService.getRepairOrder(orderId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }

    @PostMapping("/repair-orders/{orderId}/start")
    public ResponseEntity<Void> startRepair(@PathVariable String orderId) {
        if (faultService.startRepair(orderId)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/repair-orders/{orderId}/complete")
    public ResponseEntity<Void> completeRepair(@PathVariable String orderId, @RequestBody Map<String, Integer> body) {
        Integer newMileage = body != null ? body.get("newMileage") : null;
        if (faultService.completeRepair(orderId, newMileage)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/scan")
    public ResponseEntity<Map<String, Integer>> triggerScan() {
        int updatedCount = faultService.triggerScan();
        Map<String, Integer> result = new HashMap<>();
        result.put("updatedCount", updatedCount);
        return ResponseEntity.ok(result);
    }
}
