package com.smarthome.model;

import com.smarthome.model.devices.*;
import com.smarthome.model.SmartDevice;
import com.smarthome.exceptions.DeviceNotFoundException;
import java.util.*;

/**
 * Represents the entire smart home with multiple rooms and devices
 */
public class Home {
    private String name;
    private String address;
    private List<Room> rooms;
    private Map<String, SmartDevice> deviceRegistry; // ID -> Device mapping
    private boolean securitySystemArmed;
    private double electricityRate; // Cost per kWh

    public Home(String name, String address) {
        this.name = name;
        this.address = address;
        this.rooms = new ArrayList<>();
        this.deviceRegistry = new HashMap<>();
        this.securitySystemArmed = false;
        this.electricityRate = 0.12; // Default $0.12 per kWh
    }

    /**
     * Add a room to the home
     */
    public void addRoom(Room room) {
        rooms.add(room);
        System.out.println("Added room: " + room.getName());
    }

    /**
     * Get room by name
     */
    public Room getRoom(String roomName) {
        return rooms.stream()
            .filter(r -> r.getName().equalsIgnoreCase(roomName))
            .findFirst()
            .orElse(null);
    }

    /**
     * Register a device in the home
     */
    public void registerDevice(SmartDevice device) {
        deviceRegistry.put(device.getId(), device);
    }

    /**
     * Get device by ID
     */
    public SmartDevice getDeviceById(String deviceId) {
        SmartDevice d = deviceRegistry.get(deviceId);
        if (d == null) throw new DeviceNotFoundException("Device ID not found: " + deviceId);
        return d;
    }

    /**
     * Get device by name (searches all rooms)
     */
    public SmartDevice getDeviceByName(String deviceName) {
        for (Room room : rooms) {
            SmartDevice device = room.getDevice(deviceName);
            if (device != null) return device;
        }
        throw new DeviceNotFoundException("Device name not found: " + deviceName);
    }

    /**
     * Get all devices in the home
     */
    public List<SmartDevice> getAllDevices() {
        List<SmartDevice> allDevices = new ArrayList<>();
        for (Room room : rooms) allDevices.addAll(room.getDevices());
        return allDevices;
    }

    /**
     * Get all devices of a specific type
     */
    public List<SmartDevice> getDevicesByType(Class<? extends SmartDevice> deviceClass) {
        List<SmartDevice> result = new ArrayList<>();
        for (SmartDevice device : getAllDevices()) if (deviceClass.isInstance(device)) result.add(device);
        return result;
    }

    /**
     * Turn off all devices in the entire home
     */
    public void turnOffAllDevices() {
        System.out.println("\n🏠 Turning off all devices in " + name + "...");
        for (Room room : rooms) {
            room.turnOffAllDevices();
        }
    }

    /**
     * Turn on all lights in the home
     */
    public void turnOnAllLights() {
        System.out.println("\n💡 Turning on all lights in " + name + "...");
        for (Room room : rooms) {
            room.turnOnAllLights();
        }
    }

    /**
     * Lock all doors in the home
     */
    public void lockAllDoors() {
        System.out.println("\n🔒 Locking all doors...");
        List<SmartDevice> locks = getDevicesByType(DoorLock.class);
        for (SmartDevice device : locks) ((DoorLock) device).lock();
    }

    /**
     * Arm security system
     */
    public void armSecuritySystem() {
        this.securitySystemArmed = true;
        System.out.println("\n🛡️  SECURITY SYSTEM ARMED");
        
        // Enable motion detection on all cameras
        List<SmartDevice> cameras = getDevicesByType(SecurityCamera.class);
        for (SmartDevice device : cameras) {
            SecurityCamera camera = (SecurityCamera) device;
            if (!camera.isOn()) {
                camera.turnOn();
            }
            camera.enableMotionDetection();
        }
        
        // Lock all doors
        lockAllDoors();
    }

    /**
     * Disarm security system
     */
    public void disarmSecuritySystem() {
        this.securitySystemArmed = false;
        System.out.println("\n🛡️  Security system disarmed");
    }

    /**
     * Get total power consumption of the entire home
     */
    public double getTotalPowerConsumption() {
        return rooms.stream()
            .mapToDouble(Room::getTotalPowerConsumption)
            .sum();
    }

    /**
     * Calculate estimated energy cost per hour
     */
    public double getEstimatedHourlyCost() {
        double totalWatts = getTotalPowerConsumption();
        double kilowatts = totalWatts / 1000.0;
        return kilowatts * electricityRate;
    }

    /**
     * Get count of all active devices
     */
    public int getTotalActiveDevices() {
        return rooms.stream()
            .mapToInt(Room::getActiveDeviceCount)
            .sum();
    }

    /**
     * Display complete home status
     */
    public void displayHomeStatus() {
        System.out.println("\n" + "═".repeat(70));
        System.out.println("🏠 " + name + " - Smart Home Status");
        System.out.println("📍 " + address);
        System.out.println("═".repeat(70));
        
        System.out.printf("Rooms: %d | Total Devices: %d | Active: %d%n",
            rooms.size(), getAllDevices().size(), getTotalActiveDevices());
        
        System.out.printf("Power Consumption: %.1fW (%.2f kWh) | Cost: $%.2f/hour%n",
            getTotalPowerConsumption(), getTotalPowerConsumption() / 1000.0, getEstimatedHourlyCost());
        
        System.out.printf("Security System: %s%n",
            securitySystemArmed ? "🛡️  ARMED" : "DISARMED");
        
        System.out.println("═".repeat(70));
        
        // Display each room
        for (Room room : rooms) {
            room.displayDevices();
        }
        
        System.out.println("═".repeat(70) + "\n");
    }

    /**
     * Get a summary report
     */
    public void printSummaryReport() {
        System.out.println("\n📊 HOME SUMMARY REPORT");
        System.out.println("─".repeat(50));
        System.out.println("Home: " + name);
        System.out.println("Total Rooms: " + rooms.size());
        System.out.println("Total Devices: " + getAllDevices().size());
        System.out.println("Active Devices: " + getTotalActiveDevices());
        System.out.printf("Current Power Draw: %.1f W%n", getTotalPowerConsumption());
        System.out.printf("Estimated Daily Cost: $%.2f%n", getEstimatedHourlyCost() * 24);
        System.out.printf("Estimated Monthly Cost: $%.2f%n", getEstimatedHourlyCost() * 24 * 30);
        System.out.println("─".repeat(50));
        
        // Device type breakdown
        System.out.println("\nDevice Breakdown:");
        Map<String, Long> deviceCounts = new HashMap<>();
        for (SmartDevice device : getAllDevices()) {
            deviceCounts.merge(device.getType(), 1L, Long::sum);
        }
        deviceCounts.forEach((type, count) -> 
            System.out.println("  • " + type + ": " + count));
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Room> getRooms() {
        return new ArrayList<>(rooms);
    }

    public boolean isSecuritySystemArmed() {
        return securitySystemArmed;
    }

    public double getElectricityRate() {
        return electricityRate;
    }

    public void setElectricityRate(double rate) {
        this.electricityRate = rate;
    }

    @Override
    public String toString() {
        return String.format("Home[%s, %d rooms, %d devices]", 
            name, rooms.size(), getAllDevices().size());
    }
}
