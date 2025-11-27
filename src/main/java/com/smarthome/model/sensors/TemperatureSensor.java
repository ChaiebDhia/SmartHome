package com.smarthome.model.sensors;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Temperature sensor for monitoring room temperature
 */
public class TemperatureSensor extends Sensor {
    private double currentTemperature; // Celsius
    private double minNormal = 18.0;
    private double maxNormal = 26.0;
    private Random random = new Random();

    public TemperatureSensor(String name, String location) {
        super(name, location);
        this.currentTemperature = 22.0; // Default comfortable temperature
    }

    @Override
    public void takeMeasurement() {
        // Simulate temperature reading with small random variations
        double variation = (random.nextDouble() - 0.5) * 0.5; // ±0.25°C
        currentTemperature += variation;
        currentTemperature = Math.round(currentTemperature * 10.0) / 10.0;
        lastReading = LocalDateTime.now();
    }

    /**
     * Manually set temperature (for simulation purposes)
     */
    public void setTemperature(double temperature) {
        this.currentTemperature = temperature;
        this.lastReading = LocalDateTime.now();
    }

    @Override
    public String getCurrentValue() {
        return String.format("%.1f°C", currentTemperature);
    }

    @Override
    public boolean isValueNormal() {
        return currentTemperature >= minNormal && currentTemperature <= maxNormal;
    }

    public double getCurrentTemperature() {
        return currentTemperature;
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
