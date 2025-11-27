package com.smarthome;

import com.smarthome.automation.Rule;
import com.smarthome.automation.Context;
import com.smarthome.automation.Trigger;
import com.smarthome.automation.Condition;
import com.smarthome.automation.Action;
import com.smarthome.model.Home;
import com.smarthome.model.Room;
import com.smarthome.model.devices.SmartLight;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RuleTests {
    @Test
    void ruleExecutesWhenTriggerAndConditionsPass() {
        Home home = new Home("Test", "Addr");
        Room room = new Room("Living", "Floor", 10);
        home.addRoom(room);
        SmartLight light = new SmartLight("Lamp", room.getName());
        room.addDevice(light);

        Trigger trigger = ctx -> true;
        Condition condition = ctx -> true;
        final boolean[] ran = {false};
        Action action = ctx -> { light.turnOn(); ran[0] = true; };

        Rule rule = new Rule("TestRule", trigger).addCondition(condition).addAction(action);
        rule.evaluateAndExecute(new Context(home, System.currentTimeMillis()/1000));
        assertTrue(ran[0]);
        assertTrue(light.isOn());
    }
}
