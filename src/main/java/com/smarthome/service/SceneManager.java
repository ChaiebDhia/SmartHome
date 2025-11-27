package com.smarthome.service;

import com.smarthome.model.Home;
import com.smarthome.model.Room;
import com.smarthome.model.SmartDevice;
import com.smarthome.model.devices.*;

public class SceneManager {
    private final Home home;

    public SceneManager(Home home) { this.home = home; }

    public void apply(String scene) {
        switch (scene.toLowerCase()) {
            case "away": applyAway(); break;
            case "movie": applyMovie(); break;
            case "night": applyNight(); break;
            case "morning": applyMorning(); break;
            default: System.out.println("Unknown scene: " + scene); return;
        }
        System.out.println("[Scene] Applied: " + scene);
    }

    private void applyAway() {
        home.turnOffAllDevices();
        home.armSecuritySystem();
        for (SmartDevice d : home.getAllDevices()) {
            if (d instanceof Thermostat) {
                ((Thermostat)d).setTargetTemperature(18.0);
            }
        }
    }

    private void applyMovie() {
        Room living = home.getRoom("Living Room");
        if (living == null) return;
        for (SmartDevice d : living.getDevices()) {
            if (d instanceof SmartLight) {
                ((SmartLight)d).setBrightness(30);
                d.turnOn();
            } else if (d instanceof SmartBlinds) {
                ((SmartBlinds)d).close();
            }
        }
    }

    private void applyNight() {
        home.turnOffAllDevices();
        home.armSecuritySystem();
    }

    private void applyMorning() {
        Room living = home.getRoom("Living Room");
        if (living != null) {
            for (SmartDevice d : living.getDevices()) {
                if (d instanceof SmartBlinds) ((SmartBlinds)d).open();
                if (d instanceof SmartLight) {
                    ((SmartLight)d).setBrightness(80);
                    d.turnOn();
                }
            }
        }
        for (SmartDevice d : home.getAllDevices()) {
            if (d instanceof Thermostat) {
                ((Thermostat)d).setTargetTemperature(22.0);
                d.turnOn();
            }
        }
    }
}
