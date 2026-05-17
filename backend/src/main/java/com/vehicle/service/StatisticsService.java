package com.vehicle.service;

import com.vehicle.entity.*;
import com.vehicle.store.DataStore;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

    public Statistics getStatistics() {
        Statistics stats = new Statistics();
        stats.setTotalVehicles(DataStore.vehicles.size());
        long availableCount = DataStore.vehicles.values().stream()
                .filter(v -> v.getStatus() == VehicleStatus.AVAILABLE)
                .count();
        stats.setAvailableVehicles((int) availableCount);
        if (DataStore.vehicles.size() > 0) {
            stats.setAvailabilityRate(availableCount * 100.0 / DataStore.vehicles.size());
        } else {
            stats.setAvailabilityRate(0.0);
        }
        stats.setTotalRepairs(DataStore.repairOrders.size());
        long overdueCount = DataStore.maintenanceReminders.values().stream()
                .filter(r -> !r.getHandled())
                .count();
        stats.setOverdueMaintenance((int) overdueCount);

        long pendingCount = DataStore.faults.values().stream()
                .filter(f -> f.getStatus() == FaultStatus.PENDING)
                .count();
        stats.setPendingFaults((int) pendingCount);

        long highCount = DataStore.faults.values().stream()
                .filter(f -> f.getUrgencyLevel() == UrgencyLevel.HIGH && f.getStatus() == FaultStatus.PENDING)
                .count();
        stats.setHighUrgencyFaults((int) highCount);

        long mediumCount = DataStore.faults.values().stream()
                .filter(f -> f.getUrgencyLevel() == UrgencyLevel.MEDIUM && f.getStatus() == FaultStatus.PENDING)
                .count();
        stats.setMediumUrgencyFaults((int) mediumCount);

        long lowCount = DataStore.faults.values().stream()
                .filter(f -> f.getUrgencyLevel() == UrgencyLevel.LOW && f.getStatus() == FaultStatus.PENDING)
                .count();
        stats.setLowUrgencyFaults((int) lowCount);

        return stats;
    }
}
