package com.smarthome.model.devices;

import com.smarthome.model.SmartDevice;

/**
 * Smart Door Lock with access control
 */
public class DoorLock extends SmartDevice {
    private boolean isLocked;
    private String lockCode;
    private boolean autoLockEnabled;
    private int autoLockDelay; // seconds
    private static final double POWER = 0.5; // Very low power consumption

    public DoorLock(String name, String location) {
        super(name, "Door Lock", location);
        this.isLocked = true;
        this.lockCode = "1234"; // Default code
        this.autoLockEnabled = true;
        this.autoLockDelay = 30;
    }

    @Override
    public void turnOn() {
        super.turnOn();
        lock();
    }

    @Override
    public void turnOff() {
        super.turnOff();
        // Locks can't really be "off" for security, just disconnected
    }

    public boolean lock() {
        if (!isConnected) {
            System.out.println("Cannot lock: " + name + " is disconnected");
            return false;
        }
        this.isLocked = true;
        System.out.println("🔒 " + name + " LOCKED");
        return true;
    }

    public boolean unlock(String code) {
        if (!isConnected) {
            System.out.println("Cannot unlock: " + name + " is disconnected");
            return false;
        }
        
        if (code.equals(lockCode)) {
            this.isLocked = false;
            System.out.println("🔓 " + name + " UNLOCKED");
            
            if (autoLockEnabled) {
                System.out.println("Auto-lock will engage in " + autoLockDelay + " seconds");
            }
            return true;
        } else {
            System.out.println("❌ INVALID CODE for " + name);
            return false;
        }
    }

    public void changeLockCode(String oldCode, String newCode) {
        if (oldCode.equals(lockCode)) {
            this.lockCode = newCode;
            System.out.println("Lock code changed successfully for " + name);
        } else {
            System.out.println("❌ Invalid old code. Cannot change lock code.");
        }
    }

    public void setAutoLock(boolean enabled, int delaySeconds) {
        this.autoLockEnabled = enabled;
        this.autoLockDelay = delaySeconds;
        System.out.println(name + " auto-lock " + (enabled ? "enabled" : "disabled") + 
                         (enabled ? " with " + delaySeconds + "s delay" : ""));
    }

    @Override
    public double getCurrentPowerConsumption() {
        return isConnected ? POWER : 0.0;
    }

    @Override
    public String getStatus() {
        String status = isLocked ? "🔒 LOCKED" : "🔓 UNLOCKED";
        if (autoLockEnabled && !isLocked) {
            status += " (Auto-lock in " + autoLockDelay + "s)";
        }
        return status;
    }

    // Getters
    public boolean isLocked() {
        return isLocked;
    }

    public boolean isAutoLockEnabled() {
        return autoLockEnabled;
    }

    public int getAutoLockDelay() {
        return autoLockDelay;
    }
}
