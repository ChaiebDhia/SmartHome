package com.smarthome.model;

import com.smarthome.model.devices.*;
import com.smarthome.model.SmartDevice;
import com.smarthome.model.sensors.Sensor;
import java.util.*;

/**
 * Represents a room in the smart home containing multiple devices
 */
public class Room {
    private String name;
    private String floor;
    private List<SmartDevice> devices;
    private List<Sensor> sensors;
    private double area; // Square meters
    private double currentTemperature;

    public Room(String name, String floor, double area) {
        this.name = name;
        this.floor = floor;
        this.area = area;
        this.devices = new ArrayList<>();
        this.sensors = new ArrayList<>();
        this.currentTemperature = 20.0; // Default room temperature
    }

    /**
     * Add a device to this room
     */
    public void addDevice(SmartDevice device) {
        devices.add(device);
        device.setLocation(name);
        System.out.println("Added " + device.getName() + " to " + name);
    }

    /**
     * Add a sensor to this room
     */
    public void addSensor(Sensor sensor) {
        sensors.add(sensor);
        System.out.println("Added sensor " + sensor.getName() + " to " + name);
    }

    /**
     * Remove a device from this room
     */
    public boolean removeDevice(SmartDevice device) { return devices.remove(device); }

    /**
     * Get device by name
     */
    public SmartDevice getDevice(String deviceName) {
        return devices.stream()
            .filter(d -> d.getName().equalsIgnoreCase(deviceName))
            .findFirst()
            .orElse(null);
    }

    /**
     * Get all devices of a specific type
     */
    public List<SmartDevice> getDevicesByType(String type) {
        List<SmartDevice> result = new ArrayList<>();
        for (SmartDevice device : devices) {
            if (device.getType().equalsIgnoreCase(type)) {
                result.add(device);
            }
        }
        return result;
    }

    /**
     * Turn off all devices in the room
     */
    public void turnOffAllDevices() {
        System.out.println("\n🔌 Turning off all devices in " + name + "...");
        for (SmartDevice device : devices) {
            if (device.isOn()) {
                device.turnOff();
            }
        }
    }

    /**
     * Turn on all lights in the room
     */
    public void turnOnAllLights() {
        System.out.println("\n💡 Turning on all lights in " + name + "...");
        for (SmartDevice device : devices) {
            if (device instanceof SmartLight) {
                device.turnOn();
            }
        }
    }

    /**
     * Get total power consumption of all devices in this room
     */
    public double getTotalPowerConsumption() {
        return devices.stream()
            .mapToDouble(SmartDevice::getCurrentPowerConsumption)
            .sum();
    }

    /**
     * Get count of active (on) devices
     */
    public int getActiveDeviceCount() {
        return (int) devices.stream()
            .filter(SmartDevice::isOn)
            .count();
    }

    /**
     * Update temperature for thermostats in this room
     */
    public void updateTemperature(double ambientTemp) {
        this.currentTemperature = ambientTemp;
        for (SmartDevice device : devices) {
            if (device instanceof Thermostat) {
                ((Thermostat) device).updateTemperature(ambientTemp);
            }
        }
    }

    /**
     * Get room status summary
     */
    public String getStatusSummary() {
        return String.format("%s (%s) - %d devices (%d active) - %.1fW - %.1f°C",
            name, floor, devices.size(), getActiveDeviceCount(), 
            getTotalPowerConsumption(), currentTemperature);
    }

    /**
     * Display all devices in the room
     */
    public void displayDevices() {
        System.out.println("\n📍 " + name + " (" + floor + "):");
        System.out.println("─".repeat(60));
        if (devices.isEmpty()) {
            System.out.println("  No devices in this room");
        } else {
            for (SmartDevice device : devices) {
                System.out.printf("  • %s: %s (%.1fW)%n", 
                    device.getName(), device.getStatus(), device.getCurrentPowerConsumption());
            }
        }
        System.out.printf("  Total Power: %.1fW | Temperature: %.1f°C%n", 
            getTotalPowerConsumption(), currentTemperature);
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public List<SmartDevice> getDevices() { return new ArrayList<>(devices); }

    public List<Sensor> getSensors() {
        return new ArrayList<>(sensors);
    }

    public double getArea() {
        return area;
    }

    public double getCurrentTemperature() {
        return currentTemperature;
    }

    public void setCurrentTemperature(double temperature) {
        this.currentTemperature = temperature;
    }

    @Override
    public String toString() {
        return String.format("Room[%s, %s, %d devices]", name, floor, devices.size());
    }
}
