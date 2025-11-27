package com.smarthome.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Abstract base class for all smart home devices.
 * Implements common functionality shared across all device types.
 */
public abstract class Device {
    protected String id;
    protected String name;
    protected String type;
    protected boolean isOn;
    protected String location; // Room name
    protected LocalDateTime lastUpdated;
    protected double powerConsumption; // Watts
    protected boolean isConnected;

    public Device(String name, String type, String location) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.type = type;
        this.location = location;
        this.isOn = false;
        this.isConnected = true;
        this.lastUpdated = LocalDateTime.now();
        this.powerConsumption = 0.0;
    }

    /**
     * Turn the device on
     */
    public void turnOn() {
        if (isConnected) {
            this.isOn = true;
            this.lastUpdated = LocalDateTime.now();
            onStateChange();
        }
    }

    /**
     * Turn the device off
     */
    public void turnOff() {
        if (isConnected) {
            this.isOn = false;
            this.lastUpdated = LocalDateTime.now();
            onStateChange();
        }
    }

    /**
     * Toggle device state
     */
    public void toggle() {
        if (isOn) {
            turnOff();
        } else {
            turnOn();
        }
    }

    /**
     * Get current power consumption in watts
     */
    public abstract double getCurrentPowerConsumption();

    /**
     * Get device status as a human-readable string
     */
    public abstract String getStatus();

    /**
     * Hook method called when device state changes
     */
    protected void onStateChange() {
        // Override in subclasses if needed
    }

    /**
     * Simulate device operation for a given duration
     */
    public void simulate(int seconds) {
        // Override in subclasses for specific simulation behavior
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public boolean isOn() {
        return isOn;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public double getPowerConsumption() {
        return powerConsumption;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        this.isConnected = connected;
    }

    @Override
    public String toString() {
        return String.format("%s [%s] in %s - %s (Connected: %s)", 
            name, type, location, isOn ? "ON" : "OFF", isConnected ? "Yes" : "No");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Device device = (Device) obj;
        return id.equals(device.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
