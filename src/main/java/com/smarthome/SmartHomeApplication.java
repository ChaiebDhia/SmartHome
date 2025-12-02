package com.smarthome;

import com.smarthome.controller.AutomationEngine;
import com.smarthome.controller.HomeController;
import com.smarthome.model.Home;
import com.smarthome.model.Room;
import com.smarthome.model.devices.SmartLight;
import com.smarthome.model.devices.SmartPlug;
import com.smarthome.model.devices.SecurityCamera;
import com.smarthome.automation.Rule;
import com.smarthome.automation.builtins.*;
import com.smarthome.automation.Condition;
import com.smarthome.automation.Action;
import com.smarthome.scheduler.Scheduler;
import com.smarthome.ui.ConsoleInterface;
import com.smarthome.web.WebServer;

import java.time.LocalTime;
import java.util.Arrays;

public class SmartHomeApplication {
    private static Home home;
    private static AutomationEngine engine;
    private static Thread automationThread;
    private static WebServer webServer;
    
    public static void main(String[] args) throws InterruptedException {
        initializeSmartHome();
        
        // Check launch mode
        boolean webMode = Arrays.stream(args).anyMatch(arg -> 
            arg.equalsIgnoreCase("--web") || arg.equalsIgnoreCase("-w") || 
            arg.equalsIgnoreCase("web"));
            
        boolean javafxMode = Arrays.stream(args).anyMatch(arg -> 
            arg.equalsIgnoreCase("--javafx") || arg.equalsIgnoreCase("-fx") ||
            arg.equalsIgnoreCase("javafx"));

        if (javafxMode) {
            // Launch JavaFX Professional Dashboard
            System.out.println("🚀 Launching JavaFX Professional Dashboard...");
            launchJavaFX();
        } else if (webMode) {
            // Launch Web Interface
            System.out.println("🌐 Launching Web Dashboard...");
            launchWebInterface(8080);
        } else {
            // Default: Enhanced CLI mode
            launchEnhancedCLI();
        }
    }
    
    private static void initializeSmartHome() {
        home = new Home("Smart Residence Pro", "456 Innovation Drive");
        engine = new AutomationEngine(home);
        HomeController controller = new HomeController(home, engine);
        
        // Enhanced setup with more devices
        addEnhancedSetup(controller);
        
        // Add comprehensive automation rules
        addAutomationRules();
        
        // Start automation engine
        startAutomationEngine();
        
        System.out.println("✅ Smart Home System Initialized:");
        System.out.println("   - " + home.getRooms().size() + " Rooms");
        System.out.println("   - " + home.getAllDevices().size() + " Devices");
        System.out.println("   - " + engine.getRules().size() + " Automation Rules");
    }
    
    private static void addEnhancedSetup(HomeController controller) {
        controller.addDefaultSetup();
        
        // Add more devices for comprehensive testing
        Room livingRoom = home.getRoom("Living Room");
        Room kitchen = home.getRoom("Kitchen"); 
        Room bedroom = home.getRoom("Bedroom");
        
        if (livingRoom != null) {
            // Add additional devices
            SmartLight accentLight = new SmartLight("Accent Light", "Living Room");
            SmartPlug speakerPlug = new SmartPlug("Speaker System", "Living Room");
            speakerPlug.connectDevice("Surround Sound", 150.0);
            livingRoom.addDevice(accentLight);
            livingRoom.addDevice(speakerPlug);
        }
        
        if (kitchen != null) {
            SmartPlug coffeeMaker = new SmartPlug("Coffee Maker", "Kitchen");
            coffeeMaker.connectDevice("Coffee Machine", 800.0);
            kitchen.addDevice(coffeeMaker);
        }
        
        // Initialize some devices to ON state for demo
        try {
            home.getDeviceByName("Main Light").turnOn();
            home.getDeviceByName("Kitchen Light").turnOn();
        } catch (Exception e) {
            System.out.println("Note: Some demo devices not available yet");
        }
    }
    
    private static void addAutomationRules() {
        // Evening lights rule
        Rule eveningLights = new Rule("Evening Lights", 
                new TimeAfterTrigger(LocalTime.of(18, 0)))
                .addCondition(new DarkCondition("Living Room"))
                .addAction(new TurnOnRoomLightsAction("Living Room", 60));
        engine.addRule(eveningLights);
        
        // Motion-activated lights
        engine.addRule(new MotionLightRule("Living Room"));
        
        // Security rule - turn on cameras when away
        Rule securityRule = new Rule("Away Mode Security", 
                new TimeAfterTrigger(LocalTime.of(23, 0)))
                .addCondition(new Condition() {
                    @Override 
                    public boolean check(com.smarthome.automation.Context context) {
                        return !home.isSecuritySystemArmed();
                    }
                    @Override 
                    public String describe() { 
                        return "security disarmed"; 
                    }
                })
                .addAction(new Action() {
                    @Override 
                    public void execute(com.smarthome.automation.Context context) {
                        home.getDevicesByType(SecurityCamera.class).forEach(cam -> {
                            if (!cam.isOn()) cam.turnOn();
                        });
                    }
                    @Override 
                    public String describe() { 
                        return "enable cameras"; 
                    }
                });
        engine.addRule(securityRule);
    }
    
    private static void startAutomationEngine() {
        automationThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(5000); // Run every 5 seconds
                    engine.tick(System.currentTimeMillis() / 1000L);
                    
                    // Update sensor readings
                    home.getRooms().forEach(room -> {
                        room.getSensors().forEach(sensor -> sensor.takeMeasurement());
                    });
                    
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        automationThread.setDaemon(true);
        automationThread.start();
    }
    
    private static void launchJavaFX() {
        // Launch the modern JavaFX dashboard
        try {
            com.smarthome.ui.javafx.ModernSmartHomeDashboard.launchApp(home, engine);
        } catch (Exception e) {
            System.err.println("❌ Failed to launch JavaFX: " + e.getMessage());
            e.printStackTrace();
            System.out.println("🔄 Falling back to CLI mode...");
            launchEnhancedCLI();
        }
    }
    
    private static void launchWebInterface(int port) {
        webServer = new WebServer(home);
        webServer.start(port);
        
        // Keep the application running
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            System.out.println("Web server interrupted");
        }
    }
    
    private static void launchEnhancedCLI() {
        HomeController controller = new HomeController(home, engine);
        home.displayHomeStatus();
        
        System.out.println("\n🎮 Available Commands:");
        System.out.println("   status           - Show home status");
        System.out.println("   devices          - List all devices"); 
        System.out.println("   on/off <device>  - Control devices");
        System.out.println("   scene <name>     - Apply scene (morning/movie/night/away)");
        System.out.println("   rules            - List automation rules");
        System.out.println("   motion <room>    - Simulate motion");
        System.out.println("   exit             - Quit application");
        
        ConsoleInterface cli = new ConsoleInterface(home, controller);
        cli.start();
    }
    
    public static Home getHome() { 
        return home; 
    }
    
    public static AutomationEngine getAutomationEngine() { 
        return engine; 
    }
}