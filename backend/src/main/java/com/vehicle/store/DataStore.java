package com.vehicle.store;

import com.vehicle.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore {
    public static final Map<String, Vehicle> vehicles = new ConcurrentHashMap<>();
    public static final Map<String, MaintenanceReminder> maintenanceReminders = new ConcurrentHashMap<>();
    public static final Map<String, Fault> faults = new ConcurrentHashMap<>();
    public static final Map<String, RepairOrder> repairOrders = new ConcurrentHashMap<>();
    public static final Map<String, RepairPerson> repairPersons = new ConcurrentHashMap<>();
    public static final Map<String, RepairStation> repairStations = new ConcurrentHashMap<>();
    public static final Map<String, SparePart> spareParts = new ConcurrentHashMap<>();
    public static final Map<String, PartUsageRecord> partUsageRecords = new ConcurrentHashMap<>();
    public static final Map<String, PurchaseRequest> purchaseRequests = new ConcurrentHashMap<>();

    public static void initSampleData() {
        RepairPerson p1 = new RepairPerson();
        p1.setId("rp1");
        p1.setName("张师傅");
        p1.setPhone("13800138001");
        p1.setSpecialty("发动机");
        p1.setAvailable(true);
        repairPersons.put(p1.getId(), p1);

        RepairPerson p2 = new RepairPerson();
        p2.setId("rp2");
        p2.setName("李师傅");
        p2.setPhone("13800138002");
        p2.setSpecialty("电气");
        p2.setAvailable(true);
        repairPersons.put(p2.getId(), p2);

        RepairPerson p3 = new RepairPerson();
        p3.setId("rp3");
        p3.setName("王师傅");
        p3.setPhone("13800138003");
        p3.setSpecialty("底盘");
        p3.setAvailable(true);
        repairPersons.put(p3.getId(), p3);

        RepairStation s1 = new RepairStation();
        s1.setId("rs1");
        s1.setName("1号工位");
        s1.setLocation("A区");
        s1.setAvailable(true);
        repairStations.put(s1.getId(), s1);

        RepairStation s2 = new RepairStation();
        s2.setId("rs2");
        s2.setName("2号工位");
        s2.setLocation("A区");
        s2.setAvailable(true);
        repairStations.put(s2.getId(), s2);

        RepairStation s3 = new RepairStation();
        s3.setId("rs3");
        s3.setName("3号工位");
        s3.setLocation("B区");
        s3.setAvailable(true);
        repairStations.put(s3.getId(), s3);

        SparePart part1 = new SparePart();
        part1.setId("sp1");
        part1.setName("机油滤芯");
        part1.setModel("JX0810");
        part1.setStock(50);
        part1.setUnitPrice(35.0);
        spareParts.put(part1.getId(), part1);

        SparePart part2 = new SparePart();
        part2.setId("sp2");
        part2.setName("空气滤芯");
        part2.setModel("K2025");
        part2.setStock(30);
        part2.setUnitPrice(28.0);
        spareParts.put(part2.getId(), part2);

        SparePart part3 = new SparePart();
        part3.setId("sp3");
        part3.setName("刹车片");
        part3.setModel("BP1001");
        part3.setStock(20);
        part3.setUnitPrice(180.0);
        spareParts.put(part3.getId(), part3);

        SparePart part4 = new SparePart();
        part4.setId("sp4");
        part4.setName("火花塞");
        part4.setModel("PK20TT");
        part4.setStock(40);
        part4.setUnitPrice(45.0);
        spareParts.put(part4.getId(), part4);

        SparePart part5 = new SparePart();
        part5.setId("sp5");
        part5.setName("轮胎");
        part5.setModel("205/55R16");
        part5.setStock(10);
        part5.setUnitPrice(550.0);
        spareParts.put(part5.getId(), part5);
    }
}
