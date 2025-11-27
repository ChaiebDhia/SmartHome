package com.smarthome.model.sensors;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Motion sensor for detecting movement in rooms
 */
public class MotionSensor extends Sensor {
    private boolean motionDetected;
    private LocalDateTime lastMotionTime;
    private int sensitivityLevel; // 1-10
    private Random random = new Random();

    public MotionSensor(String name, String location) {
        super(name, location);
        this.motionDetected = false;
        this.sensitivityLevel = 5;
    }

    @Override
    public void takeMeasurement() {
        // Simulate random motion detection
        if (random.nextInt(100) < 15) { // 15% chance of detecting motion
            detectMotion();
        } else {
            motionDetected = false;
        }
        lastReading = LocalDateTime.now();
    }

    /**
     * Trigger motion detection
     */
    public void detectMotion() {
        this.motionDetected = true;
        this.lastMotionTime = LocalDateTime.now();
        System.out.println("⚠️  MOTION DETECTED by " + name + " in " + location);
    }

    /**
     * Clear motion detection flag
     */
    public void clearMotion() {
        this.motionDetected = false;
    }

    @Override
    public String getCurrentValue() {
        if (motionDetected) {
            return "Motion Detected";
        } else if (lastMotionTime != null) {
            return "Clear (Last: " + lastMotionTime.toLocalTime() + ")";
        } else {
            return "Clear";
        }
    }

    @Override
    public boolean isValueNormal() {
        // For security purposes, motion detection is "abnormal"
        return !motionDetected;
    }

    // Getters and Setters
    public boolean isMotionDetected() {
        return motionDetected;
    }

    public LocalDateTime getLastMotionTime() {
        return lastMotionTime;
    }

    public int getSensitivityLevel() {
        return sensitivityLevel;
    }

    public void setSensitivityLevel(int level) {
        if (level < 1 || level > 10) {
            throw new IllegalArgumentException("Sensitivity must be between 1 and 10");
        }
        this.sensitivityLevel = level;
    }
}
