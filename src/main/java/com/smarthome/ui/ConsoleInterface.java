package com.smarthome.ui;

import com.smarthome.controller.HomeController;
import com.smarthome.model.Home;
import com.smarthome.model.Room;
import com.smarthome.model.SmartDevice;
import com.smarthome.model.devices.*;
import com.smarthome.service.EnergyMonitor;
import com.smarthome.service.SceneManager;
import com.smarthome.service.SecurityService;
import com.smarthome.util.DataPersistence;

import java.util.Scanner;

public class ConsoleInterface {
    private final Home home;
    private final HomeController controller;
    private final Scanner scanner = new Scanner(System.in);
    private EnergyMonitor energyMonitor;
    private SceneManager sceneManager;
    private SecurityService securityService;

    public ConsoleInterface(Home home, HomeController controller) {
        this.home = home;
        this.controller = controller;
        this.energyMonitor = new EnergyMonitor(home);
        this.sceneManager = new SceneManager(home);
        this.securityService = new SecurityService(home);
    }

    public void start() {
        System.out.println("\nType 'help' for commands. Type 'exit' to quit.");
        while (true) {
            System.out.print("smarthome> ");
            if (!scanner.hasNextLine()) {
                System.out.println("(Input stream closed – exiting interactive mode)");
                break;
            }
            String line = scanner.nextLine().trim();
            if (line.equalsIgnoreCase("exit")) break;
            if (line.isEmpty()) continue;
            try {
                handle(line);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void handle(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0].toLowerCase();
        switch (cmd) {
            case "help":
                printHelp();
                break;
            case "status":
                home.displayHomeStatus();
                break;
            case "rooms":
                for (Room r : home.getRooms()) System.out.println("- " + r.getStatusSummary());
                break;
            case "devices":
                String roomName = parts.length > 1 ? line.substring(line.indexOf(' ') + 1) : null;
                if (roomName == null) {
                    for (Room r : home.getRooms()) r.displayDevices();
                } else {
                    Room r = home.getRoom(roomName);
                    if (r == null) System.out.println("Room not found"); else r.displayDevices();
                }
                break;
            case "add":
                // add device <type> <room> <name>
                addDevice(parts);
                break;
            case "on":
            case "off":
            case "toggle":
                SmartDevice d = null;
                try { d = home.getDeviceByName(line.substring(cmd.length()).trim()); } catch (Exception ex) { System.out.println(ex.getMessage()); break; }
                if (cmd.equals("on")) d.turnOn();
                else if (cmd.equals("off")) d.turnOff();
                else d.toggle();
                System.out.println(d.getName() + " => " + d.getStatus());
                break;
            case "lights":
                // lights <room> <brightness>
                handleLights(parts);
                break;
            case "thermo":
                // thermo set <device> <temp>
                handleThermo(parts);
                break;
            case "arm":
                securityService.arm();
                break;
            case "disarm":
                securityService.disarm();
                break;
            case "scene":
                if (parts.length < 2) System.out.println("Usage: scene <name>"); else sceneManager.apply(parts[1]);
                break;
            case "energy":
                energyMonitor.update();
                System.out.printf("Energy so far: %.3f kWh%n", energyMonitor.getCumulativeKWh());
                break;
            case "save":
                if (parts.length < 2) { System.out.println("Usage: save <file.json>"); break; }
                try { DataPersistence.saveHome(home, parts[1]); System.out.println("Saved to " + parts[1]); } catch (Exception e) { System.out.println("Save failed: " + e.getMessage()); }
                break;
            case "load":
                if (parts.length < 2) { System.out.println("Usage: load <file.json>"); break; }
                try {
                    Home loaded = DataPersistence.loadHome(parts[1]);
                    System.out.println("Loaded home: " + loaded.getName());
                    loaded.displayHomeStatus();
                } catch (Exception e) { System.out.println("Load failed: " + e.getMessage()); }
                break;
            case "motion":
                if (parts.length < 2) { System.out.println("Usage: motion <room>"); break; }
                securityService.simulateMotion(parts[1]);
                break;
            default:
                System.out.println("Unknown command. Type 'help'.");
        }
    }

    private void addDevice(String[] parts) {
        if (parts.length < 4) { System.out.println("Usage: add device <type> <room> <name>"); return; }
        String type = parts[2].toLowerCase();
        String room = parts[3];
        String name = parts.length >= 5 ? parts[4] : type + "-" + System.currentTimeMillis();
        Room r = home.getRoom(room);
        if (r == null) { System.out.println("Room not found: " + room); return; }
        SmartDevice device;
        switch (type) {
            case "light": device = new SmartLight(name, room); break;
            case "thermostat": device = new Thermostat(name, room, 21.0); break;
            case "camera": device = new SecurityCamera(name, room); break;
            case "lock": device = new DoorLock(name, room); break;
            case "plug": device = new SmartPlug(name, room); break;
            case "blinds": device = new SmartBlinds(name, room); break;
            case "tv": device = new SmartTV(name, room); break;
            default: System.out.println("Unsupported type: " + type); return;
        }
        r.addDevice(device);
        System.out.println("Added " + device);
    }

    private void handleLights(String[] parts) {
        if (parts.length < 3) { System.out.println("Usage: lights <room> <brightness>"); return; }
        String room = parts[1];
        int brightness = Integer.parseInt(parts[2]);
        Room r = home.getRoom(room);
        if (r == null) { System.out.println("Room not found: " + room); return; }
        for (SmartDevice dev : r.getDevices()) {
            if (dev instanceof SmartLight) {
                ((SmartLight) dev).setBrightness(brightness);
                dev.turnOn();
            }
        }
    }

    private void handleThermo(String[] parts) {
        if (parts.length < 4) { System.out.println("Usage: thermo set <deviceName> <temp>"); return; }
        if (!parts[1].equalsIgnoreCase("set")) { System.out.println("Usage: thermo set <deviceName> <temp>"); return; }
        String deviceName = parts[2];
        double temp = Double.parseDouble(parts[3]);
        SmartDevice d;
        try { d = home.getDeviceByName(deviceName); } catch (Exception ex) { System.out.println(ex.getMessage()); return; }
        if (!(d instanceof Thermostat)) { System.out.println("Thermostat not found: " + deviceName); return; }
        ((Thermostat) d).setTargetTemperature(temp);
        d.turnOn();
    }

    private void printHelp() {
        System.out.println("Commands:");
        System.out.println("  status                        - Show full home status");
        System.out.println("  rooms                         - List rooms summary");
        System.out.println("  devices [room]                - List devices (optional room)");
        System.out.println("  add device <type> <room> <name> - Add device");
        System.out.println("  on|off|toggle <deviceName>    - Control device power");
        System.out.println("  lights <room> <brightness>    - Set room lights brightness");
        System.out.println("  thermo set <device> <temp>    - Set thermostat temperature");
        System.out.println("  arm / disarm                  - Security system control");
        System.out.println("  motion <room>                 - Simulate motion event (armed only)");
        System.out.println("  scene <away|movie|night|morning> - Apply predefined scene");
        System.out.println("  energy                        - Show cumulative energy usage");
        System.out.println("  save <file.json>              - Persist current home state");
        System.out.println("  load <file.json>              - Load home state from file");
        System.out.println("  exit                          - Quit");
    }
}
