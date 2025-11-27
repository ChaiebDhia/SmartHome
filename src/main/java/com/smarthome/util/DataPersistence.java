package com.smarthome.util;

import com.google.gson.*;
import com.smarthome.model.*;
import com.smarthome.model.devices.*;
import com.smarthome.model.sensors.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DataPersistence {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void saveHome(Home home, String path) throws IOException {
        JsonObject root = new JsonObject();
        root.addProperty("name", home.getName());
        root.addProperty("address", home.getAddress());

        JsonArray roomsArr = new JsonArray();
        for (Room r : home.getRooms()) {
            JsonObject rObj = new JsonObject();
            rObj.addProperty("name", r.getName());
            rObj.addProperty("floor", r.getFloor());
            rObj.addProperty("area", r.getArea());

            JsonArray devicesArr = new JsonArray();
            for (SmartDevice d : r.getDevices()) {
                JsonObject dObj = new JsonObject();
                dObj.addProperty("type", d.getType());
                dObj.addProperty("name", d.getName());
                dObj.addProperty("room", r.getName());
                dObj.addProperty("on", d.isOn());
                if (d instanceof SmartLight) {
                    dObj.addProperty("brightness", ((SmartLight) d).getBrightness());
                } else if (d instanceof Thermostat) {
                    dObj.addProperty("currentTemp", ((Thermostat) d).getCurrentTemperature());
                    dObj.addProperty("targetTemp", ((Thermostat) d).getTargetTemperature());
                } else if (d instanceof DoorLock) {
                    dObj.addProperty("locked", ((DoorLock) d).isLocked());
                } else if (d instanceof SmartBlinds) {
                    dObj.addProperty("position", ((SmartBlinds) d).getPosition());
                }
                devicesArr.add(dObj);
            }
            rObj.add("devices", devicesArr);

            JsonArray sensorsArr = new JsonArray();
            for (Sensor s : r.getSensors()) {
                JsonObject sObj = new JsonObject();
                sObj.addProperty("type", s.getClass().getSimpleName());
                sObj.addProperty("name", s.getName());
                sensorsArr.add(sObj);
            }
            rObj.add("sensors", sensorsArr);
            roomsArr.add(rObj);
        }
        root.add("rooms", roomsArr);
        try (FileWriter fw = new FileWriter(path)) {
            gson.toJson(root, fw);
        }
    }

    public static Home loadHome(String path) throws IOException {
        try (FileReader fr = new FileReader(path)) {
            JsonObject root = JsonParser.parseReader(fr).getAsJsonObject();
            Home home = new Home(root.get("name").getAsString(), root.get("address").getAsString());
            JsonArray roomsArr = root.get("rooms").getAsJsonArray();
            for (JsonElement re : roomsArr) {
                JsonObject rObj = re.getAsJsonObject();
                Room room = new Room(rObj.get("name").getAsString(), rObj.get("floor").getAsString(), rObj.get("area").getAsDouble());
                home.addRoom(room);
                JsonArray devs = rObj.get("devices").getAsJsonArray();
                for (JsonElement de : devs) {
                    SmartDevice d = DeviceFactory.fromJson(de.getAsJsonObject());
                    room.addDevice(d);
                }
                JsonArray sens = rObj.get("sensors").getAsJsonArray();
                for (JsonElement se : sens) {
                    JsonObject sObj = se.getAsJsonObject();
                    String type = sObj.get("type").getAsString();
                    String name = sObj.get("name").getAsString();
                    switch (type) {
                        case "LightSensor": room.addSensor(new LightSensor(name, room.getName())); break;
                        case "TemperatureSensor": room.addSensor(new TemperatureSensor(name, room.getName())); break;
                        case "MotionSensor": room.addSensor(new MotionSensor(name, room.getName())); break;
                        case "HumiditySensor": room.addSensor(new HumiditySensor(name, room.getName())); break;
                        default: break;
                    }
                }
            }
            return home;
        }
    }
}
