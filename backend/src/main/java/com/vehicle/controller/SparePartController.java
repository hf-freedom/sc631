package com.vehicle.controller;

import com.vehicle.entity.*;
import com.vehicle.service.SparePartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/spare-parts")
@CrossOrigin(origins = "*")
public class SparePartController {

    @Autowired
    private SparePartService sparePartService;

    @GetMapping
    public ResponseEntity<List<SparePart>> getAllSpareParts() {
        return ResponseEntity.ok(sparePartService.getAllSpareParts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SparePart> getSparePart(@PathVariable String id) {
        SparePart part = sparePartService.getSparePart(id);
        if (part == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(part);
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<SparePart> updateStock(@PathVariable String id, @RequestBody Map<String, Integer> body) {
        Integer stock = body.get("stock");
        SparePart part = sparePartService.updateStock(id, stock);
        if (part == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(part);
    }

    @PostMapping("/use")
    public ResponseEntity<PartUsageRecord> usePart(@RequestBody Map<String, Object> body) {
        String repairOrderId = (String) body.get("repairOrderId");
        String partId = (String) body.get("partId");
        Object quantityObj = body.get("quantity");
        Integer quantity;
        if (quantityObj instanceof Number) {
            quantity = ((Number) quantityObj).intValue();
        } else {
            quantity = Integer.parseInt(quantityObj.toString());
        }
        PartUsageRecord record = sparePartService.usePart(repairOrderId, partId, quantity);
        if (record == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(record);
    }

    @GetMapping("/purchase-requests")
    public ResponseEntity<List<PurchaseRequest>> getPurchaseRequests() {
        return ResponseEntity.ok(sparePartService.getAllPurchaseRequests());
    }

    @GetMapping("/usage-records")
    public ResponseEntity<List<PartUsageRecord>> getPartUsageRecords() {
        return ResponseEntity.ok(sparePartService.getAllPartUsageRecords());
    }

    @PostMapping("/purchase-requests/{id}/complete")
    public ResponseEntity<Void> completePurchaseRequest(@PathVariable String id) {
        if (sparePartService.completePurchaseRequest(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
