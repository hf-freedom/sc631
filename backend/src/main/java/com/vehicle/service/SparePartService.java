package com.vehicle.service;

import com.vehicle.entity.*;
import com.vehicle.store.DataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SparePartService {

    @Autowired
    private FaultService faultService;

    public List<SparePart> getAllSpareParts() {
        return new ArrayList<>(DataStore.spareParts.values());
    }

    public SparePart getSparePart(String id) {
        return DataStore.spareParts.get(id);
    }

    public SparePart updateStock(String id, Integer stock) {
        SparePart part = DataStore.spareParts.get(id);
        if (part == null) {
            return null;
        }
        part.setStock(stock);
        return part;
    }

    public PartUsageRecord usePart(String repairOrderId, String partId, Integer quantity) {
        RepairOrder order = DataStore.repairOrders.get(repairOrderId);
        if (order == null || order.getStatus() != RepairOrderStatus.IN_PROGRESS) {
            return null;
        }
        SparePart part = DataStore.spareParts.get(partId);
        if (part == null) {
            createPurchaseRequest(repairOrderId, partId, "未知备件", quantity);
            order.setStatus(RepairOrderStatus.PAUSED);
            order.setUpdatedAt(LocalDateTime.now());
            Fault fault = DataStore.faults.get(order.getFaultId());
            if (fault != null) {
                fault.setStatus(FaultStatus.PAUSED);
                fault.setUpdatedAt(LocalDateTime.now());
            }
            return null;
        }
        if (part.getStock() < quantity) {
            createPurchaseRequest(repairOrderId, partId, part.getName(), quantity - part.getStock());
            order.setStatus(RepairOrderStatus.PAUSED);
            order.setUpdatedAt(LocalDateTime.now());
            Fault fault = DataStore.faults.get(order.getFaultId());
            if (fault != null) {
                fault.setStatus(FaultStatus.PAUSED);
                fault.setUpdatedAt(LocalDateTime.now());
            }
            return null;
        }
        part.setStock(part.getStock() - quantity);
        PartUsageRecord record = new PartUsageRecord();
        record.setId(UUID.randomUUID().toString());
        record.setRepairOrderId(repairOrderId);
        record.setPartId(partId);
        record.setPartName(part.getName());
        record.setQuantity(quantity);
        record.setCreatedAt(LocalDateTime.now());
        DataStore.partUsageRecords.put(record.getId(), record);
        return record;
    }

    public PurchaseRequest createPurchaseRequest(String repairOrderId, String partId, String partName, Integer quantity) {
        PurchaseRequest request = new PurchaseRequest();
        request.setId(UUID.randomUUID().toString());
        request.setRepairOrderId(repairOrderId);
        request.setPartId(partId);
        request.setPartName(partName);
        request.setRequiredQuantity(quantity);
        request.setStatus(PurchaseRequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        DataStore.purchaseRequests.put(request.getId(), request);
        return request;
    }

    public List<PurchaseRequest> getAllPurchaseRequests() {
        return new ArrayList<>(DataStore.purchaseRequests.values());
    }

    public List<PartUsageRecord> getAllPartUsageRecords() {
        return new ArrayList<>(DataStore.partUsageRecords.values());
    }

    public boolean completePurchaseRequest(String requestId) {
        PurchaseRequest request = DataStore.purchaseRequests.get(requestId);
        if (request == null) {
            return false;
        }
        request.setStatus(PurchaseRequestStatus.COMPLETED);
        SparePart part = DataStore.spareParts.get(request.getPartId());
        if (part != null) {
            part.setStock(part.getStock() + request.getRequiredQuantity());
        }
        return true;
    }
}
