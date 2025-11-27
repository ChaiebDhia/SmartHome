package com.smarthome.model.devices;

import com.smarthome.model.SmartDevice;
import java.time.LocalDateTime;

/**
 * Security Camera with motion detection and recording capabilities
 */
public class SecurityCamera extends SmartDevice {
    private boolean isRecording;
    private boolean motionDetectionEnabled;
    private int resolution; // 720p, 1080p, 4K
    private boolean nightVisionActive;
    private LocalDateTime lastMotionDetected;
    private static final double BASE_POWER = 8.0; // Watts
    private static final double RECORDING_POWER = 4.0; // Additional watts when recording

    public SecurityCamera(String name, String location) {
        super(name, "Security Camera", location);
        this.isRecording = false;
        this.motionDetectionEnabled = true;
        this.resolution = 1080;
        this.nightVisionActive = false;
    }

    @Override
    public void turnOn() {
        super.turnOn();
        System.out.println(name + " is now monitoring");
    }

    @Override
    public void turnOff() {
        super.turnOff();
        stopRecording();
        System.out.println(name + " stopped monitoring");
    }

    public void startRecording() {
        if (!isOn) {
            System.out.println("Cannot record: " + name + " is off");
            return;
        }
        this.isRecording = true;
        System.out.println(name + " started recording at " + resolution + "p");
    }

    public void stopRecording() {
        this.isRecording = false;
        if (isOn) {
            System.out.println(name + " stopped recording");
        }
    }

    public void detectMotion() {
        if (!isOn || !motionDetectionEnabled) return;
        
        this.lastMotionDetected = LocalDateTime.now();
        System.out.println("⚠️  MOTION DETECTED by " + name + " at " + lastMotionDetected);
        
        if (!isRecording) {
            startRecording();
        }
    }

    public void setResolution(int resolution) {
        if (resolution != 720 && resolution != 1080 && resolution != 2160) {
            throw new IllegalArgumentException("Resolution must be 720, 1080, or 2160 (4K)");
        }
        this.resolution = resolution;
        System.out.println(name + " resolution set to " + resolution + "p");
    }

    public void enableMotionDetection() {
        this.motionDetectionEnabled = true;
        System.out.println(name + " motion detection enabled");
    }

    public void disableMotionDetection() {
        this.motionDetectionEnabled = false;
        System.out.println(name + " motion detection disabled");
    }

    public void toggleNightVision() {
        this.nightVisionActive = !nightVisionActive;
        System.out.println(name + " night vision " + (nightVisionActive ? "enabled" : "disabled"));
    }

    @Override
    public double getCurrentPowerConsumption() {
        if (!isOn) return 0.0;
        double power = BASE_POWER;
        if (isRecording) {
            power += RECORDING_POWER * (resolution / 1080.0);
        }
        if (nightVisionActive) {
            power += 2.0;
        }
        return power;
    }

    @Override
    public String getStatus() {
        if (!isOn) return "OFF";
        StringBuilder status = new StringBuilder("Monitoring");
        if (isRecording) {
            status.append(" | Recording at ").append(resolution).append("p");
        }
        if (motionDetectionEnabled) {
            status.append(" | Motion Detection ON");
        }
        if (nightVisionActive) {
            status.append(" | Night Vision ON");
        }
        return status.toString();
    }

    // Getters
    public boolean isRecording() {
        return isRecording;
    }

    public boolean isMotionDetectionEnabled() {
        return motionDetectionEnabled;
    }

    public int getResolution() {
        return resolution;
    }

    public boolean isNightVisionActive() {
        return nightVisionActive;
    }

    public LocalDateTime getLastMotionDetected() {
        return lastMotionDetected;
    }
}
