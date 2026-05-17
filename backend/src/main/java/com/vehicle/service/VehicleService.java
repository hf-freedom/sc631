package com.vehicle.service;

import com.vehicle.entity.*;
import com.vehicle.store.DataStore;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class VehicleService {

    public Vehicle createVehicle(Vehicle vehicle) {
        vehicle.setId(UUID.randomUUID().toString());
        vehicle.setCreatedAt(LocalDateTime.now());
        vehicle.setUpdatedAt(LocalDateTime.now());
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        if (vehicle.getMaintenanceCycle() == null) {
            vehicle.setMaintenanceCycle(5000);
        }
        DataStore.vehicles.put(vehicle.getId(), vehicle);
        checkMaintenance(vehicle.getId());
        return vehicle;
    }

    public Vehicle updateVehicle(String id, Vehicle vehicle) {
        Vehicle existing = DataStore.vehicles.get(id);
        if (existing == null) {
            return null;
        }
        if (vehicle.getPlateNumber() != null) {
            existing.setPlateNumber(vehicle.getPlateNumber());
        }
        if (vehicle.getBrand() != null) {
            existing.setBrand(vehicle.getBrand());
        }
        if (vehicle.getModel() != null) {
            existing.setModel(vehicle.getModel());
        }
        if (vehicle.getMileage() != null) {
            existing.setMileage(vehicle.getMileage());
        }
        if (vehicle.getLastMaintenanceMileage() != null) {
            existing.setLastMaintenanceMileage(vehicle.getLastMaintenanceMileage());
        }
        if (vehicle.getMaintenanceCycle() != null) {
            existing.setMaintenanceCycle(vehicle.getMaintenanceCycle());
        }
        existing.setUpdatedAt(LocalDateTime.now());
        checkMaintenance(id);
        return existing;
    }

    public boolean deleteVehicle(String id) {
        return DataStore.vehicles.remove(id) != null;
    }

    public Vehicle getVehicle(String id) {
        return DataStore.vehicles.get(id);
    }

    public List<Vehicle> getAllVehicles() {
        return new ArrayList<>(DataStore.vehicles.values());
    }

    public void checkMaintenance(String vehicleId) {
        Vehicle vehicle = DataStore.vehicles.get(vehicleId);
        if (vehicle == null) {
            return;
        }
        int dueMileage = vehicle.getLastMaintenanceMileage() + vehicle.getMaintenanceCycle();
        if (vehicle.getMileage() >= dueMileage) {
            boolean exists = DataStore.maintenanceReminders.values().stream()
                    .anyMatch(r -> r.getVehicleId().equals(vehicleId) && !r.getHandled());
            if (!exists) {
                MaintenanceReminder reminder = new MaintenanceReminder();
                reminder.setId(UUID.randomUUID().toString());
                reminder.setVehicleId(vehicleId);
                reminder.setPlateNumber(vehicle.getPlateNumber());
                reminder.setCurrentMileage(vehicle.getMileage());
                reminder.setDueMileage(dueMileage);
                reminder.setHandled(false);
                reminder.setCreatedAt(LocalDateTime.now());
                DataStore.maintenanceReminders.put(reminder.getId(), reminder);
            }
        }
    }

    public List<MaintenanceReminder> getMaintenanceReminders() {
        return new ArrayList<>(DataStore.maintenanceReminders.values());
    }

    public boolean handleMaintenanceReminder(String id) {
        MaintenanceReminder reminder = DataStore.maintenanceReminders.get(id);
        if (reminder == null) {
            return false;
        }
        reminder.setHandled(true);
        Vehicle vehicle = DataStore.vehicles.get(reminder.getVehicleId());
        if (vehicle != null) {
            vehicle.setLastMaintenanceMileage(vehicle.getMileage());
            vehicle.setUpdatedAt(LocalDateTime.now());
        }
        return true;
    }

    public boolean updateVehicleStatus(String id, VehicleStatus status) {
        Vehicle vehicle = DataStore.vehicles.get(id);
        if (vehicle == null) {
            return false;
        }
        vehicle.setStatus(status);
        vehicle.setUpdatedAt(LocalDateTime.now());
        return true;
    }
}
