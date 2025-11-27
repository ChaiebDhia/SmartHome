package com.smarthome;

import com.smarthome.controller.AutomationEngine;
import com.smarthome.controller.HomeController;
import com.smarthome.model.Home;
import com.smarthome.ui.ConsoleInterface;
import com.smarthome.automation.Rule;
import com.smarthome.automation.builtins.TimeAfterTrigger;
import com.smarthome.automation.builtins.DarkCondition;
import com.smarthome.automation.builtins.TurnOnRoomLightsAction;
import com.smarthome.automation.builtins.MotionLightRule;
import com.smarthome.scheduler.Scheduler;
import java.time.LocalTime;

public class SmartHomeApplication {
    public static void main(String[] args) throws InterruptedException {
        Home home = new Home("Demo Home", "123 Main St");
        AutomationEngine engine = new AutomationEngine(home);
        HomeController controller = new HomeController(home, engine);
        controller.addDefaultSetup();

        // Add a sample automation rule: after 18:00 and dark, turn on Living Room lights to 60%
        Rule eveningLights = new Rule("Evening Lights",
                new TimeAfterTrigger(LocalTime.of(18, 0)))
                .addCondition(new DarkCondition("Living Room"))
                .addAction(new TurnOnRoomLightsAction("Living Room", 60));
        engine.addRule(eveningLights);

        // Motion rule
        engine.addRule(new MotionLightRule("Living Room"));

        // Scheduler sample: Turn on thermostat at 06:00
        Scheduler scheduler = new Scheduler();
        scheduler.add(LocalTime.of(6,0), "Morning heating", () -> {
            var thermo = home.getDeviceByName("Nest");
            thermo.turnOn();
        });

        // Kick the engine once on startup
        engine.tick(System.currentTimeMillis() / 1000L);
        scheduler.tick();

        // Start interactive console
        home.displayHomeStatus();
        ConsoleInterface cli = new ConsoleInterface(home, controller);
        cli.start();
    }
}
