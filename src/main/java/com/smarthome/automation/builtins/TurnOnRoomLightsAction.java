package com.smarthome.automation.builtins;

import com.smarthome.automation.Action;
import com.smarthome.automation.Context;
import com.smarthome.model.Room;
import com.smarthome.model.SmartDevice;
import com.smarthome.model.devices.SmartLight;

public class TurnOnRoomLightsAction implements Action {
    private final String roomName;
    private final int brightness;

    public TurnOnRoomLightsAction(String roomName, int brightness) {
        this.roomName = roomName;
        this.brightness = brightness;
    }

    @Override
    public void execute(Context context) {
        Room r = context.getHome().getRoom(roomName);
        if (r == null) return;
        for (SmartDevice d : r.getDevices()) {
            if (d instanceof SmartLight) {
                ((SmartLight)d).setBrightness(brightness);
                d.turnOn();
            }
        }
        System.out.println("[Rule] Lights on in " + roomName + " at " + brightness + "%");
    }

    @Override
    public String describe() { return "turn on lights in " + roomName; }
}
