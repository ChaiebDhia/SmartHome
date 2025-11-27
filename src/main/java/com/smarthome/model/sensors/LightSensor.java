package com.smarthome.model.sensors;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Light sensor for measuring ambient light levels
 */
public class LightSensor extends Sensor {
    private int lightLevel; // Lux (0-100000)
    private Random random = new Random();

    public LightSensor(String name, String location) {
        super(name, location);
        this.lightLevel = 300; // Default indoor lighting
    }

    @Override
    public void takeMeasurement() {
        // Simulate light level with random variations
        int variation = random.nextInt(100) - 50;
        lightLevel += variation;
        lightLevel = Math.max(0, Math.min(100000, lightLevel));
        lastReading = LocalDateTime.now();
    }

    /**
     * Manually set light level (for simulation purposes)
     */
    public void setLightLevel(int lux) {
        if (lux < 0 || lux > 100000) {
            throw new IllegalArgumentException("Light level must be between 0 and 100000 lux");
        }
        this.lightLevel = lux;
        this.lastReading = LocalDateTime.now();
    }

    @Override
    public String getCurrentValue() {
        return lightLevel + " lux (" + getLightDescription() + ")";
    }

    @Override
    public boolean isValueNormal() {
        // Normal range depends on context, but we'll use typical indoor range
        return lightLevel >= 100 && lightLevel <= 1000;
    }

    /**
     * Get descriptive text for current light level
     */
    public String getLightDescription() {
        if (lightLevel < 10) {
            return "Dark";
        } else if (lightLevel < 100) {
            return "Dim";
        } else if (lightLevel < 500) {
            return "Indoor Lighting";
        } else if (lightLevel < 10000) {
            return "Bright";
        } else {
            return "Very Bright/Sunlight";
        }
    }

    /**
     * Check if it's dark enough to turn on lights
     */
    public boolean isDark() {
        return lightLevel < 100;
    }

    /**
     * Check if it's bright enough to turn off lights
     */
    public boolean isBright() {
        return lightLevel > 500;
    }

    public int getLightLevel() {
        return lightLevel;
    }
}
