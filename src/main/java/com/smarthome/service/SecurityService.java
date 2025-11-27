package com.smarthome.service;

import com.smarthome.model.Home;
import com.smarthome.model.Room;
import com.smarthome.model.devices.SecurityCamera;
import com.smarthome.model.devices.DoorLock;

public class SecurityService {
    private final Home home;

    public SecurityService(Home home) { this.home = home; }

    public void arm() { home.armSecuritySystem(); }
    public void disarm() { home.disarmSecuritySystem(); }

    public void simulateMotion(String roomName) {
        Room room = home.getRoom(roomName);
        if (room == null) return;
        if (!home.isSecuritySystemArmed()) {
            System.out.println("[Security] Motion ignored (system disarmed)");
            return;
        }
        // Start recording on all cameras in the home
        for (var d : home.getAllDevices()) {
            if (d instanceof SecurityCamera) {
                SecurityCamera cam = (SecurityCamera) d;
                if (!cam.isOn()) cam.turnOn();
                cam.detectMotion();
            }
        }
        // Lock doors as precaution
        home.lockAllDoors();
        System.out.println("[Security] Intrusion procedure executed for room " + roomName);
    }

    public void unlockDoor(String name, String code) {
        var d = home.getDeviceByName(name);
        if (d instanceof DoorLock) {
            ((DoorLock)d).unlock(code);
        }
    }
}
