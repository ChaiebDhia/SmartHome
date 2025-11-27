package com.smarthome.controller;

import com.smarthome.model.Home;
import com.smarthome.model.Room;
import com.smarthome.model.devices.*;
import com.smarthome.model.SmartDevice;
import com.smarthome.model.sensors.LightSensor;

public class HomeController {
    private final Home home;
    private final AutomationEngine automationEngine;

    public HomeController(Home home, AutomationEngine automationEngine) {
        this.home = home;
        this.automationEngine = automationEngine;
    }

    public Home getHome() { return home; }
    public AutomationEngine getAutomationEngine() { return automationEngine; }

    public void addDefaultSetup() {
        Room living = new Room("Living Room", "Ground Floor", 30);
        Room kitchen = new Room("Kitchen", "Ground Floor", 18);
        Room bedroom = new Room("Bedroom", "First Floor", 20);

        home.addRoom(living);
        home.addRoom(kitchen);
        home.addRoom(bedroom);

        SmartLight light1 = new SmartLight("Main Light", living.getName());
        SmartLight light2 = new SmartLight("Ambience Light", living.getName());
        Thermostat thermo = new Thermostat("Nest", living.getName(), 21.0);
        SecurityCamera cam = new SecurityCamera("Front Cam", living.getName());
        DoorLock lock = new DoorLock("Front Door", living.getName());
        SmartPlug plug = new SmartPlug("TV Plug", living.getName());
        SmartBlinds blinds = new SmartBlinds("Living Blinds", living.getName());
        SmartTV tv = new SmartTV("Living TV", living.getName());

        living.addDevice(light1);
        living.addDevice(light2);
        living.addDevice(thermo);
        living.addDevice(cam);
        living.addDevice(lock);
        living.addDevice(plug);
        living.addDevice(blinds);
        living.addDevice(tv);

        // Sensors
        LightSensor livingLightSensor = new LightSensor("Living Ambient", living.getName());
        living.addSensor(livingLightSensor);

        SmartLight kLight = new SmartLight("Kitchen Light", kitchen.getName());
        kitchen.addDevice(kLight);
        kitchen.addSensor(new LightSensor("Kitchen Ambient", kitchen.getName()));

        SmartLight bLight = new SmartLight("Bedroom Light", bedroom.getName());
        bedroom.addDevice(bLight);
        bedroom.addSensor(new LightSensor("Bedroom Ambient", bedroom.getName()));
    }
}
