package com.smarthome.automation.builtins;

import com.smarthome.automation.Action;
import com.smarthome.automation.Condition;
import com.smarthome.automation.Context;
import com.smarthome.automation.Rule;
import com.smarthome.automation.Trigger;
import com.smarthome.model.Home;
import com.smarthome.model.Room;
import com.smarthome.model.SmartDevice;
import com.smarthome.model.devices.SmartLight;
import com.smarthome.model.sensors.MotionSensor;
import com.smarthome.model.sensors.Sensor;

import java.time.LocalTime;

/**
 * IF motion detected AND time after sunset THEN turn on lights in room.
 */
public class MotionLightRule extends Rule {
    public MotionLightRule(String roomName) {
        super("MotionLight-" + roomName,
                new Trigger() { // trigger: any motion sensor update considered by periodic tick
                    @Override public boolean evaluate(Context context) { return true; }
                }
        );
        addCondition(new Condition() {
            @Override public boolean check(Context context) {
                Home home = context.getHome();
                Room r = home.getRoom(roomName);
                if (r == null) return false;
                for (Sensor s : r.getSensors()) {
                    if (s instanceof MotionSensor) {
                        return ((MotionSensor) s).isMotionDetected() && LocalTime.now().isAfter(LocalTime.of(18,0));
                    }
                }
                return false;
            }
        }).addAction(new Action() {
            @Override public void execute(Context context) {
                Room r = context.getHome().getRoom(roomName);
                if (r == null) return;
                for (SmartDevice d : r.getDevices()) {
                    if (d instanceof SmartLight) { ((SmartLight) d).setBrightness(70); d.turnOn(); }
                }
                System.out.println("[Automation] Motion -> lights ON in " + roomName);
            }
        });
    }
}
