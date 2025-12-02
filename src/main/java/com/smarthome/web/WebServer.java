package com.smarthome.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import com.smarthome.model.Home;
import com.smarthome.model.Room;
import com.smarthome.model.SmartDevice;
import com.smarthome.service.SceneManager;
import spark.Spark;
import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.Map;

public class WebServer {
    private static final Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class,
            (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                src == null ? null : new com.google.gson.JsonPrimitive(src.toString()))
        .create();
    private final Home home;
    private final SceneManager scenes;
    private int chosenPort; // Make port an instance variable

    public WebServer(Home home) {
        this.home = home;
        this.scenes = new SceneManager(home);
    }

    public void start(int port) {
        // Find an available port starting from requested
        this.chosenPort = port; // Store as instance variable
        try {
            java.net.ServerSocket ss = new java.net.ServerSocket();
            ss.setReuseAddress(true);
            ss.bind(new java.net.InetSocketAddress("0.0.0.0", port));
            ss.close();
        } catch (Exception e) {
            for (int p = port + 1; p <= port + 10; p++) {
                try {
                    java.net.ServerSocket ss2 = new java.net.ServerSocket();
                    ss2.setReuseAddress(true);
                    ss2.bind(new java.net.InetSocketAddress("0.0.0.0", p));
                    ss2.close();
                    this.chosenPort = p; // Use instance variable
                    break;
                } catch (Exception ignore) {}
            }
        }
        Spark.port(this.chosenPort);
        Spark.staticFileLocation("public"); // src/main/resources/public

        // CORS for dev
        Spark.options("/*", (req, res) -> {
            String reqHeaders = req.headers("Access-Control-Request-Headers");
            if (reqHeaders != null) res.header("Access-Control-Allow-Headers", reqHeaders);
            String reqMethod = req.headers("Access-Control-Request-Method");
            if (reqMethod != null) res.header("Access-Control-Allow-Methods", reqMethod);
            return "OK";
        });
        Spark.before((req, res) -> res.header("Access-Control-Allow-Origin", "*"));

        Spark.get("/api/home/status", (req, res) -> {
            res.type("application/json");
            Map<String, Object> status = new HashMap<>();
            status.put("name", home.getName());
            status.put("address", home.getAddress());
            status.put("rooms", home.getRooms().size());
            status.put("devices", home.getAllDevices().size());
            status.put("activeDevices", home.getTotalActiveDevices());
            status.put("powerWatts", home.getTotalPowerConsumption());
            status.put("hourlyCost", home.getEstimatedHourlyCost());
            status.put("securityArmed", home.isSecuritySystemArmed());
            status.put("port", this.chosenPort); // Use instance variable
            return gson.toJson(status);
        });

        Spark.get("/api/rooms", (req, res) -> {
            res.type("application/json");
            return gson.toJson(home.getRooms());
        });

        Spark.get("/api/devices", (req, res) -> {
            res.type("application/json");
            return gson.toJson(home.getAllDevices());
        });

        Spark.post("/api/devices/:name/:action", (req, res) -> {
            res.type("application/json");
            String name = req.params(":name");
            String action = req.params(":action");
            SmartDevice d = home.getDeviceByName(name);
            switch (action) {
                case "on": d.turnOn(); break;
                case "off": d.turnOff(); break;
                case "toggle": d.toggle(); break;
                default: res.status(400); return gson.toJson(Map.of("error","unknown action"));
            }
            return gson.toJson(Map.of("status", d.getStatus()));
        });

        Spark.post("/api/scene/:name", (req, res) -> {
            res.type("application/json");
            String name = req.params(":name");
            scenes.apply(name);
            return gson.toJson(Map.of("ok", true));
        });

        Spark.post("/api/security/:action", (req, res) -> {
            res.type("application/json");
            String action = req.params(":action");
            if ("arm".equals(action)) home.armSecuritySystem();
            else if ("disarm".equals(action)) home.disarmSecuritySystem();
            else { res.status(400); return gson.toJson(Map.of("error","unknown action")); }
            return gson.toJson(Map.of("armed", home.isSecuritySystemArmed()));
        });
    }
}