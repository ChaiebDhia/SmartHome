package com.smarthome.model;

import com.smarthome.oop.Controllable;
import com.smarthome.oop.EnergyConsumer;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class SmartDevice implements Controllable, EnergyConsumer {
    protected String id;
    protected String name;
    protected String type;
    protected boolean isOn;
    protected String location;
    protected LocalDateTime lastUpdated;
    protected boolean isConnected;

    protected SmartDevice(String name, String type, String location) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.type = type;
        this.location = location;
        this.isOn = false;
        this.isConnected = true;
        this.lastUpdated = LocalDateTime.now();
    }

    @Override
    public void turnOn() { if (isConnected) { isOn = true; lastUpdated = LocalDateTime.now(); onStateChange(); } }

    @Override
    public void turnOff() { if (isConnected) { isOn = false; lastUpdated = LocalDateTime.now(); onStateChange(); } }

    @Override
    public boolean isOn() { return isOn; }

    public void toggle() { if (isOn) turnOff(); else turnOn(); }

    protected void onStateChange() { }

    public abstract double getCurrentPowerConsumption();
    public abstract String getStatus();

    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public boolean isConnected() { return isConnected; }
    public void setConnected(boolean connected) { this.isConnected = connected; }

    @Override
    public String toString() { return String.format("%s [%s] in %s - %s", name, type, location, isOn ? "ON" : "OFF"); }
}
