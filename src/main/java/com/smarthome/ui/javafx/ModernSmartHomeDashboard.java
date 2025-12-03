package com.smarthome.ui.javafx;

import com.smarthome.controller.AutomationEngine;
import com.smarthome.controller.HomeController;
import com.smarthome.model.*;
import com.smarthome.model.devices.*;
import com.smarthome.model.sensors.*;
import com.smarthome.automation.Rule;
import com.smarthome.automation.Trigger;
import com.smarthome.automation.Action;
import com.smarthome.automation.Context;
import com.smarthome.service.SceneManager;
import com.smarthome.service.SecurityService;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Modern White-Themed Smart Home Dashboard
 * Professional, clean design with full functionality
 */
public class ModernSmartHomeDashboard extends Application {
    
    // Static references for external launch
    private static Home staticHome;
    private static AutomationEngine staticEngine;
    
    // Core Components
    private Home home;
    private HomeController controller;
    private AutomationEngine automationEngine;
    private SceneManager sceneManager;
    private SecurityService securityService;
    private ScheduledExecutorService scheduler;
    
    // Observable Data
    private ObservableList<DeviceData> deviceData = FXCollections.observableArrayList();
    private ObservableList<RoomData> roomData = FXCollections.observableArrayList();
    
    // Properties for Live Data Binding
    private DoubleProperty totalPower = new SimpleDoubleProperty(0);
    private DoubleProperty hourlyCost = new SimpleDoubleProperty(0);
    private IntegerProperty activeDevices = new SimpleIntegerProperty(0);
    private IntegerProperty totalDevices = new SimpleIntegerProperty(0);
    private BooleanProperty securityArmed = new SimpleBooleanProperty(false);
    
    // Charts for dynamic updates
    private LineChart<String, Number> powerConsumptionChart;
    private PieChart energyByRoomChart;
    private StringProperty currentTime = new SimpleStringProperty();
    
    // Control button references for state updates
    private Button allLightsOnBtn;
    private Button allLightsOffBtn;
    private Button allDevicesOnBtn;
    private Button allDevicesOffBtn;
    private Button armSecurityBtn;
    private Button disarmSecurityBtn;
    
    // Alarm animation
    private Circle alarmIndicator;
    private Timeline alarmAnimation;
    
    // UI Components
    private BorderPane root;
    private VBox mainTabsContainer;
    private VBox dashboardTab;
    private VBox devicesTab;
    private VBox roomsTab;
    private VBox automationTab;
    private FlowPane roomSummaryGrid;
    
    // Search/Filter State (to preserve during refreshes)
    private String currentSearchText = "";
    private String currentTypeFilter = "All Types";
    private String currentRoomFilter = "All Rooms";
    
    // Color Theme - Modern IoT Design
    private final String PRIMARY = "#2563eb";      // Blue accent
    private final String PRIMARY_LIGHT = "#60a5fa"; // Light blue
    private final String PRIMARY_GRADIENT = "linear-gradient(135deg, #667eea 0%, #764ba2 100%)"; // Purple-blue gradient
    private final String SUCCESS = "#10b981";      // Green
    private final String SUCCESS_GRADIENT = "linear-gradient(to right, #10b981, #059669)"; // Green gradient
    private final String WARNING = "#f59e0b";      // Orange
    private final String DANGER = "#ef4444";       // Red
    private final String DANGER_GRADIENT = "linear-gradient(to right, #ef4444, #dc2626)"; // Red gradient
    private final String BACKGROUND = "#f0f4f8";   // Soft gray background
    private final String CARD_BG = "#ffffff";      // White cards
    private final String CARD_GRADIENT = "linear-gradient(135deg, #667eea 0%, #764ba2 100%)";
    private final String BORDER = "#e2e8f0";       // Light border
    private final String TEXT_PRIMARY = "#1e293b"; // Dark text
    private final String TEXT_SECONDARY = "#64748b"; // Gray text
    private final String GLASS_BG = "rgba(255, 255, 255, 0.7)"; // Glass morphism
    private final String IOT_CYAN = "#06b6d4";     // IoT cyan accent
    private final String IOT_PURPLE = "#8b5cf6";   // IoT purple accent
    
    // Scene-specific theme colors
    private String currentSceneColor = CARD_BG;    // Current active scene color
    private Label sceneIndicator;                   // Shows active scene
    private VBox quickActionsPanel;                 // Reference for theme updates
    
    @Override
    public void start(Stage primaryStage) {
        initializeSmartHome();
        setupScheduler();
        createUI(primaryStage);
        
        primaryStage.setTitle("Smart Home Control Center");
        primaryStage.setMaximized(true);
        primaryStage.show();
        
        // Initial data load
        refreshAllData();
        
        primaryStage.setOnCloseRequest(e -> shutdown());
    }
    
    private void initializeSmartHome() {
        home = new Home("My Smart Home", "123 Main Street");
        automationEngine = new AutomationEngine(home);
        controller = new HomeController(home, automationEngine);
        sceneManager = new SceneManager(home);
        securityService = new SecurityService(home);
        
        // Add default setup
        controller.addDefaultSetup();
        
        // Add additional devices for testing
        addExtraDevices();
        
        // Add automation rules
        addAutomationRules();
    }
    
    private void addExtraDevices() {
        Room livingRoom = home.getRoom("Living Room");
        Room kitchen = home.getRoom("Kitchen");
        Room bedroom = home.getRoom("Bedroom");
        
        if (livingRoom != null) {
            SmartLight accentLight = new SmartLight("Floor Lamp", "Living Room");
            SmartPlug tvPlug = new SmartPlug("TV Plug", "Living Room");
            tvPlug.connectDevice("Smart TV 55\"", 120.0);
            livingRoom.addDevice(accentLight);
            livingRoom.addDevice(tvPlug);
            
            MotionSensor motion = new MotionSensor("Motion Detector", "Living Room");
            TemperatureSensor temp = new TemperatureSensor("Temp Sensor", "Living Room");
            livingRoom.addSensor(motion);
            livingRoom.addSensor(temp);
        }
        
        if (kitchen != null) {
            SmartPlug coffeeMaker = new SmartPlug("Coffee Maker Plug", "Kitchen");
            coffeeMaker.connectDevice("Coffee Machine", 900.0);
            kitchen.addDevice(coffeeMaker);
            
            HumiditySensor humidity = new HumiditySensor("Humidity Sensor", "Kitchen");
            kitchen.addSensor(humidity);
        }
        
        if (bedroom != null) {
            SmartLight nightLight = new SmartLight("Bedside Lamp", "Bedroom");
            SmartBlinds blinds = new SmartBlinds("Window Blinds", "Bedroom");
            bedroom.addDevice(nightLight);
            bedroom.addDevice(blinds);
        }
    }
    
    private void addAutomationRules() {
        // Evening lights automation
        Rule eveningRule = new Rule("Evening Lights",
            new com.smarthome.automation.builtins.TimeAfterTrigger(LocalTime.of(18, 0)))
            .addCondition(new com.smarthome.automation.builtins.DarkCondition("Living Room"))
            .addAction(new com.smarthome.automation.builtins.TurnOnRoomLightsAction("Living Room", 70));
        automationEngine.addRule(eveningRule);
        
        // Motion light rule
        automationEngine.addRule(new com.smarthome.automation.builtins.MotionLightRule("Living Room"));
    }
    
    private void setupScheduler() {
        scheduler = Executors.newScheduledThreadPool(2);
        
        // Update data every 5 seconds (slowed down for smoother experience)
        scheduler.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                automationEngine.tick(System.currentTimeMillis() / 1000L);
                refreshAllData();
                currentTime.set(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            });
        }, 0, 5, TimeUnit.SECONDS);
        
        // Simulate sensor changes every 5 seconds
        scheduler.scheduleAtFixedRate(() -> {
            Platform.runLater(this::simulateSensorChanges);
        }, 5, 5, TimeUnit.SECONDS);
    }
    
    private void createUI(Stage stage) {
        root = new BorderPane();
        // Clean professional background
        root.setStyle("-fx-background-color: #f8fafc;");
        
        // Top Header
        root.setTop(createHeader());
        
        // Main Content with Tabs and Custom Navbar
        mainTabsContainer = createMainTabs();
        root.setCenter(mainTabsContainer);
        
        // Status Bar
        root.setBottom(createStatusBar());
        
        Scene scene = new Scene(root, 1400, 900);
        stage.setScene(scene);
    }
    
    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(20, 30, 20, 30));
        header.setAlignment(Pos.CENTER_LEFT);
        // Modern deep blue IoT gradient - professional and high contrast
        header.setStyle("-fx-background-color: linear-gradient(to right, #1e3a8a, #3b82f6, #1e3a8a); " +
                       "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 15, 0, 0, 5);");
        
        // Logo and Title
        VBox titleBox = new VBox(5);
        Label title = new Label("Smart Home IoT Hub");
        title.setFont(Font.font("System", FontWeight.BOLD, 26));
        title.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold; " +
                      "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.6), 8, 0, 0, 0);");
        
        Label subtitle = new Label(home.getName() + " • " + home.getAddress());
        subtitle.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.9); -fx-font-size: 13px;");
        
        titleBox.getChildren().addAll(title, subtitle);
        
        // Alarm indicator with animation
        alarmIndicator = new Circle(8);
        alarmIndicator.setFill(Color.web(SUCCESS));
        alarmIndicator.setStroke(Color.WHITE);
        alarmIndicator.setStrokeWidth(2.5);
        alarmIndicator.setEffect(new DropShadow(12, Color.web("#00000055")));
        
        Label alarmLabel = new Label("READY");
        alarmLabel.setFont(Font.font("System", FontWeight.BOLD, 11));
        alarmLabel.setStyle("-fx-text-fill: #10b981; -fx-background-color: rgba(16, 185, 129, 0.2); " +
                           "-fx-background-radius: 10; -fx-padding: 4 10; -fx-border-color: #10b981; " +
                           "-fx-border-width: 1.5; -fx-border-radius: 10;");
        
        // Bind alarm label to security state
        securityArmed.addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                if (newVal) {
                    alarmLabel.setText("ARMED");
                    alarmLabel.setStyle("-fx-text-fill: #ef4444; -fx-background-color: rgba(239, 68, 68, 0.2); " +
                                       "-fx-background-radius: 10; -fx-padding: 4 10; -fx-font-weight: bold; " +
                                       "-fx-border-color: #ef4444; -fx-border-width: 1.5; -fx-border-radius: 10;");
                } else {
                    alarmLabel.setText("READY");
                    alarmLabel.setStyle("-fx-text-fill: #10b981; -fx-background-color: rgba(16, 185, 129, 0.2); " +
                                       "-fx-background-radius: 10; -fx-padding: 4 10; " +
                                       "-fx-border-color: #10b981; -fx-border-width: 1.5; -fx-border-radius: 10;");
                }
            });
        });
        
        VBox alarmBox = new VBox(3);
        alarmBox.setAlignment(Pos.CENTER);
        alarmBox.getChildren().addAll(alarmIndicator, alarmLabel);
        
        // Scene Indicator with IoT styling
        sceneIndicator = new Label("Normal Mode");
        sceneIndicator.setFont(Font.font("System", FontWeight.BOLD, 13));
        sceneIndicator.setPadding(new Insets(8, 15, 8, 15));
        sceneIndicator.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95); -fx-text-fill: #1e3a8a; " +
                               "-fx-background-radius: 12; -fx-border-color: #60a5fa; " +
                               "-fx-border-radius: 12; -fx-border-width: 2;");
        
        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Quick Stats
        HBox stats = createQuickStats();
        
        // Time Display with IoT styling
        Label timeLabel = new Label();
        timeLabel.textProperty().bind(currentTime);
        timeLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        timeLabel.setStyle("-fx-text-fill: white; -fx-background-color: rgba(255, 255, 255, 0.2); " +
                          "-fx-background-radius: 12; -fx-padding: 8 15; " +
                          "-fx-border-color: rgba(255, 255, 255, 0.3); -fx-border-width: 1; -fx-border-radius: 12;");
        
        header.getChildren().addAll(titleBox, alarmBox, sceneIndicator, spacer, stats, timeLabel);
        
        return header;
    }
    
    private HBox createQuickStats() {
        HBox stats = new HBox(15);
        stats.setAlignment(Pos.CENTER);
        
        // Create dynamic device count string binding
        javafx.beans.binding.StringBinding deviceCountBinding = javafx.beans.binding.Bindings.createStringBinding(
            () -> activeDevices.get() + "/" + totalDevices.get(),
            activeDevices, totalDevices
        );
        
        stats.getChildren().addAll(
            createStatBadge("⚡", totalPower.asString("%.0f W")),
            createStatBadge("🔌", deviceCountBinding),
            createStatBadge("💰", hourlyCost.asString("$%.2f/h"))
        );
        
        return stats;
    }
    
    private VBox createStatBadge(String icon, javafx.beans.binding.StringBinding value) {
        VBox badge = new VBox(3);
        badge.setAlignment(Pos.CENTER);
        badge.setPadding(new Insets(8, 15, 8, 15));
        badge.setStyle("-fx-background-color: rgba(255, 255, 255, 0.25); -fx-background-radius: 10; " +
                      "-fx-border-color: rgba(255, 255, 255, 0.4); -fx-border-radius: 10; -fx-border-width: 1;");
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(16));
        
        Label valueLabel = new Label();
        valueLabel.textProperty().bind(value);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        valueLabel.setStyle("-fx-text-fill: " + TEXT_PRIMARY + ";");
        
        badge.getChildren().addAll(iconLabel, valueLabel);
        
        return badge;
    }
    
    private VBox createMainTabs() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        // Set background and hide the default tab header area via tab CSS properties
        tabPane.setStyle("-fx-background-color: " + BACKGROUND + "; " +
                 "-fx-tab-min-height: 0; -fx-tab-max-height: 0; " +
                 "-fx-tab-min-width: 0; -fx-tab-max-width: 0;");
        
        // Dashboard Tab
        Tab dashTab = new Tab();
        dashTab.setText("Dashboard"); // Keep for accessibility
        dashboardTab = createDashboardContent();
        ScrollPane dashScroll = new ScrollPane(dashboardTab);
        dashScroll.setFitToWidth(true);
        dashScroll.setStyle("-fx-background: " + BACKGROUND + "; -fx-background-color: " + BACKGROUND + ";");
        dashTab.setContent(dashScroll);
        
        // Devices Tab
        Tab devTab = new Tab();
        devTab.setText("Devices");
        devicesTab = createDevicesContent();
        ScrollPane devScroll = new ScrollPane(devicesTab);
        devScroll.setFitToWidth(true);
        devScroll.setStyle("-fx-background: " + BACKGROUND + "; -fx-background-color: " + BACKGROUND + ";");
        devTab.setContent(devScroll);
        
        // Rooms Tab
        Tab roomTab = new Tab();
        roomTab.setText("Rooms");
        roomsTab = createRoomsContent();
        ScrollPane roomScroll = new ScrollPane(roomsTab);
        roomScroll.setFitToWidth(true);
        roomScroll.setStyle("-fx-background: " + BACKGROUND + "; -fx-background-color: " + BACKGROUND + ";");
        roomTab.setContent(roomScroll);
        
        // Automation Tab
        Tab autoTab = new Tab();
        autoTab.setText("Automation");
        automationTab = createAutomationContent();
        ScrollPane autoScroll = new ScrollPane(automationTab);
        autoScroll.setFitToWidth(true);
        autoScroll.setStyle("-fx-background: " + BACKGROUND + "; -fx-background-color: " + BACKGROUND + ";");
        autoTab.setContent(autoScroll);
        
        tabPane.getTabs().addAll(dashTab, devTab, roomTab, autoTab);
        
        // Create futuristic navigation bar
        HBox navBar = createFuturisticNavBar(tabPane);
        
        // Embed custom navbar with TabPane
        VBox container = new VBox(0);
        container.getChildren().addAll(navBar, tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        
        return container;
    }
    
    private HBox createFuturisticNavBar(TabPane tabPane) {
        HBox navBar = new HBox(0);
        navBar.setPadding(new Insets(15, 20, 15, 20));
        navBar.setAlignment(Pos.CENTER);
        navBar.setStyle("-fx-background-color: linear-gradient(to right, #1e293b, #334155); " +
                       "-fx-border-color: " + PRIMARY + "; -fx-border-width: 0 0 2 0; " +
                       "-fx-effect: dropshadow(gaussian, rgba(37, 99, 235, 0.3), 10, 0, 0, 2);");
        
        // Create futuristic nav buttons
        Button dashBtn = createFuturisticNavButton("🏠", "Dashboard", 0, tabPane);
        Button devBtn = createFuturisticNavButton("🔌", "Devices", 1, tabPane);
        Button roomBtn = createFuturisticNavButton("🚪", "Rooms", 2, tabPane);
        Button autoBtn = createFuturisticNavButton("⚙️", "Automation", 3, tabPane);
        
        navBar.getChildren().addAll(dashBtn, devBtn, roomBtn, autoBtn);
        
        // Set first button as active
        Platform.runLater(() -> setActiveNavButton(dashBtn));
        
        return navBar;
    }
    
    private Button createFuturisticNavButton(String icon, String text, int tabIndex, TabPane tabPane) {
        VBox content = new VBox(5);
        content.setAlignment(Pos.CENTER);
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(24));
        iconLabel.setStyle("-fx-text-fill: white;");
        
        Label textLabel = new Label(text);
        textLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        textLabel.setStyle("-fx-text-fill: white;");
        
        content.getChildren().addAll(iconLabel, textLabel);
        
        Button btn = new Button();
        btn.setGraphic(content);
        btn.setPrefWidth(150);
        btn.setPrefHeight(70);
        btn.setStyle("-fx-background-color: transparent; " +
                    "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                    "-fx-border-width: 1; " +
                    "-fx-border-radius: 12; " +
                    "-fx-background-radius: 12; " +
                    "-fx-cursor: hand; " +
                    "-fx-padding: 10;");
        
        // Hover effect
        btn.setOnMouseEntered(e -> {
            if (!btn.getStyle().contains("background-color: " + PRIMARY)) {
                btn.setStyle("-fx-background-color: rgba(37, 99, 235, 0.2); " +
                            "-fx-border-color: " + PRIMARY + "; " +
                            "-fx-border-width: 2; " +
                            "-fx-border-radius: 12; " +
                            "-fx-background-radius: 12; " +
                            "-fx-cursor: hand; " +
                            "-fx-padding: 10; " +
                            "-fx-scale-x: 1.05; " +
                            "-fx-scale-y: 1.05;");
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), btn);
                scale.setToX(1.05);
                scale.setToY(1.05);
                scale.play();
            }
        });
        
        btn.setOnMouseExited(e -> {
            if (!btn.getStyle().contains("background-color: " + PRIMARY)) {
                btn.setStyle("-fx-background-color: transparent; " +
                            "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                            "-fx-border-width: 1; " +
                            "-fx-border-radius: 12; " +
                            "-fx-background-radius: 12; " +
                            "-fx-cursor: hand; " +
                            "-fx-padding: 10;");
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), btn);
                scale.setToX(1.0);
                scale.setToY(1.0);
                scale.play();
            }
        });
        
        // Click action
        btn.setOnAction(e -> {
            tabPane.getSelectionModel().select(tabIndex);
            setActiveNavButton(btn);
            
            // Pulse animation on click
            ScaleTransition pulse = new ScaleTransition(Duration.millis(100), btn);
            pulse.setFromX(1.0);
            pulse.setFromY(1.0);
            pulse.setToX(0.95);
            pulse.setToY(0.95);
            pulse.setCycleCount(2);
            pulse.setAutoReverse(true);
            pulse.play();
        });
        
        return btn;
    }
    
    private void setActiveNavButton(Button activeBtn) {
        // Reset all buttons in parent
        if (activeBtn.getParent() != null && activeBtn.getParent() instanceof HBox) {
            HBox parent = (HBox) activeBtn.getParent();
            for (var node : parent.getChildren()) {
                if (node instanceof Button) {
                    Button btn = (Button) node;
                    if (btn != activeBtn) {
                        btn.setStyle("-fx-background-color: transparent; " +
                                    "-fx-border-color: rgba(255, 255, 255, 0.2); " +
                                    "-fx-border-width: 1; " +
                                    "-fx-border-radius: 12; " +
                                    "-fx-background-radius: 12; " +
                                    "-fx-cursor: hand; " +
                                    "-fx-padding: 10;");
                    }
                }
            }
        }
        
        // Set active button style with glow animation
        activeBtn.setStyle("-fx-background-color: " + PRIMARY + "; " +
                          "-fx-border-color: white; " +
                          "-fx-border-width: 2; " +
                          "-fx-border-radius: 12; " +
                          "-fx-background-radius: 12; " +
                          "-fx-cursor: hand; " +
                          "-fx-padding: 10; " +
                          "-fx-effect: dropshadow(gaussian, rgba(37, 99, 235, 0.8), 20, 0, 0, 0);");
        
        // Glow animation
        FadeTransition glow = new FadeTransition(Duration.millis(300), activeBtn);
        glow.setFromValue(0.7);
        glow.setToValue(1.0);
        glow.play();
    }
    
    private VBox createDashboardContent() {
        VBox content = new VBox(25);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #f8fafc;");
        
        // Fun Welcome Message with Dynamic Jokes
        VBox welcomeBox = new VBox(8);
        welcomeBox.setPadding(new Insets(20));
        welcomeBox.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #ffffff, #f0f9ff); " +
            "-fx-background-radius: 15; " +
            "-fx-border-color: linear-gradient(to right, #3b82f6, #8b5cf6); " +
            "-fx-border-radius: 15; -fx-border-width: 2; " +
            "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.3), 15, 0, 0, 5);"
        );
        
        Label welcomeTitle = new Label("🏠 Welcome to Your Smart Home! 🎉");
        welcomeTitle.setFont(Font.font("System", FontWeight.BOLD, 24));
        welcomeTitle.setStyle("-fx-text-fill: #3b82f6;");
        
        String[] jokes = {
            "💡 Fun Fact: Your smart lights use 75% less energy than saying 'Let there be light!'",
            "🤖 Did you know? Your home is so smart, it probably knows what you want for dinner before you do!",
            "⚡ Pro Tip: The only thing smarter than your home is... well, we're still working on that!",
            "🎭 Remember: With great power consumption comes great electricity bills!",
            "🌟 Your home automation is so advanced, even the toaster is judging your breakfast choices!",
            "🚀 Breaking: Scientists confirm your smart home has better WiFi than most offices!",
            "🎯 Your automation rules are so good, they should run for political office!",
            "🔥 Hot take: Your thermostat knows your comfort zone better than you do!",
            "🎪 Welcome to the future, where even your doorbell has trust issues!",
            "🌈 Life hack: Turn everything off and on again... remotely!"
        };
        
        Label jokeLabel = new Label(jokes[0]);
        jokeLabel.setFont(Font.font("System", 14));
        jokeLabel.setStyle("-fx-text-fill: #64748b;");
        jokeLabel.setWrapText(true);
        
        // Rotate jokes every 8 seconds with fade animation
        final int[] jokeIndex = {0};
        Timeline jokeTimeline = new Timeline(new KeyFrame(Duration.seconds(8), e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), jokeLabel);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(ev -> {
                jokeIndex[0] = (jokeIndex[0] + 1) % jokes.length;
                jokeLabel.setText(jokes[jokeIndex[0]]);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), jokeLabel);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });
            fadeOut.play();
        }));
        jokeTimeline.setCycleCount(Timeline.INDEFINITE);
        jokeTimeline.play();
        
        welcomeBox.getChildren().addAll(welcomeTitle, jokeLabel);
        content.getChildren().add(welcomeBox);
        
        // Rooms Summary Cards Section
        Label roomsTitle = new Label("🏘️ Rooms Summary");
        roomsTitle.setFont(Font.font("System", FontWeight.BOLD, 22));
        roomsTitle.setStyle("-fx-text-fill: #1e293b; -fx-padding: 10 0 0 0;");
        content.getChildren().add(roomsTitle);
        
        // Rooms Summary Grid - Centered
        roomSummaryGrid = new FlowPane();
        roomSummaryGrid.setHgap(15);
        roomSummaryGrid.setVgap(15);
        roomSummaryGrid.setAlignment(Pos.CENTER);
        
        int cardIndex = 0;
        for (Room room : home.getRooms()) {
            VBox card = createRoomSummaryCard(room);
            // Add entrance animation
            card.setOpacity(0);
            card.setTranslateY(20);
            
            FadeTransition fadeIn = new FadeTransition(Duration.millis(400), card);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setDelay(Duration.millis(cardIndex * 100));
            
            TranslateTransition slideUp = new TranslateTransition(Duration.millis(400), card);
            slideUp.setFromY(20);
            slideUp.setToY(0);
            slideUp.setDelay(Duration.millis(cardIndex * 100));
            
            fadeIn.play();
            slideUp.play();
            
            roomSummaryGrid.getChildren().add(card);
            cardIndex++;
        }
        
        content.getChildren().add(roomSummaryGrid);
        
        // Row 2: Control Panel (Scenes + Quick Controls)
        HBox controlRow = new HBox(20);
        controlRow.setPrefHeight(200);
        controlRow.getChildren().addAll(
            createScenesPanel(),
            createQuickControlsPanel()
        );
        content.getChildren().add(controlRow);
        
        // Row 3: Charts Section (2 charts side by side)
        HBox chartsRow = new HBox(20);
        chartsRow.setPrefHeight(350);
        VBox powerChartBox = createPowerChart();
        VBox energyChartBox = createEnergyPieChart();
        HBox.setHgrow(powerChartBox, Priority.ALWAYS);
        HBox.setHgrow(energyChartBox, Priority.ALWAYS);
        chartsRow.getChildren().addAll(powerChartBox, energyChartBox);
        content.getChildren().add(chartsRow);
        
        return content;
    }
    
    private VBox createScenesPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(25));
        panel.setPrefWidth(450);
        HBox.setHgrow(panel, Priority.SOMETIMES);
        panel.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #ffffff, #f0f9ff); " +
            "-fx-background-radius: 12; " +
            "-fx-border-color: linear-gradient(to right, #3b82f6, #8b5cf6); " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.3), 12, 0, 0, 3);"
        );
        
        Label title = new Label("Scene Control");
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        title.setStyle("-fx-text-fill: #1e293b;");
        
        GridPane scenesGrid = new GridPane();
        scenesGrid.setHgap(10);
        scenesGrid.setVgap(10);
        
        Button morningBtn = createSceneButtonCompact("🌅 Morning", "morning", "#10b981");
        Button movieBtn = createSceneButtonCompact("🎬 Movie", "movie", "#3b82f6");
        Button nightBtn = createSceneButtonCompact("🌙 Night", "night", "#6366f1");
        Button awayBtn = createSceneButtonCompact("🚗 Away", "away", "#f59e0b");
        
        scenesGrid.add(morningBtn, 0, 0);
        scenesGrid.add(movieBtn, 1, 0);
        scenesGrid.add(nightBtn, 0, 1);
        scenesGrid.add(awayBtn, 1, 1);
        
        panel.getChildren().addAll(title, scenesGrid);
        return panel;
    }
    
    private Button createSceneButtonCompact(String text, String sceneName, String color) {
        Button btn = new Button(text);
        btn.setPrefWidth(190);
        btn.setPrefHeight(60);
        btn.setStyle(
            "-fx-background-color: " + color + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;"
        );
        
        btn.setOnMouseEntered(e -> {
            btn.setStyle(
                "-fx-background-color: derive(" + color + ", -10%); " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;"
            );
        });
        
        btn.setOnMouseExited(e -> {
            btn.setStyle(
                "-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;"
            );
        });
        
        btn.setOnAction(e -> {
            sceneManager.apply(sceneName);
            refreshAllData();
            showEnhancedAlert("Scene Applied", "Scene '" + text + "' has been activated!", Alert.AlertType.INFORMATION);
        });
        
        return btn;
    }
    
    private VBox createQuickControlsPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(25));
        HBox.setHgrow(panel, Priority.ALWAYS);
        panel.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #ffffff, #f0f9ff); " +
            "-fx-background-radius: 12; " +
            "-fx-border-color: linear-gradient(to right, #3b82f6, #8b5cf6); " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.3), 12, 0, 0, 3);"
        );
        
        Label title = new Label("Quick Controls");
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        title.setStyle("-fx-text-fill: #1e293b;");
        
        VBox controls = new VBox(10);
        controls.getChildren().addAll(
            createToggleControlRow("💡 All Lights", true),
            createToggleControlRow("🔌 All Devices", false),
            createSecurityControlRow()
        );
        
        panel.getChildren().addAll(title, controls);
        return panel;
    }
    
    private HBox createToggleControlRow(String label, boolean isLights) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10));
        row.setStyle(
            "-fx-background-color: #f8fafc; " +
            "-fx-background-radius: 8;"
        );
        
        Label labelText = new Label(label);
        labelText.setFont(Font.font("System", FontWeight.NORMAL, 14));
        labelText.setStyle("-fx-text-fill: #1e293b;");
        HBox.setHgrow(labelText, Priority.ALWAYS);
        
        Button toggleBtn = new Button("Turn ON");
        toggleBtn.setPrefWidth(120);
        toggleBtn.setPrefHeight(35);
        toggleBtn.setStyle(
            "-fx-background-color: #10b981; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 12px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 6; " +
            "-fx-cursor: hand;"
        );
        
        final boolean[] isOn = {false};
        
        toggleBtn.setOnAction(e -> {
            isOn[0] = !isOn[0];
            
            if (isLights) {
                if (isOn[0]) {
                    // Turn ON all lights
                    for (Room room : home.getRooms()) {
                        for (SmartDevice device : room.getDevices()) {
                            if (device instanceof com.smarthome.model.devices.SmartLight) {
                                device.turnOn();
                            }
                        }
                    }
                    home.turnOnAllLights();
                    toggleBtn.setText("Turn OFF");
                    toggleBtn.setStyle(
                        "-fx-background-color: #ef4444; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 6; " +
                        "-fx-cursor: hand;"
                    );
                } else {
                    // Turn OFF all lights
                    for (Room room : home.getRooms()) {
                        for (SmartDevice device : room.getDevices()) {
                            if (device instanceof com.smarthome.model.devices.SmartLight) {
                                device.turnOff();
                            }
                        }
                    }
                    toggleBtn.setText("Turn ON");
                    toggleBtn.setStyle(
                        "-fx-background-color: #10b981; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 6; " +
                        "-fx-cursor: hand;"
                    );
                }
            } else {
                if (isOn[0]) {
                    // Turn ON all devices
                    for (SmartDevice device : home.getAllDevices()) {
                        if (!device.isOn()) {
                            device.turnOn();
                        }
                    }
                    toggleBtn.setText("Turn OFF");
                    toggleBtn.setStyle(
                        "-fx-background-color: #ef4444; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 6; " +
                        "-fx-cursor: hand;"
                    );
                } else {
                    // Turn OFF all devices
                    home.turnOffAllDevices();
                    toggleBtn.setText("Turn ON");
                    toggleBtn.setStyle(
                        "-fx-background-color: #10b981; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 12px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 6; " +
                        "-fx-cursor: hand;"
                    );
                }
            }
            
            refreshAllData();
        });
        
        row.getChildren().addAll(labelText, toggleBtn);
        return row;
    }
    
    private HBox createSecurityControlRow() {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10));
        row.setStyle(
            "-fx-background-color: #f8fafc; " +
            "-fx-background-radius: 8;"
        );
        
        Label labelText = new Label("🛡️ Security System");
        labelText.setFont(Font.font("System", FontWeight.NORMAL, 14));
        labelText.setStyle("-fx-text-fill: #1e293b;");
        HBox.setHgrow(labelText, Priority.ALWAYS);
        
        Button toggleBtn = new Button("Arm");
        toggleBtn.setPrefWidth(120);
        toggleBtn.setPrefHeight(35);
        toggleBtn.setStyle(
            "-fx-background-color: #f59e0b; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 12px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 6; " +
            "-fx-cursor: hand;"
        );
        
        final boolean[] armed = {false};
        
        toggleBtn.setOnAction(e -> {
            armed[0] = !armed[0];
            
            if (armed[0]) {
                securityService.arm();
                securityArmed.set(true);
                startAlarmAnimation(DANGER);
                toggleBtn.setText("Disarm");
                toggleBtn.setStyle(
                    "-fx-background-color: #10b981; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 12px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-radius: 6; " +
                    "-fx-cursor: hand;"
                );
            } else {
                securityService.disarm();
                securityArmed.set(false);
                stopAlarmAnimation();
                toggleBtn.setText("Arm");
                toggleBtn.setStyle(
                    "-fx-background-color: #f59e0b; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 12px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-background-radius: 6; " +
                    "-fx-cursor: hand;"
                );
            }
            
            refreshAllData();
        });
        
        row.getChildren().addAll(labelText, toggleBtn);
        return row;
    }
    
    private VBox createStatCard(String title, javafx.beans.binding.StringExpression valueBinding, String icon, String accentColor) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setPrefHeight(120);
        card.setMaxHeight(120);
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #ffffff, #f0f9ff); " +
            "-fx-background-radius: 12; " +
            "-fx-border-color: linear-gradient(to right, #3b82f6, #8b5cf6); " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.3), 12, 0, 0, 3);"
        );
        
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(24));
        iconLabel.setStyle("-fx-text-fill: " + accentColor + ";");
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.NORMAL, 13));
        titleLabel.setStyle("-fx-text-fill: #64748b;");
        
        header.getChildren().addAll(iconLabel, titleLabel);
        
        Label valueLabel = new Label();
        valueLabel.textProperty().bind(valueBinding);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        valueLabel.setStyle("-fx-text-fill: #1e293b;");
        
        card.getChildren().addAll(header, valueLabel);
        return card;
    }
    
    private VBox createSecurityCard() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(25));
        card.setPrefHeight(120);
        card.setMaxHeight(120);
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #ffffff, #f0f9ff); " +
            "-fx-background-radius: 12; " +
            "-fx-border-color: linear-gradient(to right, #3b82f6, #8b5cf6); " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.3), 12, 0, 0, 3);"
        );
        
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label("🛡️");
        iconLabel.setFont(Font.font(24));
        
        Label titleLabel = new Label("Security");
        titleLabel.setFont(Font.font("System", FontWeight.NORMAL, 13));
        titleLabel.setStyle("-fx-text-fill: #64748b;");
        
        header.getChildren().addAll(iconLabel, titleLabel);
        
        Label valueLabel = new Label();
        securityArmed.addListener((obs, old, newVal) -> {
            if (newVal) {
                valueLabel.setText("ARMED");
                valueLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-font-size: 28px;");
            } else {
                valueLabel.setText("DISARMED");
                valueLabel.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold; -fx-font-size: 28px;");
            }
        });
        valueLabel.setText("DISARMED");
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        valueLabel.setStyle("-fx-text-fill: #10b981;");
        
        card.getChildren().addAll(header, valueLabel);
        return card;
    }
    
    private VBox createQuickActionsPanel_OLD() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: linear-gradient(to bottom right, #ffffff, #f0f9ff); " +
                      "-fx-background-radius: 15; " +
                      "-fx-border-color: linear-gradient(to right, #3b82f6, #8b5cf6); " +
                      "-fx-border-radius: 15; -fx-border-width: 2; " +
                      "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.3), 15, 0, 0, 5);");
        
        // Store reference for theme updates
        quickActionsPanel = panel;
        
        Label title = new Label("Quick Actions");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        title.setStyle("-fx-text-fill: " + TEXT_PRIMARY + ";");
        
        // Scene Buttons
        HBox sceneButtons = new HBox(10);
        sceneButtons.getChildren().addAll(
            createSceneButton("🌅 Morning", "morning", SUCCESS, 
                "☀️ Wake Up Mode\n• Lights: 80% brightness\n• Blinds: Fully open\n• Thermostat: 22°C\n• Perfect start to your day!"),
            createSceneButton("🎬 Movie", "movie", PRIMARY,
                "🎥 Cinema Mode\n• Lights: 30% dimmed\n• Blinds: Fully closed\n• Theater atmosphere\n• Enjoy the show!"),
            createSceneButton("🌙 Night", "night", "#6366f1",
                "😴 Sleep Mode\n• All devices: OFF\n• Security: ARMED\n• Doors: LOCKED\n• Sweet dreams!"),
            createSceneButton("🚗 Away", "away", WARNING,
                "🏃 Leaving Home\n• Energy saving mode\n• Security: Active\n• Minimal power usage\n• Safe travels!")
        );
        
        // Control Buttons with state tracking
        HBox controlButtons = new HBox(10);
        
        allLightsOnBtn = createActionButton("💡 All Lights ON", () -> {
            int lightCount = 0;
            for (Room room : home.getRooms()) {
                for (SmartDevice device : room.getDevices()) {
                    if (device instanceof com.smarthome.model.devices.SmartLight) {
                        device.turnOn();
                        lightCount++;
                    }
                }
            }
            home.turnOnAllLights();
            showEnhancedAlert("Success", 
                "✅ All Lights Turned ON\n\n" +
                "Lights activated: " + lightCount + "\n" +
                "Total power: " + String.format("%.0fW", home.getTotalPowerConsumption()),
                Alert.AlertType.INFORMATION);
            refreshAllData();
        }, SUCCESS);
        
        allLightsOffBtn = createActionButton("💡 All Lights OFF", () -> {
            int lightCount = 0;
            for (Room room : home.getRooms()) {
                for (SmartDevice device : room.getDevices()) {
                    if (device instanceof com.smarthome.model.devices.SmartLight) {
                        device.turnOff();
                        lightCount++;
                    }
                }
            }
            showEnhancedAlert("Success",
                "🌑 All Lights Turned OFF\n\n" +
                "Lights deactivated: " + lightCount + "\n" +
                "Power saved: " + String.format("%.0fW", 60.0 * lightCount),
                Alert.AlertType.INFORMATION);
            refreshAllData();
        }, TEXT_SECONDARY);
        
        allDevicesOnBtn = createActionButton("🔌 All Devices ON", () -> {
            int deviceCount = 0;
            for (SmartDevice device : home.getAllDevices()) {
                if (!device.isOn()) {
                    device.turnOn();
                    deviceCount++;
                }
            }
            showEnhancedAlert("Success",
                "⚡ All Devices Turned ON\n\n" +
                "Devices activated: " + deviceCount + "\n" +
                "Total power: " + String.format("%.0fW", home.getTotalPowerConsumption()),
                Alert.AlertType.INFORMATION);
            refreshAllData();
        }, SUCCESS);
        
        allDevicesOffBtn = createActionButton("🔌 All Devices OFF", () -> {
            double powerBefore = home.getTotalPowerConsumption();
            int deviceCount = home.getAllDevices().size();
            home.turnOffAllDevices();
            double powerSaved = powerBefore;
            showEnhancedAlert("Success",
                "⚡ All Devices Turned OFF\n\n" +
                "Devices deactivated: " + deviceCount + "\n" +
                "Power saved: " + String.format("%.0fW", powerSaved) + "\n" +
                "Current power: 0W",
                Alert.AlertType.INFORMATION);
            refreshAllData();
            stopAlarmAnimation();
        }, DANGER);
        
        armSecurityBtn = createActionButton("🛡️ Arm Security", () -> {
            securityService.arm();
            int cameraCount = 0;
            for (SmartDevice device : home.getAllDevices()) {
                if (device instanceof com.smarthome.model.devices.SecurityCamera) {
                    cameraCount++;
                }
            }
            showEnhancedAlert("Security Armed",
                "🛡️ Security System ARMED\n\n" +
                "✓ " + cameraCount + " cameras monitoring\n" +
                "✓ Motion detection enabled\n" +
                "✓ Doors locked\n" +
                "✓ Intrusion alerts active",
                Alert.AlertType.WARNING);
            securityArmed.set(true);
            startAlarmAnimation(DANGER);
            refreshAllData();
        }, WARNING);
        
        disarmSecurityBtn = createActionButton("🔓 Disarm Security", () -> {
            securityService.disarm();
            showEnhancedAlert("Security Disarmed",
                "🔓 Security System DISARMED\n\n" +
                "✓ Cameras on standby\n" +
                "✓ Motion detection disabled\n" +
                "✓ Normal operation mode\n" +
                "✓ Intrusion alerts paused",
                Alert.AlertType.INFORMATION);
            securityArmed.set(false);
            stopAlarmAnimation();
            refreshAllData();
        }, PRIMARY);
        
        controlButtons.getChildren().addAll(
            allLightsOnBtn, allLightsOffBtn, allDevicesOnBtn, allDevicesOffBtn, armSecurityBtn, disarmSecurityBtn
        );
        
        panel.getChildren().addAll(title, new Label("Scenes:"), sceneButtons, 
                                    new Label("Controls:"), controlButtons);
        
        return panel;
    }
    
    private Button createSceneButton(String text, String sceneName, String color, String description) {
        Button btn = new Button(text);
        btn.setPrefWidth(160);
        btn.setPrefHeight(50);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                    "-fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; " +
                    "-fx-cursor: hand;");
        
        // Tooltip with scene description
        Tooltip tooltip = new Tooltip(description);
        tooltip.setStyle("-fx-font-size: 12px; -fx-background-color: #1e293b; -fx-text-fill: white; " +
                        "-fx-background-radius: 6; -fx-padding: 10;");
        tooltip.setShowDelay(Duration.millis(300));
        btn.setTooltip(tooltip);
        
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: derive(" + color + ", -10%); " +
                    "-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; " +
                    "-fx-background-radius: 8; -fx-cursor: hand; -fx-scale-x: 1.05; -fx-scale-y: 1.05;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + color + "; " +
                    "-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; " +
                    "-fx-background-radius: 8; -fx-cursor: hand;"));
        
        btn.setOnAction(e -> {
            // Apply the scene
            sceneManager.apply(sceneName);
            
            // Update theme and indicator based on scene
            updateSceneTheme(sceneName, text, color);
            
            // Show detailed success message
            String message = getSceneSuccessMessage(sceneName);
            showAlert("Scene Activated: " + text, message, Alert.AlertType.INFORMATION);
            
            // Refresh all data
            refreshAllData();
            
            // Animate button press
            animateButtonPress(btn);
        });
        
        return btn;
    }
    
    private void updateSceneTheme(String sceneName, String sceneName2, String color) {
        // Update scene indicator with modern high-contrast colors
        String displayName = sceneName2.replaceAll("[^a-zA-Z\\s]", "").trim(); // Remove emoji
        
        // Apply scene-specific styling with proper contrast
        switch (sceneName.toLowerCase()) {
            case "movie":
                sceneIndicator.setText("Movie Mode");
                sceneIndicator.setStyle("-fx-background-color: #4c1d95; -fx-text-fill: #fdf4ff; " +
                                       "-fx-background-radius: 12; -fx-border-color: #a78bfa; " +
                                       "-fx-border-radius: 12; -fx-border-width: 2; -fx-padding: 8 15; " +
                                       "-fx-effect: dropshadow(gaussian, rgba(124, 58, 237, 0.4), 8, 0, 0, 0);");
                if (quickActionsPanel != null) {
                    quickActionsPanel.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; " +
                                              "-fx-border-color: #a78bfa; -fx-border-radius: 12; -fx-border-width: 2; -fx-padding: 20;");
                }
                break;
            case "night":
                sceneIndicator.setText("Night Mode");
                sceneIndicator.setStyle("-fx-background-color: #0f172a; -fx-text-fill: #67e8f9; " +
                                       "-fx-background-radius: 12; -fx-border-color: #06b6d4; " +
                                       "-fx-border-radius: 12; -fx-border-width: 2; -fx-padding: 8 15; " +
                                       "-fx-effect: dropshadow(gaussian, rgba(6, 182, 212, 0.4), 8, 0, 0, 0);");
                if (quickActionsPanel != null) {
                    quickActionsPanel.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; " +
                                              "-fx-border-color: #22d3ee; -fx-border-radius: 12; -fx-border-width: 2; -fx-padding: 20;");
                }
                break;
            case "party":
                sceneIndicator.setText("Party Mode");
                sceneIndicator.setStyle("-fx-background-color: #db2777; -fx-text-fill: #fdf2f8; " +
                                       "-fx-background-radius: 12; -fx-border-color: #f9a8d4; " +
                                       "-fx-border-radius: 12; -fx-border-width: 2; -fx-padding: 8 15; " +
                                       "-fx-effect: dropshadow(gaussian, rgba(236, 72, 153, 0.4), 8, 0, 0, 0);");
                if (quickActionsPanel != null) {
                    quickActionsPanel.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; " +
                                              "-fx-border-color: #f9a8d4; -fx-border-radius: 12; -fx-border-width: 2; -fx-padding: 20;");
                }
                break;
            case "energy saving":
            case "away":
                sceneIndicator.setText(displayName);
                sceneIndicator.setStyle("-fx-background-color: #047857; -fx-text-fill: #d1fae5; " +
                                       "-fx-background-radius: 12; -fx-border-color: #6ee7b7; " +
                                       "-fx-border-radius: 12; -fx-border-width: 2; -fx-padding: 8 15; " +
                                       "-fx-effect: dropshadow(gaussian, rgba(16, 185, 129, 0.4), 8, 0, 0, 0);");
                if (quickActionsPanel != null) {
                    quickActionsPanel.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; " +
                                              "-fx-border-color: #6ee7b7; -fx-border-radius: 12; -fx-border-width: 2; -fx-padding: 20;");
                }
                break;
            default:
                sceneIndicator.setText("Normal Mode");
                sceneIndicator.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95); -fx-text-fill: #1e3a8a; " +
                                       "-fx-background-radius: 12; -fx-border-color: #60a5fa; " +
                                       "-fx-border-radius: 12; -fx-border-width: 2; -fx-padding: 8 15;");
                if (quickActionsPanel != null) {
                    quickActionsPanel.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; " +
                                              "-fx-border-color: #e2e8f0; -fx-border-radius: 12; -fx-border-width: 1; -fx-padding: 20;");
                }
        }
        
        // Animate the theme change
        FadeTransition fade = new FadeTransition(Duration.millis(300), sceneIndicator);
        fade.setFromValue(0.3);
        fade.setToValue(1.0);
        fade.play();
    }
    
    private String getSceneSuccessMessage(String sceneName) {
        switch (sceneName.toLowerCase()) {
            case "morning":
                return "☀️ Good morning!\n\n" +
                       "✓ All lights turned ON at 80% brightness\n" +
                       "✓ Blinds opened to 100%\n" +
                       "✓ Thermostat set to comfortable 22°C\n" +
                       "✓ Perfect environment for a fresh start!\n\n" +
                       "Total Power: " + String.format("%.0fW", home.getTotalPowerConsumption());
            case "movie":
                return "🎬 Cinema mode activated!\n\n" +
                       "✓ Lights dimmed to 30% for viewing\n" +
                       "✓ Blinds closed completely\n" +
                       "✓ Ambient lighting optimized\n" +
                       "✓ Enjoy your entertainment!\n\n" +
                       "Total Power: " + String.format("%.0fW", home.getTotalPowerConsumption());
            case "night":
                return "🌙 Good night!\n\n" +
                       "✓ All devices turned OFF\n" +
                       "✓ Security system ARMED\n" +
                       "✓ All doors LOCKED\n" +
                       "✓ Cameras actively monitoring\n" +
                       "✓ Sleep safe and sound!\n\n" +
                       "Total Power: " + String.format("%.0fW", home.getTotalPowerConsumption());
            case "away":
                return "🚗 Away mode activated!\n\n" +
                       "✓ Energy-saving mode enabled\n" +
                       "✓ Security system on full alert\n" +
                       "✓ Unnecessary devices turned OFF\n" +
                       "✓ Home secured for your absence\n\n" +
                       "Total Power: " + String.format("%.0fW", home.getTotalPowerConsumption());
            default:
                return "Scene '" + sceneName + "' has been successfully applied!";
        }
    }
    
    private void animateButtonPress(Button btn) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(100), btn);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(0.95);
        scale.setToY(0.95);
        scale.setCycleCount(2);
        scale.setAutoReverse(true);
        scale.play();
    }
    
    private Button createActionButton(String text, Runnable action, String color) {
        Button btn = new Button(text);
        btn.setPrefWidth(160);
        btn.setPrefHeight(50);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                    "-fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 8; " +
                    "-fx-cursor: hand;");
        
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: derive(" + color + ", -10%); " +
                    "-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; " +
                    "-fx-background-radius: 8; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + color + "; " +
                    "-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; " +
                    "-fx-background-radius: 8; -fx-cursor: hand;"));
        
        btn.setOnAction(e -> action.run());
        
        return btn;
    }
    
    private FlowPane createRoomCardsGrid() {
        FlowPane grid = new FlowPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPrefWrapLength(1200);
        
        for (Room room : home.getRooms()) {
            grid.getChildren().add(createRoomCard(room));
        }
        
        return grid;
    }
    
    private VBox createRoomCard(Room room) {
        VBox card = new VBox(12);
        card.setPrefWidth(380);
        card.setPrefHeight(280);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 12; " +
                     "-fx-border-color: " + BORDER + "; -fx-border-radius: 12; -fx-border-width: 1; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        
        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label icon = new Label("🚪");
        icon.setFont(Font.font(20));
        
        VBox titleBox = new VBox(3);
        Label roomName = new Label(room.getName());
        roomName.setFont(Font.font("System", FontWeight.BOLD, 16));
        roomName.setStyle("-fx-text-fill: " + TEXT_PRIMARY + ";");
        
        Label floor = new Label(room.getFloor());
        floor.setStyle("-fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-size: 12px;");
        
        titleBox.getChildren().addAll(roomName, floor);
        header.getChildren().addAll(icon, titleBox);
        
        // Stats with live binding
        GridPane stats = new GridPane();
        stats.setHgap(15);
        stats.setVgap(8);
        
        Label devicesLabel = new Label("Devices: " + room.getDevices().size());
        Label activeLabel = new Label("Active: " + room.getActiveDeviceCount());
        Label powerLabel = new Label("Power: " + String.format("%.0fW", room.getTotalPowerConsumption()));
        Label tempLabel = new Label("Temp: " + String.format("%.1f°C", room.getCurrentTemperature()));
        
        devicesLabel.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-size: 12px;");
        activeLabel.setStyle("-fx-text-fill: " + SUCCESS + "; -fx-font-size: 12px; -fx-font-weight: bold;");
        powerLabel.setStyle("-fx-text-fill: " + WARNING + "; -fx-font-size: 12px; -fx-font-weight: bold;");
        tempLabel.setStyle("-fx-text-fill: " + PRIMARY + "; -fx-font-size: 12px;");
        
        stats.add(devicesLabel, 0, 0);
        stats.add(activeLabel, 1, 0);
        stats.add(powerLabel, 0, 1);
        stats.add(tempLabel, 1, 1);
        
        // Control Buttons with state feedback
        VBox controlsBox = new VBox(8);
        
        HBox lightsRow = new HBox(8);
        lightsRow.setAlignment(Pos.CENTER_LEFT);
        
        Button lightsOnBtn = createSmallButton("💡 Lights ON");
        lightsOnBtn.setStyle("-fx-background-color: " + SUCCESS + "; -fx-text-fill: white; " +
                    "-fx-font-size: 11px; -fx-background-radius: 6; -fx-cursor: hand;");
        lightsOnBtn.setOnAction(e -> {
            int count = 0;
            for (SmartDevice device : room.getDevices()) {
                if (device instanceof com.smarthome.model.devices.SmartLight) {
                    device.turnOn();
                    count++;
                }
            }
            room.turnOnAllLights();
            showEnhancedAlert("Lights ON",
                "💡 Lights activated in " + room.getName() + "\n\n" +
                "✓ " + count + " lights turned ON\n" +
                "Power: " + String.format("%.0fW", room.getTotalPowerConsumption()),
                Alert.AlertType.INFORMATION);
            refreshAllData();
        });
        
        Button lightsOffBtn = createSmallButton("💡 Lights OFF");
        lightsOffBtn.setStyle("-fx-background-color: " + TEXT_SECONDARY + "; -fx-text-fill: white; " +
                    "-fx-font-size: 11px; -fx-background-radius: 6; -fx-cursor: hand;");
        lightsOffBtn.setOnAction(e -> {
            int count = 0;
            for (SmartDevice device : room.getDevices()) {
                if (device instanceof com.smarthome.model.devices.SmartLight) {
                    device.turnOff();
                    count++;
                }
            }
            showEnhancedAlert("Lights OFF",
                "🌑 Lights deactivated in " + room.getName() + "\n\n" +
                "✓ " + count + " lights turned OFF\n" +
                "Power saved: " + String.format("%.0fW", count * 60.0),
                Alert.AlertType.INFORMATION);
            refreshAllData();
        });
        
        lightsRow.getChildren().addAll(lightsOnBtn, lightsOffBtn);
        
        HBox devicesRow = new HBox(8);
        devicesRow.setAlignment(Pos.CENTER_LEFT);
        
        Button allOnBtn = createSmallButton("🔌 All Devices ON");
        allOnBtn.setStyle("-fx-background-color: " + SUCCESS + "; -fx-text-fill: white; " +
                    "-fx-font-size: 11px; -fx-background-radius: 6; -fx-cursor: hand;");
        allOnBtn.setOnAction(e -> {
            int deviceCount = 0;
            for (SmartDevice device : room.getDevices()) {
                if (!device.isOn()) {
                    device.turnOn();
                    deviceCount++;
                }
            }
            showEnhancedAlert("All Devices ON",
                "⚡ All devices activated in " + room.getName() + "\n\n" +
                "✓ " + deviceCount + " devices turned ON\n" +
                "Power: " + String.format("%.0fW", room.getTotalPowerConsumption()),
                Alert.AlertType.INFORMATION);
            refreshAllData();
        });
        
        Button allOffBtn = createSmallButton("🔌 All Devices OFF");
        allOffBtn.setStyle("-fx-background-color: " + DANGER + "; -fx-text-fill: white; " +
                    "-fx-font-size: 11px; -fx-background-radius: 6; -fx-cursor: hand;");
        allOffBtn.setOnAction(e -> {
            double powerBefore = room.getTotalPowerConsumption();
            int deviceCount = room.getDevices().size();
            room.turnOffAllDevices();
            showEnhancedAlert("All Devices OFF",
                "🔌 All devices deactivated in " + room.getName() + "\n\n" +
                "✓ " + deviceCount + " devices turned OFF\n" +
                "Power saved: " + String.format("%.0fW", powerBefore),
                Alert.AlertType.INFORMATION);
            refreshAllData();
        });
        
        Button detailsBtn = createSmallButton("📊 Details");
        detailsBtn.setOnAction(e -> showRoomDetails(room));
        
        devicesRow.getChildren().addAll(allOnBtn, allOffBtn, detailsBtn);
        
        controlsBox.getChildren().addAll(lightsRow, devicesRow);
        
        card.getChildren().addAll(header, new Separator(), stats, controlsBox);
        
        return card;
    }
    
    private HBox createStatLabel(String label, String value) {
        HBox box = new HBox(5);
        box.setAlignment(Pos.CENTER_LEFT);
        
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-size: 12px;");
        
        Label val = new Label(value);
        val.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-size: 13px; -fx-font-weight: bold;");
        
        box.getChildren().addAll(lbl, val);
        return box;
    }
    
    private Button createSmallButton(String text) {
        Button btn = new Button(text);
        btn.setPrefHeight(32);
        btn.setStyle("-fx-background-color: white; -fx-text-fill: " + TEXT_PRIMARY + "; " +
                    "-fx-font-size: 11px; -fx-background-radius: 6; -fx-border-color: " + BORDER + "; " +
                    "-fx-border-radius: 6; -fx-border-width: 1; -fx-cursor: hand;");
        
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + CARD_BG + "; " +
                    "-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-size: 11px; -fx-background-radius: 6; " +
                    "-fx-border-color: " + PRIMARY + "; -fx-border-radius: 6; -fx-border-width: 1; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: white; -fx-text-fill: " + TEXT_PRIMARY + "; " +
                    "-fx-font-size: 11px; -fx-background-radius: 6; -fx-border-color: " + BORDER + "; " +
                    "-fx-border-radius: 6; -fx-border-width: 1; -fx-cursor: hand;"));
        
        return btn;
    }
    
    private VBox createPowerChart() {
        VBox box = new VBox(10);
        box.setPrefWidth(600);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: linear-gradient(to bottom right, #ffffff, #f0f9ff); " +
                    "-fx-background-radius: 15; " +
                    "-fx-border-color: linear-gradient(to right, #3b82f6, #8b5cf6); " +
                    "-fx-border-radius: 15; -fx-border-width: 2; " +
                    "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.3), 15, 0, 0, 5);");
        
        Label title = new Label("Power Consumption (Live)");
        title.setFont(Font.font("System", FontWeight.BOLD, 14));
        title.setStyle("-fx-text-fill: " + TEXT_PRIMARY + ";");
        
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Watts");
        yAxis.setAutoRanging(true);
        
        powerConsumptionChart = new LineChart<>(xAxis, yAxis);
        powerConsumptionChart.setLegendVisible(false);
        powerConsumptionChart.setPrefHeight(250);
        powerConsumptionChart.setCreateSymbols(true);
        powerConsumptionChart.setAnimated(true);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Power (W)");
        
        // Initialize with current power data from each room
        for (Room room : home.getRooms()) {
            series.getData().add(new XYChart.Data<>(room.getName(), room.getTotalPowerConsumption()));
        }
        
        powerConsumptionChart.getData().add(series);
        
        box.getChildren().addAll(title, powerConsumptionChart);
        
        return box;
    }
    
    private VBox createEnergyPieChart() {
        VBox box = new VBox(10);
        box.setPrefWidth(400);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: linear-gradient(to bottom right, #ffffff, #f0f9ff); " +
                    "-fx-background-radius: 15; " +
                    "-fx-border-color: linear-gradient(to right, #3b82f6, #8b5cf6); " +
                    "-fx-border-radius: 15; -fx-border-width: 2; " +
                    "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.3), 15, 0, 0, 5);");
        
        Label title = new Label("Energy Distribution by Room");
        title.setFont(Font.font("System", FontWeight.BOLD, 14));
        title.setStyle("-fx-text-fill: " + TEXT_PRIMARY + ";");
        
        energyByRoomChart = new PieChart();
        energyByRoomChart.setPrefHeight(250);
        energyByRoomChart.setLegendVisible(true);
        energyByRoomChart.setAnimated(true);
        
        // Initialize with real room power data
        updateEnergyPieChart();
        
        box.getChildren().addAll(title, energyByRoomChart);
        
        return box;
    }
    
    private VBox createDevicesContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        // Header with title and action buttons
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("All Devices");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));
        title.setStyle("-fx-text-fill: " + TEXT_PRIMARY + ";");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Add Device button
        Button addDeviceBtn = new Button("+ Add Device");
        addDeviceBtn.setStyle("-fx-background-color: " + SUCCESS + "; -fx-text-fill: white; " +
                             "-fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 8; " +
                             "-fx-cursor: hand; -fx-padding: 10 20;");
        addDeviceBtn.setOnMouseEntered(e -> addDeviceBtn.setStyle("-fx-background-color: derive(" + SUCCESS + ", -10%); " +
                    "-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; " +
                    "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 20;"));
        addDeviceBtn.setOnMouseExited(e -> addDeviceBtn.setStyle("-fx-background-color: " + SUCCESS + "; " +
                    "-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; " +
                    "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 20;"));
        addDeviceBtn.setOnAction(e -> showAddDeviceDialog());
        
        header.getChildren().addAll(title, spacer, addDeviceBtn);
        
        // Search bar with filters
        HBox searchBar = new HBox(10);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        searchBar.setPadding(new Insets(10));
        searchBar.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                          "-fx-border-color: #e2e8f0; -fx-border-radius: 10; -fx-border-width: 1;");
        
        Label searchIcon = new Label("🔍");
        searchIcon.setFont(Font.font(18));
        
        TextField searchField = new TextField();
        searchField.setPromptText("Search by name, type, or ID...");
        searchField.setPrefWidth(400);
        searchField.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; " +
                            "-fx-font-size: 13px;");
        
        ComboBox<String> typeFilter = new ComboBox<>();
        typeFilter.getItems().addAll("All Types", "Light", "Thermostat", "Smart TV", "Smart Plug", 
                                     "Smart Blinds", "Security Camera", "Door Lock");
        typeFilter.setValue("All Types");
        typeFilter.setStyle("-fx-font-size: 12px;");
        
        ComboBox<String> roomFilter = new ComboBox<>();
        roomFilter.getItems().add("All Rooms");
        for (Room room : home.getRooms()) {
            roomFilter.getItems().add(room.getName());
        }
        roomFilter.setValue("All Rooms");
        roomFilter.setStyle("-fx-font-size: 12px;");
        
        Button clearBtn = new Button("Clear");
        clearBtn.setStyle("-fx-background-color: #6b7280; -fx-text-fill: white; " +
                         "-fx-font-size: 11px; -fx-background-radius: 6; -fx-cursor: hand;");
        clearBtn.setOnAction(e -> {
            searchField.clear();
            typeFilter.setValue("All Types");
            roomFilter.setValue("All Rooms");
            filterDeviceTable(searchField.getText(), typeFilter.getValue(), roomFilter.getValue());
        });
        
        searchBar.getChildren().addAll(searchIcon, searchField, typeFilter, roomFilter, clearBtn);
        
        // Add listeners for live search
        searchField.textProperty().addListener((obs, old, newVal) -> 
            filterDeviceTable(newVal, typeFilter.getValue(), roomFilter.getValue()));
        typeFilter.valueProperty().addListener((obs, old, newVal) -> 
            filterDeviceTable(searchField.getText(), newVal, roomFilter.getValue()));
        roomFilter.valueProperty().addListener((obs, old, newVal) -> 
            filterDeviceTable(searchField.getText(), typeFilter.getValue(), newVal));
        
        TableView<DeviceData> table = createDeviceTable();
        
        content.getChildren().addAll(header, searchBar, table);
        
        return content;
    }
    
    private void filterDeviceTable(String searchText, String typeFilter, String roomFilter) {
        // Save current filter state
        this.currentSearchText = searchText != null ? searchText : "";
        this.currentTypeFilter = typeFilter != null ? typeFilter : "All Types";
        this.currentRoomFilter = roomFilter != null ? roomFilter : "All Rooms";
        
        ObservableList<DeviceData> filtered = FXCollections.observableArrayList();
        
        for (SmartDevice device : home.getAllDevices()) {
            boolean matchesSearch = searchText == null || searchText.isEmpty() || 
                                   device.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                                   device.getType().toLowerCase().contains(searchText.toLowerCase()) ||
                                   device.getId().toLowerCase().contains(searchText.toLowerCase());
            
            boolean matchesType = typeFilter.equals("All Types") || 
                                 device.getType().equalsIgnoreCase(typeFilter);
            
            boolean matchesRoom = roomFilter.equals("All Rooms") || 
                                 device.getLocation().equalsIgnoreCase(roomFilter);
            
            if (matchesSearch && matchesType && matchesRoom) {
                filtered.add(new DeviceData(device));
            }
        }
        
        deviceData.setAll(filtered);
    }
    
    private void showAddDeviceDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Device");
        dialog.setHeaderText("Create a new smart device");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField nameField = new TextField();
        nameField.setPromptText("Device name");
        
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Light", "Thermostat", "Smart TV", "Smart Plug", 
                                    "Smart Blinds", "Security Camera", "Door Lock");
        typeCombo.setPromptText("Select device type");
        
        ComboBox<String> roomCombo = new ComboBox<>();
        for (Room room : home.getRooms()) {
            roomCombo.getItems().add(room.getName());
        }
        roomCombo.setPromptText("Select room");
        
        // Type-specific fields
        TextField powerField = new TextField("50");
        powerField.setPromptText("Power consumption (W)");
        
        grid.add(new Label("Device Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Device Type:"), 0, 1);
        grid.add(typeCombo, 1, 1);
        grid.add(new Label("Room:"), 0, 2);
        grid.add(roomCombo, 1, 2);
        grid.add(new Label("Power (W):"), 0, 3);
        grid.add(powerField, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String name = nameField.getText().trim();
                String type = typeCombo.getValue();
                String roomName = roomCombo.getValue();
                
                if (name.isEmpty() || type == null || roomName == null) {
                    showAlert("Invalid Input", "Please fill in all required fields", Alert.AlertType.ERROR);
                    return;
                }
                
                Room room = home.getRoom(roomName);
                if (room == null) {
                    showAlert("Error", "Room not found", Alert.AlertType.ERROR);
                    return;
                }
                
                try {
                    SmartDevice newDevice = createDeviceByType(type, name, roomName, powerField);
                    room.addDevice(newDevice);
                    home.registerDevice(newDevice);
                    refreshAllData();
                    showEnhancedAlert("Success", "Device '" + name + "' added to " + roomName + "!", 
                                    Alert.AlertType.INFORMATION);
                } catch (Exception ex) {
                    showAlert("Error", "Failed to create device: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }
    
    private SmartDevice createDeviceByType(String type, String name, String location, 
                                          TextField powerField) {
        SmartDevice device;
        switch (type) {
            case "Light":
                device = new SmartLight(name, location);
                // Default brightness is 100% (set in SmartLight constructor)
                break;
            case "Thermostat":
                device = new Thermostat(name, location, 22.0); // default 22°C
                break;
            case "Smart TV":
                device = new SmartTV(name, location);
                break;
            case "Smart Plug":
                device = new SmartPlug(name, location);
                if (powerField.getText() != null && !powerField.getText().isEmpty()) {
                    double power = Double.parseDouble(powerField.getText());
                    ((SmartPlug) device).connectDevice("Connected Device", power);
                }
                break;
            case "Smart Blinds":
                device = new SmartBlinds(name, location);
                break;
            case "Security Camera":
                device = new SecurityCamera(name, location);
                break;
            case "Door Lock":
                device = new DoorLock(name, location);
                break;
            default:
                throw new IllegalArgumentException("Unknown device type: " + type);
        }
        return device;
    }
    
    private TableView<DeviceData> createDeviceTable() {
        TableView<DeviceData> table = new TableView<>();
        table.setItems(deviceData);
        table.setPrefHeight(600);
        table.setStyle("-fx-background-color: white;");
        
        TableColumn<DeviceData, String> nameCol = new TableColumn<>("Device");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(180);
        
        TableColumn<DeviceData, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(130);
        
        TableColumn<DeviceData, String> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("room"));
        roomCol.setPrefWidth(130);
        
        TableColumn<DeviceData, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(200);
        
        TableColumn<DeviceData, Number> powerCol = new TableColumn<>("Power (W)");
        powerCol.setCellValueFactory(new PropertyValueFactory<>("power"));
        powerCol.setPrefWidth(100);
        
        TableColumn<DeviceData, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setPrefWidth(250);
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button onBtn = new Button("ON");
            private final Button offBtn = new Button("OFF");
            private final Button removeBtn = new Button("Remove");
            private final HBox box = new HBox(5, onBtn, offBtn, removeBtn);
            
            {
                onBtn.setStyle("-fx-background-color: " + SUCCESS + "; -fx-text-fill: white; -fx-font-size: 11px; -fx-cursor: hand; -fx-padding: 5 10;");
                offBtn.setStyle("-fx-background-color: " + DANGER + "; -fx-text-fill: white; -fx-font-size: 11px; -fx-cursor: hand; -fx-padding: 5 10;");
                removeBtn.setStyle("-fx-background-color: #6b7280; -fx-text-fill: white; -fx-font-size: 11px; -fx-cursor: hand; -fx-padding: 5 10;");
                
                onBtn.setOnAction(e -> {
                    DeviceData device = getTableView().getItems().get(getIndex());
                    device.turnOn();
                    refreshAllData();
                    showAlert("Device Control", device.getName() + " turned ON", Alert.AlertType.INFORMATION);
                });
                
                offBtn.setOnAction(e -> {
                    DeviceData device = getTableView().getItems().get(getIndex());
                    device.turnOff();
                    refreshAllData();
                    showAlert("Device Control", device.getName() + " turned OFF", Alert.AlertType.INFORMATION);
                });
                
                removeBtn.setOnAction(e -> {
                    DeviceData deviceData = getTableView().getItems().get(getIndex());
                    // Find the actual device from home
                    SmartDevice actualDevice = home.getAllDevices().stream()
                        .filter(d -> d.getId().equals(deviceData.getId()))
                        .findFirst()
                        .orElse(null);
                    if (actualDevice != null) {
                        showRemoveDeviceConfirmation(actualDevice);
                    }
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(box);
                }
            }
        });
        
        table.getColumns().addAll(nameCol, typeCol, roomCol, statusCol, powerCol, actionCol);
        
        return table;
    }
    
    private VBox createRoomsContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        Label title = new Label("Room Management");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));
        title.setStyle("-fx-text-fill: " + TEXT_PRIMARY + ";");
        
        FlowPane roomsGrid = new FlowPane();
        roomsGrid.setHgap(15);
        roomsGrid.setVgap(15);
        
        for (Room room : home.getRooms()) {
            roomsGrid.getChildren().add(createDetailedRoomCard(room));
        }
        
        content.getChildren().addAll(title, roomsGrid);
        
        return content;
    }
    
    private VBox createRoomSummaryCard(Room room) {
        VBox card = new VBox(15);
        card.setPrefWidth(320);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #ffffff, #f0f9ff); " +
            "-fx-background-radius: 15; " +
            "-fx-border-color: linear-gradient(to right, #3b82f6, #8b5cf6); " +
            "-fx-border-radius: 15; -fx-border-width: 2; " +
            "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.3), 15, 0, 0, 5);"
        );
        
        // Store room reference for updates
        card.setUserData(room);
        
        // Add hover animation
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), card);
        scaleUp.setToX(1.03);
        scaleUp.setToY(1.03);
        
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), card);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);
        
        card.setOnMouseEntered(e -> scaleUp.playFromStart());
        card.setOnMouseExited(e -> scaleDown.playFromStart());
        
        // Room Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label roomIcon = new Label(getRoomIcon(room.getName()));
        roomIcon.setFont(Font.font(28));
        
        Label roomName = new Label(room.getName());
        roomName.setFont(Font.font("System", FontWeight.BOLD, 18));
        roomName.setStyle("-fx-text-fill: #3b82f6;");
        
        header.getChildren().addAll(roomIcon, roomName);
        
        // Quick Stats
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(15);
        statsGrid.setVgap(10);
        statsGrid.setPadding(new Insets(10, 0, 10, 0));
        
        // Device Count - DYNAMIC
        VBox deviceBox = new VBox(3);
        Label deviceIcon = new Label("📱");
        deviceIcon.setFont(Font.font(20));
        Label deviceCount = new Label();
        deviceCount.setFont(Font.font("System", FontWeight.BOLD, 16));
        deviceCount.setStyle("-fx-text-fill: #1e293b;");
        Label deviceLabel = new Label("Devices");
        deviceLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
        deviceBox.getChildren().addAll(deviceIcon, deviceCount, deviceLabel);
        
        // Power Usage - DYNAMIC
        VBox powerBox = new VBox(3);
        Label powerIcon = new Label("⚡");
        powerIcon.setFont(Font.font(20));
        Label powerValue = new Label();
        powerValue.setFont(Font.font("System", FontWeight.BOLD, 16));
        powerValue.setStyle("-fx-text-fill: #1e293b;");
        Label powerLabel = new Label("Power");
        powerLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
        powerBox.getChildren().addAll(powerIcon, powerValue, powerLabel);
        
        // Status Indicator - DYNAMIC
        VBox statusBox = new VBox(3);
        Label statusIcon = new Label();
        statusIcon.setFont(Font.font(20));
        Label statusText = new Label();
        statusText.setFont(Font.font("System", FontWeight.BOLD, 16));
        Label statusLabel = new Label("Status");
        statusLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
        statusBox.getChildren().addAll(statusIcon, statusText, statusLabel);
        
        // Update function for this card
        Runnable updateCard = () -> {
            int totalDevices = room.getDevices().size();
            int activeDevices = (int) room.getDevices().stream().filter(SmartDevice::isOn).count();
            double totalPower = room.getDevices().stream()
                .filter(SmartDevice::isOn)
                .mapToDouble(SmartDevice::getCurrentPowerConsumption)
                .sum();
            boolean hasActiveDevices = activeDevices > 0;
            
            Platform.runLater(() -> {
                deviceCount.setText(activeDevices + "/" + totalDevices);
                powerValue.setText(String.format("%.0fW", totalPower));
                statusIcon.setText(hasActiveDevices ? "✅" : "💤");
                statusText.setText(hasActiveDevices ? "Active" : "Idle");
                statusText.setStyle("-fx-text-fill: " + (hasActiveDevices ? "#10b981" : "#94a3b8") + ";");
            });
        };
        
        // Store update function in card properties
        card.getProperties().put("updateCard", updateCard);
        
        // Initial update
        updateCard.run();
        
        statsGrid.add(deviceBox, 0, 0);
        statsGrid.add(powerBox, 1, 0);
        statsGrid.add(statusBox, 2, 0);
        
        // Separator
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #e2e8f0;");
        
        // Quick Actions
        HBox actionsBox = new HBox(10);
        actionsBox.setAlignment(Pos.CENTER);
        
        Button manageBtn = new Button("🔧 Manage Devices");
        manageBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #3b82f6, #2563eb); " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 12px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 8 16;"
        );
        
        manageBtn.setOnMouseEntered(e -> {
            manageBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #2563eb, #1d4ed8); " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 12px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand; " +
                "-fx-padding: 8 16;"
            );
        });
        
        manageBtn.setOnMouseExited(e -> {
            manageBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, #3b82f6, #2563eb); " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 12px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand; " +
                "-fx-padding: 8 16;"
            );
        });
        
        manageBtn.setOnAction(e -> {
            // Switch to Rooms tab (index 2)
            TabPane tabPane = (TabPane) ((VBox) mainTabsContainer).getChildren().get(1);
            tabPane.getSelectionModel().select(2);
        });
        
        actionsBox.getChildren().add(manageBtn);
        
        card.getChildren().addAll(header, statsGrid, separator, actionsBox);
        
        return card;
    }
    
    private VBox createDetailedRoomCard(Room room) {
        VBox card = new VBox(15);
        card.setPrefWidth(450);
        card.setPadding(new Insets(20));
        
        // IoT-themed gradient card with shadow
        card.setStyle("-fx-background-color: linear-gradient(to bottom right, #ffffff, #f0f9ff); " +
                     "-fx-background-radius: 15; " +
                     "-fx-border-color: linear-gradient(to right, #3b82f6, #8b5cf6); " +
                     "-fx-border-radius: 15; -fx-border-width: 2; " +
                     "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.3), 15, 0, 0, 5);");
        
        // Add hover animation
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), card);
        scaleUp.setToX(1.02);
        scaleUp.setToY(1.02);
        
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), card);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);
        
        card.setOnMouseEntered(e -> scaleUp.playFromStart());
        card.setOnMouseExited(e -> scaleDown.playFromStart());
        
        // Room name with icon
        HBox roomHeader = new HBox(10);
        roomHeader.setAlignment(Pos.CENTER_LEFT);
        
        Label roomIcon = new Label(getRoomIcon(room.getName()));
        roomIcon.setFont(Font.font(22));
        
        Label roomName = new Label(room.getName());
        roomName.setFont(Font.font("System", FontWeight.BOLD, 18));
        roomName.setStyle("-fx-text-fill: " + PRIMARY + ";");
        
        roomHeader.getChildren().addAll(roomIcon, roomName);
        
        // Device List with animated indicators
        VBox deviceList = new VBox(8);
        for (SmartDevice device : room.getDevices()) {
            HBox deviceRow = new HBox(10);
            deviceRow.setAlignment(Pos.CENTER_LEFT);
            deviceRow.setPadding(new Insets(5));
            deviceRow.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 8;");
            
            Label deviceName = new Label(device.getName());
            deviceName.setPrefWidth(150);
            deviceName.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-size: 12px;");
            
            // Animated status indicator with pulse effect
            Circle statusIndicator = new Circle(6);
            statusIndicator.setFill(device.isOn() ? Color.web(SUCCESS) : Color.web(TEXT_SECONDARY));
            
            if (device.isOn()) {
                // Add pulsing animation for active devices
                Timeline pulse = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(statusIndicator.radiusProperty(), 6)),
                    new KeyFrame(Duration.millis(800), new KeyValue(statusIndicator.radiusProperty(), 8)),
                    new KeyFrame(Duration.millis(1600), new KeyValue(statusIndicator.radiusProperty(), 6))
                );
                pulse.setCycleCount(Timeline.INDEFINITE);
                pulse.play();
            }
            
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            
            // Create toggle button with proper state
            Button toggleBtn = new Button(device.isOn() ? "Turn OFF" : "Turn ON");
            updateToggleButtonStyle(toggleBtn, device.isOn());
            
            // Add button hover animation
            toggleBtn.setOnMouseEntered(ev -> {
                toggleBtn.setStyle(toggleBtn.getStyle() + "-fx-scale-x: 1.05; -fx-scale-y: 1.05;");
            });
            toggleBtn.setOnMouseExited(ev -> {
                updateToggleButtonStyle(toggleBtn, device.isOn());
            });
            
            toggleBtn.setOnAction(e -> {
                // Toggle device
                device.toggle();
                boolean isOn = device.isOn();
                
                // Update button with animation
                FadeTransition fade = new FadeTransition(Duration.millis(150), toggleBtn);
                fade.setFromValue(1.0);
                fade.setToValue(0.3);
                fade.setOnFinished(ev -> {
                    toggleBtn.setText(isOn ? "Turn OFF" : "Turn ON");
                    updateToggleButtonStyle(toggleBtn, isOn);
                    statusIndicator.setFill(isOn ? Color.web(SUCCESS) : Color.web(TEXT_SECONDARY));
                    
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(150), toggleBtn);
                    fadeIn.setFromValue(0.3);
                    fadeIn.setToValue(1.0);
                    fadeIn.play();
                });
                fade.play();
                
                // Refresh data
                refreshAllData();
                
                // Show notification
                showEnhancedAlert("Device Toggled", 
                    device.getName() + " is now " + (isOn ? "ON" : "OFF") + "\n\n" +
                    "Power: " + String.format("%.0fW", device.getCurrentPowerConsumption()),
                    Alert.AlertType.INFORMATION);
            });
            
            deviceRow.getChildren().addAll(deviceName, statusIndicator, spacer, toggleBtn);
            deviceList.getChildren().add(deviceRow);
        }
        
        card.getChildren().addAll(roomHeader, new Separator(), deviceList);
        
        return card;
    }
    
    private void updateToggleButtonStyle(Button button, boolean isOn) {
        String baseStyle = "-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; " +
                          "-fx-cursor: hand; -fx-background-radius: 8; -fx-padding: 6 12; ";
        if (isOn) {
            button.setStyle(baseStyle + "-fx-background-color: linear-gradient(to right, #ef4444, #dc2626);");
        } else {
            button.setStyle(baseStyle + "-fx-background-color: linear-gradient(to right, #10b981, #059669);");
        }
    }
    
    private String getRoomIcon(String roomName) {
        switch (roomName.toLowerCase()) {
            case "living room": return "\uD83D\uDECB";
            case "kitchen": return "\uD83C\uDF73";
            case "bedroom": return "\uD83D\uDECF";
            case "bathroom": return "\uD83D\uDEBF";
            case "office": return "\uD83D\uDCBC";
            default: return "\uD83C\uDFE0";
        }
    }
    
    private VBox createAutomationContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        
        // Automation Guide Box
        VBox guideBox = createAutomationGuideBox();
        
        // Header with Add button
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("Automation Rules");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));
        title.setStyle("-fx-text-fill: " + TEXT_PRIMARY + ";");
        
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        
        Button addRuleBtn = new Button("+ Add New Rule");
        addRuleBtn.setStyle("-fx-background-color: " + PRIMARY + "; -fx-text-fill: white; " +
                           "-fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 8; -fx-padding: 10 20;");
        addRuleBtn.setOnAction(e -> showAddRuleDialog());
        
        header.getChildren().addAll(title, spacer1, addRuleBtn);
        
        // Rules List
        VBox rulesList = new VBox(12);
        
        if (automationEngine.getRules().isEmpty()) {
            Label emptyMsg = new Label("No automation rules configured. Click 'Add New Rule' to create one.");
            emptyMsg.setStyle("-fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-style: italic; -fx-padding: 20;");
            rulesList.getChildren().add(emptyMsg);
        } else {
            for (Rule rule : automationEngine.getRules()) {
                VBox ruleCard = createRuleCard(rule);
                rulesList.getChildren().add(ruleCard);
            }
        }
        
        ScrollPane scrollPane = new ScrollPane(rulesList);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + BACKGROUND + "; -fx-background-color: " + BACKGROUND + ";");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        content.getChildren().addAll(guideBox, header, scrollPane);
        
        return content;
    }
    
    private VBox createAutomationGuideBox() {
        VBox guideBox = new VBox(10);
        guideBox.setPadding(new Insets(15));
        guideBox.setStyle("-fx-background-color: linear-gradient(to bottom right, #ffffff, #f0f9ff); " +
                         "-fx-background-radius: 15; " +
                         "-fx-border-color: linear-gradient(to right, #3b82f6, #8b5cf6); " +
                         "-fx-border-radius: 15; -fx-border-width: 2; " +
                         "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.3), 15, 0, 0, 5);");
        
        Label guideTitle = new Label("Automation Rules Guide");
        guideTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        guideTitle.setStyle("-fx-text-fill: " + PRIMARY + ";");
        
        Label guideText = new Label(
            "HOW TO USE:\n" +
            "1. CREATE: Click 'Add New Rule' to create automated actions (lights, temperature, security)\n" +
            "2. ENABLE/DISABLE: Toggle rules on/off without deleting them\n" +
            "3. EDIT: Modify rule name, triggers, and actions\n" +
            "4. TEST: Execute rules immediately to verify they work\n" +
            "5. DELETE: Remove rules you no longer need\n\n" +
            "HOW TO VERIFY A RULE WORKS:\n" +
            "• After creating, click 'Edit' then 'Test Rule Now' to execute immediately\n" +
            "• Watch console output for confirmation messages\n" +
            "• Check device states in Dashboard or Devices tab\n" +
            "• For time-based rules, wait for trigger time or test manually\n\n" +
            "ACTIVE RULES: Green border | PAUSED RULES: Gray border"
        );
        guideText.setWrapText(true);
        guideText.setStyle("-fx-text-fill: #1e40af; -fx-font-size: 12px; -fx-line-spacing: 2px;");
        
        guideBox.getChildren().addAll(guideTitle, guideText);
        return guideBox;
    }
    
    private VBox createRuleCard(Rule rule) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(15));
        String borderColor = rule.isEnabled() ? "linear-gradient(to right, #10b981, #059669)" : "linear-gradient(to right, #3b82f6, #8b5cf6)";
        card.setStyle("-fx-background-color: linear-gradient(to bottom right, #ffffff, #f0f9ff); " +
                     "-fx-background-radius: 15; " +
                     "-fx-border-color: " + borderColor + "; " +
                     "-fx-border-radius: 15; -fx-border-width: 2; " +
                     "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.3), 15, 0, 0, 5);");
        
        // Rule header
        HBox ruleHeader = new HBox(15);
        ruleHeader.setAlignment(Pos.CENTER_LEFT);
        
        Label ruleIcon = new Label("[*]");
        ruleIcon.setFont(Font.font(16));
        ruleIcon.setStyle("-fx-text-fill: " + PRIMARY + "; -fx-font-weight: bold;");
        
        VBox nameBox = new VBox(3);
        Label ruleName = new Label(rule.getName());
        ruleName.setFont(Font.font("System", FontWeight.BOLD, 16));
        ruleName.setStyle("-fx-text-fill: " + TEXT_PRIMARY + ";");
        
        Label ruleDescription = new Label(getRuleDescription(rule.getName()));
        ruleDescription.setStyle("-fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-size: 12px;");
        ruleDescription.setWrapText(true);
        ruleDescription.setMaxWidth(500);
        
        nameBox.getChildren().addAll(ruleName, ruleDescription);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label statusLabel = new Label(rule.isEnabled() ? "Active" : "Paused");
        statusLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        statusLabel.setStyle("-fx-text-fill: " + (rule.isEnabled() ? SUCCESS : WARNING) + "; " +
                            "-fx-background-color: " + (rule.isEnabled() ? SUCCESS + "22" : WARNING + "22") + "; " +
                            "-fx-padding: 5 10; -fx-background-radius: 5;");
        
        ruleHeader.getChildren().addAll(ruleIcon, nameBox, spacer, statusLabel);
        
        // Action buttons
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);
        
        Button toggleBtn = new Button(rule.isEnabled() ? "Disable" : "Enable");
        toggleBtn.setStyle("-fx-background-color: " + (rule.isEnabled() ? WARNING : SUCCESS) + "; " +
                          "-fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 6; -fx-padding: 8 15; -fx-font-size: 12px;");
        toggleBtn.setOnAction(e -> {
            rule.setEnabled(!rule.isEnabled());
            String updatedBorderColor = rule.isEnabled() ? "linear-gradient(to right, #10b981, #059669)" : "linear-gradient(to right, #3b82f6, #8b5cf6)";
            card.setStyle("-fx-background-color: linear-gradient(to bottom right, #ffffff, #f0f9ff); " +
                         "-fx-background-radius: 15; " +
                         "-fx-border-color: " + updatedBorderColor + "; " +
                         "-fx-border-radius: 15; -fx-border-width: 2; " +
                         "-fx-effect: dropshadow(gaussian, rgba(59, 130, 246, 0.3), 15, 0, 0, 5);");
            statusLabel.setText(rule.isEnabled() ? "Active" : "Paused");
            statusLabel.setStyle("-fx-text-fill: " + (rule.isEnabled() ? SUCCESS : WARNING) + "; " +
                                "-fx-background-color: " + (rule.isEnabled() ? SUCCESS + "22" : WARNING + "22") + "; " +
                                "-fx-padding: 5 10; -fx-background-radius: 5;");
            toggleBtn.setText(rule.isEnabled() ? "Disable" : "Enable");
            toggleBtn.setStyle("-fx-background-color: " + (rule.isEnabled() ? WARNING : SUCCESS) + "; " +
                              "-fx-text-fill: white; -fx-cursor: hand; -fx-background-radius: 6; -fx-padding: 8 15;");
            showEnhancedAlert("Rule Updated", 
                rule.getName() + " is now " + (rule.isEnabled() ? "ENABLED" : "DISABLED") + "\n\n" +
                ruleDescription.getText(),
                Alert.AlertType.INFORMATION);
        });
        
        Button editBtn = new Button("Edit");
        editBtn.setStyle("-fx-background-color: " + PRIMARY + "; -fx-text-fill: white; " +
                        "-fx-cursor: hand; -fx-background-radius: 6; -fx-padding: 8 15; -fx-font-size: 12px;");
        editBtn.setOnAction(e -> showEditRuleDialog(rule));
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: " + DANGER + "; -fx-text-fill: white; " +
                          "-fx-cursor: hand; -fx-background-radius: 6; -fx-padding: 8 15; -fx-font-size: 12px;");
        deleteBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Delete Rule");
            confirm.setHeaderText("Delete " + rule.getName() + "?");
            confirm.setContentText("This action cannot be undone.");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    deleteRule(rule);
                }
            });
        });
        
        actions.getChildren().addAll(toggleBtn, editBtn, deleteBtn);
        
        card.getChildren().addAll(ruleHeader, new Separator(), actions);
        
        return card;
    }
    
    private String getRuleDescription(String ruleName) {
        switch (ruleName) {
            case "Motion Light Rule":
                return "🚶 Automatically turns on lights in the Living Room when motion is detected. " +
                       "Lights stay on for 5 minutes after last motion.";
            case "Night Mode":
                return "🌙 Automatically dims lights to 20% and sets temperature to 19°C after 10 PM. " +
                       "Activates security cameras and locks doors.";
            case "Energy Saver":
                return "💰 Turns off devices that have been idle for more than 30 minutes. " +
                       "Helps reduce energy consumption and costs.";
            case "Morning Routine":
                return "☀️ Gradually turns on lights at 7 AM, opens blinds, and sets temperature to 22°C. " +
                       "Starts coffee machine automatically.";
            case "Away Mode":
                return "🏠 When all occupants leave, turns off all non-essential devices, " +
                       "arms security system, and locks all doors.";
            default:
                return "Custom automation rule. Configure triggers, conditions, and actions to automate your home.";
        }
    }
    
    private HBox createStatusBar() {
        HBox statusBar = new HBox(20);
        statusBar.setPadding(new Insets(10, 20, 10, 20));
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.setStyle("-fx-background-color: " + CARD_BG + "; -fx-border-color: " + BORDER + "; -fx-border-width: 2 0 0 0;");
        
        Label statusLabel = new Label("System Status: All Systems Operational");
        statusLabel.setStyle("-fx-text-fill: " + SUCCESS + "; -fx-font-weight: bold;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label versionLabel = new Label("Smart Home v1.0 • " + home.getAllDevices().size() + " devices");
        versionLabel.setStyle("-fx-text-fill: " + TEXT_SECONDARY + ";");
        
        statusBar.getChildren().addAll(statusLabel, spacer, versionLabel);
        
        return statusBar;
    }
    
    private void showRoomDetails(Room room) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Room Details");
        alert.setHeaderText(room.getName() + " - " + room.getFloor());
        
        StringBuilder content = new StringBuilder();
        content.append("Area: ").append(room.getArea()).append(" m²\n");
        content.append("Temperature: ").append(String.format("%.1f°C", room.getCurrentTemperature())).append("\n");
        content.append("Devices: ").append(room.getDevices().size()).append("\n");
        content.append("Active: ").append(room.getActiveDeviceCount()).append("\n");
        content.append("Power: ").append(String.format("%.1fW", room.getTotalPowerConsumption())).append("\n\n");
        
        content.append("Devices:\n");
        for (SmartDevice device : room.getDevices()) {
            content.append("  • ").append(device.getName()).append(" - ").append(device.getStatus()).append("\n");
        }
        
        alert.setContentText(content.toString());
        alert.showAndWait();
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.show();
            
            // Auto-close after 2 seconds
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> alert.close()));
            timeline.play();
        });
    }
    
    private void showEnhancedAlert(String title, String message, Alert.AlertType type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            
            // Style the alert
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle("-fx-background-color: white; -fx-font-size: 13px;");
            dialogPane.lookupButton(ButtonType.OK).setStyle(
                "-fx-background-color: " + PRIMARY + "; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-cursor: hand;");
            
            alert.show();
            
            // Auto-close after 3 seconds
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> alert.close()));
            timeline.play();
        });
    }
    
    private void showRemoveDeviceConfirmation(SmartDevice device) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Remove Device");
        confirmation.setHeaderText("Are you sure you want to remove this device?");
        confirmation.setContentText("Device: " + device.getName() + "\n" +
                                   "Type: " + device.getType() + "\n" +
                                   "Location: " + device.getLocation() + "\n\n" +
                                   "This action cannot be undone.");
        
        // Style the confirmation dialog
        DialogPane dialogPane = confirmation.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white; -fx-font-size: 13px;");
        dialogPane.lookupButton(ButtonType.OK).setStyle(
            "-fx-background-color: " + DANGER + "; -fx-text-fill: white; " +
            "-fx-font-weight: bold; -fx-cursor: hand;");
        dialogPane.lookupButton(ButtonType.CANCEL).setStyle(
            "-fx-background-color: #6b7280; -fx-text-fill: white; " +
            "-fx-font-weight: bold; -fx-cursor: hand;");
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                removeDevice(device);
            }
        });
    }
    
    private void removeDevice(SmartDevice device) {
        // Find and remove from room
        Room room = home.getRoom(device.getLocation());
        if (room != null) {
            boolean removed = room.removeDevice(device);
            if (removed) {
                // Remove from device registry
                home.getAllDevices().remove(device);
                
                // Refresh UI
                refreshAllData();
                
                showEnhancedAlert("Device Removed", 
                    "Device '" + device.getName() + "' has been removed from " + device.getLocation() + ".\n\n" +
                    "Remaining devices in room: " + room.getDevices().size(),
                    Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "Failed to remove device from room", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Error", "Room not found: " + device.getLocation(), Alert.AlertType.ERROR);
        }
    }
    
    // Rule CRUD Operations
    private void showAddRuleDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Create New Automation Rule");
        dialog.setHeaderText("🎯 Guided Rule Creation");
        
        // Create the custom dialog content
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setPrefWidth(600);
        
        // Step 1: Rule Name
        Label step1Label = new Label("STEP 1: Rule Name");
        step1Label.setFont(Font.font("System", FontWeight.BOLD, 14));
        step1Label.setStyle("-fx-text-fill: " + PRIMARY + ";");
        TextField nameField = new TextField();
        nameField.setPromptText("e.g., Evening Lights, Security Alert, etc.");
        nameField.setStyle("-fx-font-size: 13px; -fx-padding: 8;");
        
        // Step 2: Trigger Type Selection
        Label step2Label = new Label("2️⃣ When to Trigger (Select One)");
        step2Label.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        VBox triggerBox = new VBox(8);
        ToggleGroup triggerGroup = new ToggleGroup();
        
        RadioButton timeRB = new RadioButton("⏰ Time-Based - Trigger at specific time");
        timeRB.setToggleGroup(triggerGroup);
        timeRB.setSelected(true);
        timeRB.setUserData("time");
        
        RadioButton motionRB = new RadioButton("🚶 Motion Sensor - When motion detected");
        motionRB.setToggleGroup(triggerGroup);
        motionRB.setUserData("motion");
        
        RadioButton tempRB = new RadioButton("🌡️ Temperature - When temperature changes");
        tempRB.setToggleGroup(triggerGroup);
        tempRB.setUserData("temperature");
        
        RadioButton deviceRB = new RadioButton("🔌 Device State - When device turns on/off");
        deviceRB.setToggleGroup(triggerGroup);
        deviceRB.setUserData("device");
        
        triggerBox.getChildren().addAll(timeRB, motionRB, tempRB, deviceRB);
        
        // Time settings (shown when time-based selected)
        HBox timeSettings = new HBox(10);
        timeSettings.setAlignment(Pos.CENTER_LEFT);
        Label timeLabel = new Label("Time:");
        Spinner<Integer> hourSpinner = new Spinner<>(0, 23, 20);
        hourSpinner.setPrefWidth(70);
        Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, 0);
        minuteSpinner.setPrefWidth(70);
        timeSettings.getChildren().addAll(timeLabel, hourSpinner, new Label(":"), minuteSpinner);
        
        // Room selection for motion/device triggers
        HBox roomSettings = new HBox(10);
        roomSettings.setAlignment(Pos.CENTER_LEFT);
        roomSettings.setVisible(false);
        Label roomLabel = new Label("Room:");
        ComboBox<String> roomCombo = new ComboBox<>();
        for (Room room : home.getRooms()) {
            roomCombo.getItems().add(room.getName());
        }
        if (!roomCombo.getItems().isEmpty()) {
            roomCombo.getSelectionModel().select(0);
        }
        roomSettings.getChildren().addAll(roomLabel, roomCombo);
        
        // Temperature threshold
        HBox tempSettings = new HBox(10);
        tempSettings.setAlignment(Pos.CENTER_LEFT);
        tempSettings.setVisible(false);
        Label tempLabel = new Label("Threshold:");
        Spinner<Integer> tempSpinner = new Spinner<>(15, 35, 25);
        tempSpinner.setPrefWidth(80);
        ComboBox<String> tempCondition = new ComboBox<>();
        tempCondition.getItems().addAll("Above", "Below");
        tempCondition.getSelectionModel().select(0);
        tempSettings.getChildren().addAll(tempLabel, tempSpinner, new Label("°C"), tempCondition);
        
        // Dynamic trigger settings based on selection
        VBox triggerSettingsBox = new VBox(10);
        triggerSettingsBox.getChildren().add(timeSettings);
        
        triggerGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            triggerSettingsBox.getChildren().clear();
            if (newVal == timeRB) {
                triggerSettingsBox.getChildren().add(timeSettings);
            } else if (newVal == motionRB) {
                triggerSettingsBox.getChildren().add(roomSettings);
                roomSettings.setVisible(true);
            } else if (newVal == tempRB) {
                triggerSettingsBox.getChildren().add(tempSettings);
                tempSettings.setVisible(true);
            } else if (newVal == deviceRB) {
                triggerSettingsBox.getChildren().add(roomSettings);
                roomSettings.setVisible(true);
            }
        });
        
        // Step 3: Actions
        Label step3Label = new Label("STEP 3: What to Do (Select Actions)");
        step3Label.setFont(Font.font("System", FontWeight.BOLD, 14));
        step3Label.setStyle("-fx-text-fill: " + PRIMARY + ";");
        
        VBox actionsBox = new VBox(8);
        CheckBox lightsOnCB = new CheckBox("Turn ON all lights");
        CheckBox lightsOffCB = new CheckBox("Turn OFF all lights");
        CheckBox dimLightsCB = new CheckBox("Dim lights to 20%");
        CheckBox tempUpCB = new CheckBox("Set temperature to 22°C");
        CheckBox tempDownCB = new CheckBox("Set temperature to 19°C");
        CheckBox securityOnCB = new CheckBox("Arm security system");
        CheckBox securityOffCB = new CheckBox("Disarm security system");
        CheckBox notificationCB = new CheckBox("Send notification");
        
        actionsBox.getChildren().addAll(lightsOnCB, lightsOffCB, dimLightsCB, 
                                        tempUpCB, tempDownCB, securityOnCB, securityOffCB, notificationCB);
        
        // Step 4: Additional Options
        Label step4Label = new Label("STEP 4: Options");
        step4Label.setFont(Font.font("System", FontWeight.BOLD, 14));
        step4Label.setStyle("-fx-text-fill: " + PRIMARY + ";");
        CheckBox enabledCB = new CheckBox("Enable rule immediately");
        enabledCB.setSelected(true);
        
        // Add all to content
        content.getChildren().addAll(
            step1Label, nameField,
            new Separator(),
            step2Label, triggerBox, triggerSettingsBox,
            new Separator(),
            step3Label, actionsBox,
            new Separator(),
            step4Label, enabledCB
        );
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);
        
        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Style the dialog
        dialog.getDialogPane().setStyle("-fx-background-color: white;");
        
        // Handle OK button
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                String ruleName = nameField.getText().trim();
                if (ruleName.isEmpty()) {
                    showEnhancedAlert("Error", "Please enter a rule name", Alert.AlertType.ERROR);
                    return null;
                }
                
                // Create the rule based on selections
                try {
                    Rule newRule = createRuleFromDialog(
                        ruleName,
                        triggerGroup.getSelectedToggle(),
                        hourSpinner.getValue(), minuteSpinner.getValue(),
                        roomCombo.getSelectionModel().getSelectedItem(),
                        tempSpinner.getValue(), tempCondition.getValue(),
                        lightsOnCB.isSelected(), lightsOffCB.isSelected(), dimLightsCB.isSelected(),
                        tempUpCB.isSelected(), tempDownCB.isSelected(),
                        securityOnCB.isSelected(), securityOffCB.isSelected(),
                        notificationCB.isSelected(),
                        enabledCB.isSelected()
                    );
                    
                    automationEngine.addRule(newRule);
                    
                    // Refresh automation tab
                    Platform.runLater(() -> {
                        automationTab.getChildren().clear();
                        VBox newContent = createAutomationContent();
                        automationTab.getChildren().setAll(newContent.getChildren());
                    });
                    
                    showEnhancedAlert("Rule Created", 
                        ruleName + " has been created successfully!\n\n" +
                        "The rule is now " + (enabledCB.isSelected() ? "active" : "disabled") + ".",
                        Alert.AlertType.INFORMATION);
                        
                } catch (Exception e) {
                    showEnhancedAlert("Error", "Failed to create rule: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
            return buttonType;
        });
        
        dialog.showAndWait();
    }
    
    private Rule createRuleFromDialog(String name, Toggle triggerType, int hour, int minute,
                                     String room, int tempThreshold, String tempCondition,
                                     boolean lightsOn, boolean lightsOff, boolean dimLights,
                                     boolean tempUp, boolean tempDown,
                                     boolean securityOn, boolean securityOff,
                                     boolean notification, boolean enabled) {
        
        // Create trigger based on selection
        Trigger trigger;
        if (triggerType.getUserData() != null && triggerType.getUserData().equals("time")) {
            trigger = new com.smarthome.automation.builtins.TimeAfterTrigger(
                java.time.LocalTime.of(hour, minute)
            );
        } else {
            // Simple always-true trigger for other types (can be extended)
            trigger = new Trigger() {
                @Override
                public boolean evaluate(Context context) {
                    return true;
                }
                @Override
                public String describe() {
                    return "always active";
                }
            };
        }
        
        Rule rule = new Rule(name, trigger);
        
        // Add actions based on checkboxes
        if (lightsOn) {
            rule.addAction(context -> {
                home.turnOnAllLights();
                System.out.println("✅ Rule '" + name + "': Turned ON all lights");
            });
        }
        
        if (lightsOff) {
            rule.addAction(context -> {
                for (Room r : home.getRooms()) {
                    for (SmartDevice device : r.getDevices()) {
                        if (device instanceof com.smarthome.model.devices.SmartLight) {
                            device.turnOff();
                        }
                    }
                }
                System.out.println("🌑 Rule '" + name + "': Turned OFF all lights");
            });
        }
        
        if (dimLights) {
            rule.addAction(context -> {
                for (Room r : home.getRooms()) {
                    for (SmartDevice device : r.getDevices()) {
                        if (device instanceof com.smarthome.model.devices.SmartLight) {
                            ((com.smarthome.model.devices.SmartLight) device).setBrightness(20);
                        }
                    }
                }
                System.out.println("🔅 Rule '" + name + "': Dimmed all lights to 20%");
            });
        }
        
        if (tempUp) {
            rule.addAction(context -> {
                for (Room r : home.getRooms()) {
                    for (SmartDevice device : r.getDevices()) {
                        if (device instanceof com.smarthome.model.devices.Thermostat) {
                            ((com.smarthome.model.devices.Thermostat) device).setTargetTemperature(22.0);
                        }
                    }
                }
                System.out.println("🌡️ Rule '" + name + "': Set temperature to 22°C");
            });
        }
        
        if (tempDown) {
            rule.addAction(context -> {
                for (Room r : home.getRooms()) {
                    for (SmartDevice device : r.getDevices()) {
                        if (device instanceof com.smarthome.model.devices.Thermostat) {
                            ((com.smarthome.model.devices.Thermostat) device).setTargetTemperature(19.0);
                        }
                    }
                }
                System.out.println("❄️ Rule '" + name + "': Set temperature to 19°C");
            });
        }
        
        if (securityOn) {
            rule.addAction(context -> {
                securityService.arm();
                System.out.println("🛡️ Rule '" + name + "': Armed security system");
            });
        }
        
        if (securityOff) {
            rule.addAction(context -> {
                securityService.disarm();
                System.out.println("🔓 Rule '" + name + "': Disarmed security system");
            });
        }
        
        if (notification) {
            rule.addAction(context -> {
                System.out.println("📢 Rule '" + name + "': Notification sent");
            });
        }
        
        rule.setEnabled(enabled);
        return rule;
    }
    
    private void showEditRuleDialog(Rule rule) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Automation Rule");
        dialog.setHeaderText("Modify Rule Settings: " + rule.getName());
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setPrefWidth(650);
        
        // Step 1: Rule Name
        Label step1Label = new Label("STEP 1: Rule Name");
        step1Label.setFont(Font.font("System", FontWeight.BOLD, 14));
        step1Label.setStyle("-fx-text-fill: " + PRIMARY + ";");
        TextField nameField = new TextField(rule.getName());
        nameField.setPromptText("e.g., 'Evening Lights', 'Security Alert'");
        nameField.setStyle("-fx-font-size: 13px; -fx-padding: 8;");
        
        // Step 2: Trigger Selection
        Label step2Label = new Label("STEP 2: When to Trigger (Choose One)");
        step2Label.setFont(Font.font("System", FontWeight.BOLD, 14));
        step2Label.setStyle("-fx-text-fill: " + PRIMARY + ";");
        
        ToggleGroup triggerGroup = new ToggleGroup();
        RadioButton timeRB = new RadioButton("Time-Based (Execute at specific time)");
        RadioButton motionRB = new RadioButton("Motion Sensor (When motion detected)");
        RadioButton tempRB = new RadioButton("Temperature (When temp above/below threshold)");
        RadioButton deviceRB = new RadioButton("Device State (When device turns on/off)");
        
        timeRB.setToggleGroup(triggerGroup);
        motionRB.setToggleGroup(triggerGroup);
        tempRB.setToggleGroup(triggerGroup);
        deviceRB.setToggleGroup(triggerGroup);
        
        timeRB.setUserData("time");
        motionRB.setUserData("motion");
        tempRB.setUserData("temperature");
        deviceRB.setUserData("device");
        
        // Default to time-based
        timeRB.setSelected(true);
        
        VBox triggerBox = new VBox(8);
        triggerBox.getChildren().addAll(timeRB, motionRB, tempRB, deviceRB);
        
        // Trigger settings
        HBox timeSettings = new HBox(10);
        timeSettings.setAlignment(Pos.CENTER_LEFT);
        Label timeLabel = new Label("Time:");
        Spinner<Integer> hourSpinner = new Spinner<>(0, 23, 22);
        hourSpinner.setPrefWidth(80);
        Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, 0);
        minuteSpinner.setPrefWidth(80);
        timeSettings.getChildren().addAll(timeLabel, hourSpinner, new Label(":"), minuteSpinner);
        
        VBox triggerSettingsBox = new VBox(10);
        triggerSettingsBox.getChildren().add(timeSettings);
        
        triggerGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            triggerSettingsBox.getChildren().clear();
            if (newVal == timeRB) {
                triggerSettingsBox.getChildren().add(timeSettings);
            }
        });
        
        // Step 3: Actions
        Label step3Label = new Label("STEP 3: What to Do (Select Actions)");
        step3Label.setFont(Font.font("System", FontWeight.BOLD, 14));
        step3Label.setStyle("-fx-text-fill: " + PRIMARY + ";");
        
        VBox actionsBox = new VBox(8);
        CheckBox lightsOnCB = new CheckBox("Turn ON all lights");
        CheckBox lightsOffCB = new CheckBox("Turn OFF all lights");
        CheckBox dimLightsCB = new CheckBox("Dim lights to 20%");
        CheckBox tempUpCB = new CheckBox("Set temperature to 22°C");
        CheckBox tempDownCB = new CheckBox("Set temperature to 19°C");
        CheckBox securityOnCB = new CheckBox("Arm security system");
        CheckBox securityOffCB = new CheckBox("Disarm security system");
        CheckBox notificationCB = new CheckBox("Send notification");
        
        actionsBox.getChildren().addAll(lightsOnCB, lightsOffCB, dimLightsCB, 
                                        tempUpCB, tempDownCB, securityOnCB, securityOffCB, notificationCB);
        
        // Step 4: Options
        Label step4Label = new Label("STEP 4: Options");
        step4Label.setFont(Font.font("System", FontWeight.BOLD, 14));
        step4Label.setStyle("-fx-text-fill: " + PRIMARY + ";");
        CheckBox enabledCB = new CheckBox("Enable rule immediately");
        enabledCB.setSelected(rule.isEnabled());
        
        // Test button
        HBox testBox = new HBox(10);
        testBox.setAlignment(Pos.CENTER_LEFT);
        Button testBtn = new Button("Test Rule Now");
        testBtn.setStyle("-fx-background-color: " + WARNING + "; -fx-text-fill: white; " +
                        "-fx-cursor: hand; -fx-background-radius: 6; -fx-padding: 10 20; -fx-font-size: 13px;");
        testBtn.setOnAction(e -> {
            Context testContext = new Context(home, System.currentTimeMillis() / 1000);
            rule.evaluateAndExecute(testContext);
            showEnhancedAlert("Rule Tested", 
                "Rule has been executed! Check console output for action confirmations.",
                Alert.AlertType.INFORMATION);
        });
        testBox.getChildren().add(testBtn);
        
        // Add all to content
        content.getChildren().addAll(
            step1Label, nameField,
            new Separator(),
            step2Label, triggerBox, triggerSettingsBox,
            new Separator(),
            step3Label, actionsBox,
            new Separator(),
            step4Label, enabledCB,
            new Separator(),
            testBox
        );
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);
        
        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Style buttons
        Button saveButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        saveButton.setText("Save Changes");
        saveButton.setStyle("-fx-background-color: " + SUCCESS + "; -fx-text-fill: white; -fx-font-weight: bold;");
        
        dialog.getDialogPane().setStyle("-fx-background-color: white;");
        
        // Handle save
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                String newName = nameField.getText().trim();
                if (newName.isEmpty()) {
                    showEnhancedAlert("Error", "Please enter a rule name", Alert.AlertType.ERROR);
                    return null;
                }
                
                // Remove old rule
                automationEngine.removeRule(rule);
                
                // Create new rule with updated settings
                try {
                    Rule updatedRule = createRuleFromDialog(
                        newName,
                        triggerGroup.getSelectedToggle(),
                        hourSpinner.getValue(), minuteSpinner.getValue(),
                        null, 25, "Above",
                        lightsOnCB.isSelected(), lightsOffCB.isSelected(), dimLightsCB.isSelected(),
                        tempUpCB.isSelected(), tempDownCB.isSelected(),
                        securityOnCB.isSelected(), securityOffCB.isSelected(),
                        notificationCB.isSelected(),
                        enabledCB.isSelected()
                    );
                    
                    automationEngine.addRule(updatedRule);
                    
                    // Refresh automation tab
                    Platform.runLater(() -> {
                        automationTab.getChildren().clear();
                        VBox newContent = createAutomationContent();
                        automationTab.getChildren().setAll(newContent.getChildren());
                    });
                    
                    showEnhancedAlert("Rule Updated", 
                        newName + " has been updated successfully!\n\n" +
                        "The rule is now " + (enabledCB.isSelected() ? "active" : "disabled") + ".",
                        Alert.AlertType.INFORMATION);
                        
                } catch (Exception e) {
                    showEnhancedAlert("Error", "Failed to update rule: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
            return buttonType;
        });
        
        dialog.showAndWait();
    }
    
    private void deleteRule(Rule rule) {
        // Remove from automation engine using proper method
        automationEngine.removeRule(rule);
        
        // Refresh the automation tab
        Platform.runLater(() -> {
            automationTab.getChildren().clear();
            VBox newContent = createAutomationContent();
            automationTab.getChildren().setAll(newContent.getChildren());
        });
        
        showEnhancedAlert("Rule Deleted", 
            rule.getName() + " has been removed\n\n" +
            "The automation rule is no longer active.",
            Alert.AlertType.INFORMATION);
    }
    
    private void refreshAllData() {
        // Update properties
        totalPower.set(home.getTotalPowerConsumption());
        hourlyCost.set(home.getEstimatedHourlyCost());
        activeDevices.set(home.getTotalActiveDevices());
        totalDevices.set(home.getAllDevices().size());
        securityArmed.set(home.isSecuritySystemArmed());
        
        // Update device data with current filters applied
        filterDeviceTable(currentSearchText, currentTypeFilter, currentRoomFilter);
        
        // Update room data
        roomData.clear();
        for (Room room : home.getRooms()) {
            roomData.add(new RoomData(room));
        }
        
        // Update room summary cards dynamically
        if (roomSummaryGrid != null) {
            for (javafx.scene.Node node : roomSummaryGrid.getChildren()) {
                if (node instanceof VBox) {
                    VBox card = (VBox) node;
                    Runnable updateCard = (Runnable) card.getProperties().get("updateCard");
                    if (updateCard != null) {
                        updateCard.run();
                    }
                }
            }
        }
        
        // Update charts with real-time data
        updatePowerChart();
        updateEnergyPieChart();
    }
    
    private void simulateSensorChanges() {
        home.getRooms().forEach(room -> {
            room.getSensors().forEach(Sensor::takeMeasurement);
            
            // Simulate temperature changes
            double baseTemp = 21.0;
            LocalTime now = LocalTime.now();
            if (now.isAfter(LocalTime.of(22, 0)) || now.isBefore(LocalTime.of(7, 0))) {
                baseTemp = 19.0;
            } else if (now.isAfter(LocalTime.of(12, 0)) && now.isBefore(LocalTime.of(18, 0))) {
                baseTemp = 23.0;
            }
            room.setCurrentTemperature(baseTemp + (Math.random() - 0.5) * 2);
        });
    }
    
    // Chart Update Methods
    private void updatePowerChart() {
        if (powerConsumptionChart == null || powerConsumptionChart.getData().isEmpty()) {
            return;
        }
        
        XYChart.Series<String, Number> series = powerConsumptionChart.getData().get(0);
        series.getData().clear();
        
        // Update with current power consumption from each room
        for (Room room : home.getRooms()) {
            double power = room.getTotalPowerConsumption();
            series.getData().add(new XYChart.Data<>(room.getName(), power));
        }
        
        // Add total power as well
        series.getData().add(new XYChart.Data<>("Total", home.getTotalPowerConsumption()));
    }
    
    private void updateEnergyPieChart() {
        if (energyByRoomChart == null) {
            return;
        }
        
        energyByRoomChart.getData().clear();
        
        // Calculate total power to show percentages
        double totalPowerAll = home.getTotalPowerConsumption();
        
        if (totalPowerAll > 0) {
            for (Room room : home.getRooms()) {
                double roomPower = room.getTotalPowerConsumption();
                if (roomPower > 0) {
                    String label = String.format("%s (%.0fW)", room.getName(), roomPower);
                    energyByRoomChart.getData().add(new PieChart.Data(label, roomPower));
                }
            }
        } else {
            // Show placeholder when no devices are on
            energyByRoomChart.getData().add(new PieChart.Data("No active devices", 1));
        }
    }
    
    // Alarm Animation Methods
    private void startAlarmAnimation(String color) {
        if (alarmAnimation != null) {
            alarmAnimation.stop();
        }
        
        // Set initial armed state
        alarmIndicator.setFill(Color.web(color));
        alarmIndicator.setOpacity(1.0);
        
        // Create pulsing animation
        alarmAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, 
                new KeyValue(alarmIndicator.opacityProperty(), 1.0),
                new KeyValue(alarmIndicator.scaleXProperty(), 1.0),
                new KeyValue(alarmIndicator.scaleYProperty(), 1.0)
            ),
            new KeyFrame(Duration.seconds(0.8), 
                new KeyValue(alarmIndicator.opacityProperty(), 0.3),
                new KeyValue(alarmIndicator.scaleXProperty(), 1.3),
                new KeyValue(alarmIndicator.scaleYProperty(), 1.3)
            )
        );
        alarmAnimation.setCycleCount(Timeline.INDEFINITE);
        alarmAnimation.setAutoReverse(true);
        alarmAnimation.play();
        
        // Update quick actions panel with red border
        if (quickActionsPanel != null) {
            quickActionsPanel.setStyle("-fx-background-color: " + CARD_BG + "; " +
                "-fx-background-radius: 15; -fx-border-radius: 15; " +
                "-fx-border-color: " + DANGER + "; -fx-border-width: 3; " +
                "-fx-effect: dropshadow(gaussian, rgba(239, 68, 68, 0.4), 15, 0, 0, 0);");
        }
        
        System.out.println("🚨 ALARM ANIMATION STARTED - Security Armed");
    }
    
    private void stopAlarmAnimation() {
        if (alarmAnimation != null) {
            alarmAnimation.stop();
        }
        
        // Reset to disarmed state
        alarmIndicator.setFill(Color.web(SUCCESS));
        alarmIndicator.setOpacity(1.0);
        alarmIndicator.setScaleX(1.0);
        alarmIndicator.setScaleY(1.0);
        
        // Reset quick actions panel to normal
        if (quickActionsPanel != null) {
            quickActionsPanel.setStyle("-fx-background-color: " + CARD_BG + "; " +
                "-fx-background-radius: 15; -fx-border-radius: 15; " +
                "-fx-border-color: " + currentSceneColor + "; -fx-border-width: 2; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        }
        
        System.out.println("✅ ALARM ANIMATION STOPPED - Security Disarmed");
    }
    
    private void shutdown() {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }
    
    // Data Model Classes
    public static class DeviceData {
        private final SmartDevice device;
        
        public DeviceData(SmartDevice device) {
            this.device = device;
        }
        
        public String getId() { return device.getId(); }
        public String getName() { return device.getName(); }
        public String getType() { return device.getType(); }
        public String getRoom() { return device.getLocation(); }
        public String getStatus() { return device.getStatus(); }
        public double getPower() { return device.getCurrentPowerConsumption(); }
        public boolean isOn() { return device.isOn(); }
        
        public void turnOn() { device.turnOn(); }
        public void turnOff() { device.turnOff(); }
        public void toggle() { device.toggle(); }
    }
    
    public static class RoomData {
        private final Room room;
        
        public RoomData(Room room) {
            this.room = room;
        }
        
        public String getName() { return room.getName(); }
        public String getFloor() { return room.getFloor(); }
        public double getPower() { return room.getTotalPowerConsumption(); }
        public int getDeviceCount() { return room.getDevices().size(); }
        public int getActiveCount() { return room.getActiveDeviceCount(); }
        public double getTemperature() { return room.getCurrentTemperature(); }
    }
    
    // Static method to launch from external application
    public static void launchApp(Home h, AutomationEngine ae) {
        staticHome = h;
        staticEngine = ae;
        new Thread(() -> Application.launch(ModernSmartHomeDashboard.class)).start();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
