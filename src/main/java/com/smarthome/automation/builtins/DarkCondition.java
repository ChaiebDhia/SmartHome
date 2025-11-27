package com.smarthome.automation.builtins;

import com.smarthome.automation.Context;
import com.smarthome.automation.Condition;
import com.smarthome.model.Home;
import com.smarthome.model.Room;
import com.smarthome.model.sensors.LightSensor;
import com.smarthome.model.sensors.Sensor;

public class DarkCondition implements Condition {
    private final String roomName;

    public DarkCondition(String roomName) { this.roomName = roomName; }

    @Override
    public boolean check(Context context) {
        Home home = context.getHome();
        Room room = home.getRoom(roomName);
        if (room == null) return false;
        for (Sensor s : room.getSensors()) {
            if (s instanceof LightSensor) {
                return ((LightSensor) s).isDark();
            }
        }
        // No sensor present: assume dark so rule can act
        return true;
    }

    @Override
    public String describe() { return "dark in " + roomName; }
}
