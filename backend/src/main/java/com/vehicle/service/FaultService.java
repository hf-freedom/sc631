package com.vehicle.service;

import com.vehicle.entity.*;
import com.vehicle.store.DataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FaultService {

    @Autowired
    private VehicleService vehicleService;

    public Fault submitFault(Fault fault) {
        fault.setId(UUID.randomUUID().toString());
        fault.setUrgencyLevel(fault.getType().getDefaultUrgency());
        fault.setStatus(FaultStatus.PENDING);
        fault.setCreatedAt(LocalDateTime.now());
        fault.setUpdatedAt(LocalDateTime.now());
        Vehicle vehicle = DataStore.vehicles.get(fault.getVehicleId());
        if (vehicle != null) {
            fault.setPlateNumber(vehicle.getPlateNumber());
        }
        DataStore.faults.put(fault.getId(), fault);
        return fault;
    }

    public Fault updateFault(String id, Fault fault) {
        Fault existing = DataStore.faults.get(id);
        if (existing == null) {
            return null;
        }
        if (fault.getDescription() != null) {
            existing.setDescription(fault.getDescription());
        }
        if (fault.getType() != null) {
            existing.setType(fault.getType());
            existing.setUrgencyLevel(fault.getType().getDefaultUrgency());
        }
        if (fault.getStatus() != null) {
            existing.setStatus(fault.getStatus());
        }
        existing.setUpdatedAt(LocalDateTime.now());
        return existing;
    }

    public Fault getFault(String id) {
        return DataStore.faults.get(id);
    }

    public List<Fault> getAllFaults() {
        return new ArrayList<>(DataStore.faults.values());
    }

    public RepairOrder assignRepair(String faultId, String repairPersonId, String stationId) {
        Fault fault = DataStore.faults.get(faultId);
        if (fault == null || fault.getStatus() != FaultStatus.PENDING) {
            return null;
        }
        RepairPerson person = DataStore.repairPersons.get(repairPersonId);
        RepairStation station = DataStore.repairStations.get(stationId);
        if (person == null || !person.getAvailable() || station == null || !station.getAvailable()) {
            return null;
        }
        RepairOrder order = new RepairOrder();
        order.setId(UUID.randomUUID().toString());
        order.setFaultId(faultId);
        order.setVehicleId(fault.getVehicleId());
        order.setPlateNumber(fault.getPlateNumber());
        order.setRepairPersonId(repairPersonId);
        order.setRepairPersonName(person.getName());
        order.setStationId(stationId);
        order.setStationName(station.getName());
        order.setStatus(RepairOrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        DataStore.repairOrders.put(order.getId(), order);
        fault.setStatus(FaultStatus.ASSIGNED);
        fault.setUpdatedAt(LocalDateTime.now());
        return order;
    }

    public List<RepairPerson> getAvailableRepairPersons() {
        return DataStore.repairPersons.values().stream()
                .filter(RepairPerson::getAvailable)
                .collect(Collectors.toList());
    }

    public List<RepairStation> getAvailableStations() {
        return DataStore.repairStations.values().stream()
                .filter(RepairStation::getAvailable)
                .collect(Collectors.toList());
    }

    public List<RepairPerson> getAllRepairPersons() {
        return new ArrayList<>(DataStore.repairPersons.values());
    }

    public List<RepairStation> getAllRepairStations() {
        return new ArrayList<>(DataStore.repairStations.values());
    }

    public List<RepairOrder> getAllRepairOrders() {
        return new ArrayList<>(DataStore.repairOrders.values());
    }

    public RepairOrder getRepairOrder(String id) {
        return DataStore.repairOrders.get(id);
    }

    public boolean startRepair(String orderId) {
        RepairOrder order = DataStore.repairOrders.get(orderId);
        if (order == null || order.getStatus() != RepairOrderStatus.PENDING) {
            return false;
        }
        order.setStatus(RepairOrderStatus.IN_PROGRESS);
        order.setUpdatedAt(LocalDateTime.now());
        vehicleService.updateVehicleStatus(order.getVehicleId(), VehicleStatus.IN_REPAIR);
        Fault fault = DataStore.faults.get(order.getFaultId());
        if (fault != null) {
            fault.setStatus(FaultStatus.IN_PROGRESS);
            fault.setUpdatedAt(LocalDateTime.now());
        }
        return true;
    }

    public boolean completeRepair(String orderId, Integer newMileage) {
        RepairOrder order = DataStore.repairOrders.get(orderId);
        if (order == null || order.getStatus() != RepairOrderStatus.IN_PROGRESS) {
            return false;
        }
        order.setStatus(RepairOrderStatus.COMPLETED);
        order.setUpdatedAt(LocalDateTime.now());
        Vehicle vehicle = DataStore.vehicles.get(order.getVehicleId());
        if (vehicle != null) {
            vehicle.setStatus(VehicleStatus.AVAILABLE);
            if (newMileage != null) {
                vehicle.setMileage(newMileage);
            }
            vehicle.setUpdatedAt(LocalDateTime.now());
        }
        Fault fault = DataStore.faults.get(order.getFaultId());
        if (fault != null) {
            fault.setStatus(FaultStatus.COMPLETED);
            fault.setUpdatedAt(LocalDateTime.now());
        }
        return true;
    }

    @Scheduled(fixedRate = 60000)
    public void scanLongPendingFaults() {
        LocalDateTime threshold = LocalDateTime.now().minus(24, ChronoUnit.HOURS);
        for (Fault fault : DataStore.faults.values()) {
            if (fault.getStatus() == FaultStatus.PENDING && fault.getCreatedAt().isBefore(threshold)) {
                if (fault.getUrgencyLevel() == UrgencyLevel.LOW) {
                    fault.setUrgencyLevel(UrgencyLevel.MEDIUM);
                } else if (fault.getUrgencyLevel() == UrgencyLevel.MEDIUM) {
                    fault.setUrgencyLevel(UrgencyLevel.HIGH);
                }
                fault.setUpdatedAt(LocalDateTime.now());
            }
        }
    }

    public int triggerScan() {
        int updatedCount = 0;
        LocalDateTime threshold = LocalDateTime.now().minus(24, ChronoUnit.HOURS);
        for (Fault fault : DataStore.faults.values()) {
            if (fault.getStatus() == FaultStatus.PENDING && fault.getCreatedAt().isBefore(threshold)) {
                UrgencyLevel oldLevel = fault.getUrgencyLevel();
                if (fault.getUrgencyLevel() == UrgencyLevel.LOW) {
                    fault.setUrgencyLevel(UrgencyLevel.MEDIUM);
                } else if (fault.getUrgencyLevel() == UrgencyLevel.MEDIUM) {
                    fault.setUrgencyLevel(UrgencyLevel.HIGH);
                }
                if (oldLevel != fault.getUrgencyLevel()) {
                    updatedCount++;
                    fault.setUpdatedAt(LocalDateTime.now());
                }
            }
        }
        return updatedCount;
    }
}
