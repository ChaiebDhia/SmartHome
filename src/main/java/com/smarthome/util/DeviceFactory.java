package com.smarthome.util;

import com.google.gson.JsonObject;
import com.smarthome.model.SmartDevice;
import com.smarthome.model.devices.*;

public class DeviceFactory {
    public static SmartDevice fromJson(JsonObject obj) {
        String type = obj.get("type").getAsString();
        String name = obj.get("name").getAsString();
        String room = obj.get("room").getAsString();
        boolean on = obj.get("on").getAsBoolean();
        SmartDevice d;
        switch (type) {
            case "Smart Light":
                d = new SmartLight(name, room);
                if (obj.has("brightness")) {
                    ((SmartLight) d).setBrightness(obj.get("brightness").getAsInt());
                }
                break;
            case "Thermostat":
                d = new Thermostat(name, room, obj.has("currentTemp") ? obj.get("currentTemp").getAsDouble() : 21.0);
                if (obj.has("targetTemp")) {
                    ((Thermostat) d).setTargetTemperature(obj.get("targetTemp").getAsDouble());
                }
                break;
            case "Security Camera":
                d = new SecurityCamera(name, room);
                break;
            case "Door Lock":
                d = new DoorLock(name, room);
                if (!obj.get("locked").getAsBoolean()) {
                    ((DoorLock) d).unlock("1234");
                }
                break;
            case "Smart Plug":
                d = new SmartPlug(name, room);
                break;
            case "Smart Blinds":
                d = new SmartBlinds(name, room);
                if (obj.has("position")) {
                    ((SmartBlinds) d).setPosition(obj.get("position").getAsInt());
                }
                break;
            case "Smart TV":
                d = new SmartTV(name, room);
                break;
            default:
                throw new IllegalArgumentException("Unknown device type: " + type);
        }
        if (on) d.turnOn(); else d.turnOff();
        return d;
    }
}
