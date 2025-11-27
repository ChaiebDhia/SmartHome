package com.smarthome.model.sensors;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Humidity sensor for monitoring moisture levels
 */
public class HumiditySensor extends Sensor {
    private double currentHumidity; // Percentage 0-100
    private double minNormal = 30.0;
    private double maxNormal = 60.0;
    private Random random = new Random();

    public HumiditySensor(String name, String location) {
        super(name, location);
        this.currentHumidity = 45.0; // Default comfortable humidity
    }

    @Override
    public void takeMeasurement() {
        // Simulate humidity reading with random variations
        double variation = (random.nextDouble() - 0.5) * 2.0; // ±1%
        currentHumidity += variation;
        currentHumidity = Math.max(0, Math.min(100, currentHumidity)); // Clamp to 0-100
        currentHumidity = Math.round(currentHumidity * 10.0) / 10.0;
        lastReading = LocalDateTime.now();
    }

    /**
     * Manually set humidity (for simulation purposes)
     */
    public void setHumidity(double humidity) {
        if (humidity < 0 || humidity > 100) {
            throw new IllegalArgumentException("Humidity must be between 0 and 100");
        }
        this.currentHumidity = humidity;
        this.lastReading = LocalDateTime.now();
    }

    @Override
    public String getCurrentValue() {
        return String.format("%.1f%%", currentHumidity);
    }

    @Override
    public boolean isValueNormal() {
        return currentHumidity >= minNormal && currentHumidity <= maxNormal;
    }

    /**
     * Get humidity status description
     */
    public String getHumidityStatus() {
        if (currentHumidity < 30) {
            return "Too Dry";
        } else if (currentHumidity > 60) {
            return "Too Humid";
        } else {
            return "Comfortable";
        }
    }

    public double getCurrentHumidity() {
        return currentHumidity;
    }

    public void setNormalRange(double min, double max) {
        this.minNormal = min;
        this.maxNormal = max;
    }

    public double getMinNormal() {
        return minNormal;
    }

    public double getMaxNormal() {
        return maxNormal;
    }
}
