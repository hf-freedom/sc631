package com.vehicle;

import com.vehicle.store.DataStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableScheduling
public class VehicleMaintenanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VehicleMaintenanceApplication.class, args);
    }

    @PostConstruct
    public void init() {
        DataStore.initSampleData();
    }
}
