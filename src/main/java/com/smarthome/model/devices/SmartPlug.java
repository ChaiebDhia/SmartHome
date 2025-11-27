package com.smarthome.model.devices;

import com.smarthome.model.SmartDevice;

/**
 * Smart Plug that can control power to connected devices
 */
public class SmartPlug extends SmartDevice {
    private String connectedDevice;
    private double connectedDevicePower; // Watts
    private boolean scheduleEnabled;
    private double energyUsedToday; // kWh
    private static final double PLUG_POWER = 0.3; // Standby power

    public SmartPlug(String name, String location) {
        super(name, "Smart Plug", location);
        this.connectedDevice = "None";
        this.connectedDevicePower = 0.0;
        this.scheduleEnabled = false;
        this.energyUsedToday = 0.0;
    }

    @Override
    public void turnOn() {
        super.turnOn();
        System.out.println(name + " turned ON" + 
            (connectedDevice.equals("None") ? "" : " - Powering " + connectedDevice));
    }

    @Override
    public void turnOff() {
        super.turnOff();
        System.out.println(name + " turned OFF" + 
            (connectedDevice.equals("None") ? "" : " - " + connectedDevice + " powered down"));
    }

    public void connectDevice(String deviceName, double powerRating) {
        this.connectedDevice = deviceName;
        this.connectedDevicePower = powerRating;
        System.out.println(connectedDevice + " (" + powerRating + "W) connected to " + name);
    }

    public void disconnectDevice() {
        System.out.println(connectedDevice + " disconnected from " + name);
        this.connectedDevice = "None";
        this.connectedDevicePower = 0.0;
    }

    public void resetEnergyStats() {
        this.energyUsedToday = 0.0;
        System.out.println("Energy statistics reset for " + name);
    }

    /**
     * Update energy consumption (call this periodically)
     */
    public void updateEnergyUsage(double hours) {
        if (isOn && connectedDevicePower > 0) {
            double energyUsed = (connectedDevicePower / 1000.0) * hours; // Convert to kWh
            energyUsedToday += energyUsed;
        }
    }

    @Override
    public double getCurrentPowerConsumption() {
        double power = PLUG_POWER; // Base standby power
        if (isOn && !connectedDevice.equals("None")) {
            power += connectedDevicePower;
        }
        return power;
    }

    @Override
    public String getStatus() {
        StringBuilder status = new StringBuilder(isOn ? "ON" : "OFF");
        if (!connectedDevice.equals("None")) {
            status.append(" | Connected: ").append(connectedDevice);
            status.append(" (").append(connectedDevicePower).append("W)");
        }
        status.append(String.format(" | Today: %.2f kWh", energyUsedToday));
        return status.toString();
    }

    // Getters
    public String getConnectedDevice() {
        return connectedDevice;
    }

    public double getConnectedDevicePower() {
        return connectedDevicePower;
    }

    public double getEnergyUsedToday() {
        return energyUsedToday;
    }

    public boolean isScheduleEnabled() {
        return scheduleEnabled;
    }

    public void setScheduleEnabled(boolean enabled) {
        this.scheduleEnabled = enabled;
    }
}
