package com.smarthome.model.devices;

import com.smarthome.model.SmartDevice;

/**
 * Smart Blinds/Curtains with automatic positioning
 */
public class SmartBlinds extends SmartDevice {
    private int position; // 0 (closed) to 100 (fully open)
    private boolean autoMode;
    private static final double MOTOR_POWER = 15.0; // Power when moving
    private static final double IDLE_POWER = 0.2;

    public SmartBlinds(String name, String location) {
        super(name, "Smart Blinds", location);
        this.position = 0; // Start closed
        this.autoMode = false;
    }

    @Override
    public void turnOn() {
        super.turnOn();
        open();
    }

    @Override
    public void turnOff() {
        super.turnOff();
        close();
    }

    public void open() {
        setPosition(100);
    }

    public void close() {
        setPosition(0);
    }

    public void setPosition(int position) {
        if (position < 0 || position > 100) {
            throw new IllegalArgumentException("Position must be between 0 and 100");
        }
        
        if (!isConnected) {
            System.out.println("Cannot move: " + name + " is disconnected");
            return;
        }
        
        int oldPosition = this.position;
        this.position = position;
        this.isOn = position > 0;
        this.lastUpdated = java.time.LocalDateTime.now();
        
        String movement = position > oldPosition ? "opening" : "closing";
        System.out.println(name + " " + movement + " to " + position + "%");
    }

    public void tilt(int degrees) {
        // Simulate tilt adjustment (for horizontal blinds)
        System.out.println(name + " tilted to " + degrees + " degrees");
    }

    public void setAutoMode(boolean enabled) {
        this.autoMode = enabled;
        System.out.println(name + " automatic mode " + (enabled ? "enabled" : "disabled"));
    }

    /**
     * Adjust blinds based on sunlight intensity
     */
    public void adjustForSunlight(int sunlightIntensity) {
        if (!autoMode) return;
        
        if (sunlightIntensity > 80) {
            setPosition(20); // Close mostly to block strong sun
        } else if (sunlightIntensity > 50) {
            setPosition(50); // Partially close
        } else {
            setPosition(100); // Open fully in low light
        }
    }

    @Override
    public double getCurrentPowerConsumption() {
        if (!isConnected) return 0.0;
        // Power consumption is higher during movement, lower when idle
        return IDLE_POWER; // In real scenario, would be higher during actual movement
    }

    @Override
    public String getStatus() {
        StringBuilder status = new StringBuilder();
        if (position == 0) {
            status.append("Closed");
        } else if (position == 100) {
            status.append("Fully Open");
        } else {
            status.append(position).append("% Open");
        }
        
        if (autoMode) {
            status.append(" | Auto Mode ON");
        }
        
        return status.toString();
    }

    // Getters
    public int getPosition() {
        return position;
    }

    public boolean isAutoMode() {
        return autoMode;
    }
}
