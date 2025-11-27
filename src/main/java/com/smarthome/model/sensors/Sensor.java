package com.smarthome.model.sensors;

import java.time.LocalDateTime;

/**
 * Base class for environmental sensors
 */
public abstract class Sensor {
    protected String id;
    protected String name;
    protected String location;
    protected LocalDateTime lastReading;
    protected boolean isActive;

    public Sensor(String name, String location) {
        this.id = java.util.UUID.randomUUID().toString();
        this.name = name;
        this.location = location;
        this.isActive = true;
        this.lastReading = LocalDateTime.now();
    }

    /**
     * Take a new sensor reading
     */
    public abstract void takeMeasurement();

    /**
     * Get current sensor value as a string
     */
    public abstract String getCurrentValue();

    /**
     * Check if sensor value is within normal range
     */
    public abstract boolean isValueNormal();

    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getLastReading() {
        return lastReading;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    @Override
    public String toString() {
        return String.format("%s in %s: %s (Last reading: %s)", 
            name, location, getCurrentValue(), lastReading);
    }
}
