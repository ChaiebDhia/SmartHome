package com.smarthome.model.devices;

import com.smarthome.model.SmartDevice;

/**
 * Smart Light with dimming and color temperature control
 */
public class SmartLight extends SmartDevice {
    private int brightness; // 0-100
    private int colorTemperature; // 2700K-6500K (warm to cool)
    private String color; // RGB hex code
    private static final double BASE_POWER = 10.0; // Watts at full brightness

    public SmartLight(String name, String location) {
        super(name, "Smart Light", location);
        this.brightness = 100;
        this.colorTemperature = 3000; // Warm white
        this.color = "#FFFFFF";
    }

    @Override
    public void turnOn() {
        super.turnOn();
        System.out.println(name + " turned ON at " + brightness + "% brightness");
    }

    @Override
    public void turnOff() {
        super.turnOff();
        System.out.println(name + " turned OFF");
    }

    public void setBrightness(int brightness) {
        if (brightness < 0 || brightness > 100) {
            throw new IllegalArgumentException("Brightness must be between 0 and 100");
        }
        this.brightness = brightness;
        this.lastUpdated = java.time.LocalDateTime.now();
        System.out.println(name + " brightness set to " + brightness + "%");
    }

    public void dim(int amount) {
        setBrightness(Math.max(0, brightness - amount));
    }

    public void brighten(int amount) {
        setBrightness(Math.min(100, brightness + amount));
    }

    public void setColorTemperature(int kelvin) {
        if (kelvin < 2700 || kelvin > 6500) {
            throw new IllegalArgumentException("Color temperature must be between 2700K and 6500K");
        }
        this.colorTemperature = kelvin;
        System.out.println(name + " color temperature set to " + kelvin + "K");
    }

    public void setColor(String hexColor) {
        this.color = hexColor;
        System.out.println(name + " color set to " + hexColor);
    }

    @Override
    public double getCurrentPowerConsumption() {
        if (!isOn) return 0.0;
        return BASE_POWER * (brightness / 100.0);
    }

    @Override
    public String getStatus() {
        if (!isOn) return "OFF";
        return String.format("ON - %d%% brightness, %dK, %s", brightness, colorTemperature, color);
    }

    // Getters
    public int getBrightness() {
        return brightness;
    }

    public int getColorTemperature() {
        return colorTemperature;
    }

    public String getColor() {
        return color;
    }
}
