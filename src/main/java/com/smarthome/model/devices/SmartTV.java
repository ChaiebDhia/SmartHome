package com.smarthome.model.devices;

import com.smarthome.model.SmartDevice;

public class SmartTV extends SmartDevice {
    private int volume; // 0-100
    private int channel;
    private String app; // current streaming app
    private static final double BASE_POWER = 70.0; // idle
    private static final double STREAM_POWER = 20.0; // additional when streaming

    public SmartTV(String name, String location) {
        super(name, "Smart TV", location);
        this.volume = 20;
        this.channel = 1;
        this.app = "Home";
    }

    public void setVolume(int volume) {
        if (volume < 0 || volume > 100) throw new IllegalArgumentException("Volume 0-100");
        this.volume = volume;
    }

    public void channelUp() { channel++; }
    public void channelDown() { if (channel > 1) channel--; }
    public void openApp(String app) { this.app = app; if (!isOn) turnOn(); }

    @Override
    public double getCurrentPowerConsumption() {
        if (!isOn) return 0.0;
        double power = BASE_POWER;
        if (!"Home".equals(app)) power += STREAM_POWER;
        return power;
    }

    @Override
    public String getStatus() {
        return isOn ? String.format("Channel %d | Vol %d | App %s", channel, volume, app) : "OFF";
    }
}
