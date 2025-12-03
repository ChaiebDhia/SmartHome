# 📘 Smart Home Automation Simulator - Complete User Guide

## 🚀 Quick Start Guide

### Prerequisites
- **Java 17** or higher
- **Maven 3.6+**
- **JavaFX 17.0.6** (included in dependencies)

### Installation & Running

#### Step 1: Clone the Repository
```bash
git clone https://github.com/ChaiebDhia/SmartHome.git
cd SmartHome
```

#### Step 2: Compile the Project
```bash
mvn clean compile
```

#### Step 3: Run the Application
```bash
mvn javafx:run
```

The application will launch with a professional IoT-themed dashboard.

---

## 🎯 Application Overview

The **Smart Home Automation Simulator** is a comprehensive Java-based application that demonstrates Object-Oriented Programming principles through a virtual smart home management system. It provides:

- Real-time device control and monitoring
- Automated rule-based actions
- Energy consumption tracking
- Security system management
- Scene-based home automation
- Multi-room device organization

---

## 🏠 Main Navigation

The application has 4 main tabs accessible via the navigation bar:

### 1. 🏠 Dashboard Tab
**Purpose**: Real-time overview, room summaries, and quick actions

**📁 Implementation File**: `src/main/java/com/smarthome/ui/javafx/ModernSmartHomeDashboard.java`

**New Features**:

#### 🎉 Welcome Message with Dynamic Jokes (Lines 566-616)
**How It Works**:
- Displays "🏠 Welcome to Your Smart Home! 🎉" with IoT-themed gradient border
- **10 rotating tech/IoT jokes** that change automatically every 8 seconds
- Uses JavaFX Timeline with fade transitions for smooth joke changes
- Jokes array contains 10 humorous messages about smart homes

**Implementation**:
```java
// Joke rotation with Timeline (Lines 598-610)
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
```

#### 🏘️ Room Summary Cards (Lines 618-635)
**Purpose**: Quick overview of each room with live statistics and direct access to management

**Features**:
- **Centered layout** with FlowPane alignment
- **Entrance animations**: Staggered fade-in and slide-up effects (100ms delay between cards)
- **Real-time dynamic updates**: Cards update instantly when devices are toggled
- Each card displays:
  - 📱 Device count (active/total) - **updates live**
  - ⚡ Power usage in watts - **updates live**
  - ✅/💤 Status (Active/Idle) - **updates live**
  - 🔧 "Manage Devices" button - links directly to Rooms Management tab
- **Hover animation**: Scales to 1.03x on mouse enter
- **Bluish IoT borders**: Gradient from #3b82f6 to #8b5cf6

**Implementation** (Lines 1926-2070):
```java
private VBox createRoomSummaryCard(Room room) {
    VBox card = new VBox(15);
    card.setUserData(room); // Store room reference
    
    // Update function for dynamic data
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
    updateCard.run(); // Initial update
    return card;
}
```

**Dynamic Updates** (Lines 3062-3071):
```java
// In refreshAllData() - updates all room cards
if (roomSummaryGrid != null) {
    for (javafx.scene.Node node : roomSummaryGrid.getChildren()) {
        if (node instanceof VBox) {
            VBox card = (VBox) node;
            Runnable updateCard = (Runnable) card.getProperties().get("updateCard");
            if (updateCard != null) {
                updateCard.run(); // Update card with latest data
            }
        }
    }
}
```

**Performance Optimizations**:
- Card reference caching via `roomSummaryGrid` field
- Property-based storage of update functions (no external maps)
- `Platform.runLater()` for thread-safe UI updates
- Efficient stream processing for device filtering

#### 🎬 Scene Indicator (Header - Lines 299-304)
**Purpose**: Shows current active scene in real-time

**Features**:
- Displays in header: "Normal Mode", "Morning Mode", "Movie Mode", "Night Mode"
- **Automatically updates** when scenes are changed via scene buttons
- Color-coded styling for each scene:
  - **Normal**: Light background with blue border
  - **Morning**: Yellow background with golden border and glow
  - **Movie**: Dark purple with violet border and glow
  - **Night**: Dark blue with cyan border and glow
- Visual feedback with drop shadow effects

**Implementation** (Lines 1233-1274):
```java
private void updateSceneTheme(String sceneName, String sceneName2, String color) {
    switch (sceneName.toLowerCase()) {
        case "normal":
        case "away":
            sceneIndicator.setText("Normal Mode");
            sceneIndicator.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95); -fx-text-fill: #1e3a8a; " +
                                   "-fx-background-radius: 12; -fx-border-color: #60a5fa; " +
                                   "-fx-border-radius: 12; -fx-border-width: 2; -fx-padding: 8 15;");
            break;
        case "morning":
            sceneIndicator.setText("Morning Mode");
            sceneIndicator.setStyle("-fx-background-color: #fef3c7; -fx-text-fill: #92400e; " +
                                   "-fx-background-radius: 12; -fx-border-color: #fbbf24; " +
                                   "-fx-border-radius: 12; -fx-border-width: 2; -fx-padding: 8 15; " +
                                   "-fx-effect: dropshadow(gaussian, rgba(251, 191, 36, 0.4), 8, 0, 0, 0);");
            break;
        case "movie":
            sceneIndicator.setText("Movie Mode");
            sceneIndicator.setStyle("-fx-background-color: #4c1d95; -fx-text-fill: #fdf4ff; " +
                                   "-fx-background-radius: 12; -fx-border-color: #a78bfa; " +
                                   "-fx-border-radius: 12; -fx-border-width: 2; -fx-padding: 8 15; " +
                                   "-fx-effect: dropshadow(gaussian, rgba(124, 58, 237, 0.4), 8, 0, 0, 0);");
            break;
        case "night":
            sceneIndicator.setText("Night Mode");
            sceneIndicator.setStyle("-fx-background-color: #0f172a; -fx-text-fill: #67e8f9; " +
                                   "-fx-background-radius: 12; -fx-border-color: #06b6d4; " +
                                   "-fx-border-radius: 12; -fx-border-width: 2; -fx-padding: 8 15; " +
                                   "-fx-effect: dropshadow(gaussian, rgba(6, 182, 212, 0.4), 8, 0, 0, 0);");
            break;
    }
}
```

**Components**:
- **Live Statistics Cards** (Header):
  - Total Power Consumption (Watts)
  - Active/Total Devices count
  - Hourly Energy Cost ($)

**📍 Implementation Details**:

#### Live Statistics Cards (Lines 410-550)
**How It Works**:
- Uses JavaFX `DoubleProperty` and `IntegerProperty` for reactive data binding
- Properties automatically update UI when values change
- Created in `createStatCard()` method (Lines 835-870)

```java
// Property declarations (Lines 68-72)
private DoubleProperty totalPower = new SimpleDoubleProperty(0);
private DoubleProperty hourlyCost = new SimpleDoubleProperty(0);
private IntegerProperty activeDevices = new SimpleIntegerProperty(0);
private IntegerProperty totalDevices = new SimpleIntegerProperty(0);
private BooleanProperty securityArmed = new SimpleBooleanProperty(false);

// Updated every 5 seconds in refreshAllData() (Lines 2458-2476)
totalPower.set(home.getTotalPowerConsumption());
hourlyCost.set(home.getEstimatedHourlyCost());
activeDevices.set(home.getTotalActiveDevices());
totalDevices.set(home.getAllDevices().size());
securityArmed.set(home.isSecuritySystemArmed());
```

**Technical Flow**:
1. Scheduler calls `refreshAllData()` every 5 seconds (Line 262)
2. Properties get new values from `Home` model
3. JavaFX binding automatically updates UI labels
4. No manual UI refresh needed (Observer pattern)

- **Quick Actions Panel**:

#### 💡 All Lights ON Button (Lines 620-635)
**Implementation**:
```java
allLightsOnBtn = createActionButton("💡 All Lights ON", () -> {
    int lightCount = 0;
    for (Room room : home.getRooms()) {
        for (SmartDevice device : room.getDevices()) {
            if (device instanceof SmartLight) {
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
```
**What Happens**:
1. Iterates through all rooms and devices
2. Checks if device is instance of `SmartLight`
3. Calls `device.turnOn()` for each light
4. Calls `home.turnOnAllLights()` for bulk operation
5. Shows success alert with light count and power consumption
6. Calls `refreshAllData()` to update UI

#### 💡 All Lights OFF Button (Lines 637-650)
**Implementation**: Similar to ON but calls `device.turnOff()` and calculates power saved
**Power Calculation**: `60.0W * lightCount` (estimates 60W per light)

#### 🔌 All Devices ON Button (Lines 652-665)
**Implementation**:
```java
allDevicesOnBtn = createActionButton("🔌 All Devices ON", () -> {
    int deviceCount = 0;
    for (SmartDevice device : home.getAllDevices()) {
        if (!device.isOn()) {
            device.turnOn();
            deviceCount++;
        }
    }
    refreshAllData();
}, SUCCESS);
```
**What Happens**:
1. Iterates `home.getAllDevices()` (returns List<SmartDevice>)
2. Checks `device.isOn()` state to avoid double-turning-on
3. Calls `device.turnOn()` using polymorphism
4. Works with all 7 device types (Light, Thermostat, TV, Plug, Blinds, Camera, Lock)

#### 🔌 All Devices OFF Button (Lines 667-680)
**Implementation**:
```java
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
```
**What Happens**:
1. Captures power consumption before shutdown
2. Calls `home.turnOffAllDevices()` (bulk operation)
3. Calculates power saved
4. Stops alarm animation if security was armed
5. Updates all UI elements

#### 🛡️ Arm Security Button (Lines 682-701)
**File**: Uses `SecurityService.java` (src/main/java/com/smarthome/service/SecurityService.java)
**Implementation**:
```java
armSecurityBtn = createActionButton("🛡️ Arm Security", () -> {
    securityService.arm();
    int cameraCount = 0;
    for (SmartDevice device : home.getAllDevices()) {
        if (device instanceof SecurityCamera) {
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
```
**What Happens**:
1. Calls `securityService.arm()` which:
   - Locks all DoorLock devices
   - Enables all SecurityCamera devices
   - Arms motion sensors
   - Sets home security state to true
2. Counts active cameras
3. Sets `securityArmed` property to true
4. Starts pulsing red alarm animation (Lines 2541-2560)
5. Updates all UI

**Alarm Animation** (Lines 2541-2560):
```java
private void startAlarmAnimation(String color) {
    if (alarmIndicator == null || alarmAnimation != null) return;
    
    alarmAnimation = new Timeline(
        new KeyFrame(Duration.ZERO, 
            new KeyValue(alarmIndicator.opacityProperty(), 1.0)),
        new KeyFrame(Duration.seconds(0.5), 
            new KeyValue(alarmIndicator.opacityProperty(), 0.3))
    );
    alarmAnimation.setCycleCount(Timeline.INDEFINITE);
    alarmAnimation.setAutoReverse(true);
    alarmAnimation.play();
}
```

#### 🔓 Disarm Security Button (Lines 703-720)
**Implementation**: Calls `securityService.disarm()`, stops alarm animation, sets `securityArmed` to false

- **Scene Buttons**: (Lines 595-615)

**📁 Scene Logic File**: `src/main/java/com/smarthome/service/SceneManager.java`

#### Scene Implementation Details:

Each scene button created by `createSceneButton()` method (Lines 724-765) with:
- Hover animations (scale 1.05x)
- Color coding (Morning=Green, Movie=Blue, Night=Purple, Away=Orange)
- Tooltip descriptions
- Action handler that calls `sceneManager.apply(sceneName)`

**🌅 Morning Scene** (SceneManager.java, Line ~50):
```java
public void applyMorning() {
    // Set all lights to 80% brightness
    for (SmartDevice device : home.getAllDevices()) {
        if (device instanceof SmartLight) {
            SmartLight light = (SmartLight) device;
            light.turnOn();
            light.setBrightness(80);
        }
        if (device instanceof SmartBlinds) {
            SmartBlinds blinds = (SmartBlinds) device;
            blinds.setPosition(100); // Fully open
        }
        if (device instanceof Thermostat) {
            Thermostat thermo = (Thermostat) device;
            thermo.setTargetTemperature(22.0);
        }
    }
}
```
**What Happens**: Lights→80%, Blinds→100% open, Temp→22°C

**🎬 Movie Scene** (SceneManager.java, Line ~80):
```java
public void applyMovie() {
    // Dim lights to 30%
    for (SmartDevice device : home.getAllDevices()) {
        if (device instanceof SmartLight) {
            SmartLight light = (SmartLight) device;
            light.turnOn();
            light.setBrightness(30); // Dim for cinema
        }
        if (device instanceof SmartBlinds) {
            SmartBlinds blinds = (SmartBlinds) device;
            blinds.setPosition(0); // Fully closed
        }
    }
}
```
**What Happens**: Lights→30% (dim), Blinds→0% (closed), Creates theater atmosphere

**🌙 Night Scene** (SceneManager.java, Line ~110):
```java
public void applyNight() {
    // Turn everything off
    home.turnOffAllDevices();
    
    // Arm security
    securityService.arm();
    
    // Lock all doors
    for (SmartDevice device : home.getAllDevices()) {
        if (device instanceof DoorLock) {
            DoorLock lock = (DoorLock) device;
            lock.lock();
        }
    }
}
```
**What Happens**: All devices OFF, Security ARMED, All doors LOCKED

**🚗 Away Scene** (SceneManager.java, Line ~140):
```java
public void applyAway() {
    // Turn off unnecessary devices
    for (SmartDevice device : home.getAllDevices()) {
        if (!(device instanceof SecurityCamera) && 
            !(device instanceof DoorLock)) {
            device.turnOff();
        }
    }
    
    // Full security mode
    securityService.arm();
}
```
**What Happens**: Only cameras and locks remain on, Energy-saving mode, Full security

- **Real-Time Charts**:
  - **Power Consumption Chart**: Line graph showing power usage over time
  - **Energy by Room Chart**: Pie chart displaying energy distribution across rooms

### 2. 🔌 Devices Tab
**Purpose**: Complete device management with search and CRUD operations

**📁 Implementation File**: `src/main/java/com/smarthome/ui/javafx/ModernSmartHomeDashboard.java` (Lines 1140-1450)

**Features**:

#### Search & Filter Bar (Lines 1189-1244)

**📍 Implementation Details**:

**Search Bar Creation**:
```java
// Search bar components (Lines 1189-1220)
HBox searchBar = new HBox(10);
TextField searchField = new TextField();
searchField.setPromptText("Search by name, type, or ID...");
searchField.setPrefWidth(400);

ComboBox<String> typeFilter = new ComboBox<>();
typeFilter.getItems().addAll("All Types", "Light", "Thermostat", "Smart TV", 
                             "Smart Plug", "Smart Blinds", "Security Camera", "Door Lock");
typeFilter.setValue("All Types");

ComboBox<String> roomFilter = new ComboBox<>();
roomFilter.getItems().add("All Rooms");
for (Room room : home.getRooms()) {
    roomFilter.getItems().add(room.getName());
}
roomFilter.setValue("All Rooms");

Button clearBtn = new Button("Clear");
```

**Live Filtering Listeners** (Lines 1232-1240):
```java
// Real-time search as you type
searchField.textProperty().addListener((obs, old, newVal) -> 
    filterDeviceTable(newVal, typeFilter.getValue(), roomFilter.getValue()));

typeFilter.valueProperty().addListener((obs, old, newVal) -> 
    filterDeviceTable(searchField.getText(), newVal, roomFilter.getValue()));

roomFilter.valueProperty().addListener((obs, old, newVal) -> 
    filterDeviceTable(searchField.getText(), typeFilter.getValue(), newVal));
```

**How It Works**:
1. Each component has a `ValueProperty` listener
2. Any change triggers `filterDeviceTable()` immediately
3. All three filters work simultaneously (AND logic)
4. Results update live as you type (no "Search" button needed)

**Filter Logic** (Lines 1246-1272):
```java
private void filterDeviceTable(String searchText, String typeFilter, String roomFilter) {
    // Save current filter state (important for auto-refresh persistence)
    this.currentSearchText = searchText != null ? searchText : "";
    this.currentTypeFilter = typeFilter != null ? typeFilter : "All Types";
    this.currentRoomFilter = roomFilter != null ? roomFilter : "All Rooms";
    
    ObservableList<DeviceData> filtered = FXCollections.observableArrayList();
    
    for (SmartDevice device : home.getAllDevices()) {
        // Multi-criteria matching
        boolean matchesSearch = searchText == null || searchText.isEmpty() || 
            device.getName().toLowerCase().contains(searchText.toLowerCase()) ||  // Name
            device.getType().toLowerCase().contains(searchText.toLowerCase()) ||  // Type
            device.getId().toLowerCase().contains(searchText.toLowerCase());      // UUID
        
        boolean matchesType = typeFilter.equals("All Types") || 
            device.getType().equalsIgnoreCase(typeFilter);
        
        boolean matchesRoom = roomFilter.equals("All Rooms") || 
            device.getLocation().equalsIgnoreCase(roomFilter);
        
        // All conditions must be true (AND logic)
        if (matchesSearch && matchesType && matchesRoom) {
            filtered.add(new DeviceData(device));
        }
    }
    
    // Update observable list - TableView auto-refreshes
    deviceData.setAll(filtered);
}
```

**Filter Persistence During Auto-Refresh** (Lines 90-92, 2467):
**Problem Solved**: Originally, every 5-second refresh cleared filters and showed all devices
**Solution**: 
```java
// Instance variables store current filter state (Lines 90-92)
private String currentSearchText = "";
private String currentTypeFilter = "All Types";
private String currentRoomFilter = "All Rooms";

// refreshAllData() reapplies filters instead of clearing (Line 2467)
private void refreshAllData() {
    // ... update properties ...
    
    // OLD CODE (caused bug): deviceData.clear(); for(...) deviceData.add(...)
    // NEW CODE: Reapply current filters
    filterDeviceTable(currentSearchText, currentTypeFilter, currentRoomFilter);
}
```
**Result**: Search filters now persist through automatic 5-second refreshes!

- **🔍 Search Field**: Type to search by device name, type, or ID
  - **Name Search**: "Light" matches "Main Light", "Kitchen Light", etc.
  - **Type Search**: "Thermostat" shows all thermostats
  - **ID Search**: Paste UUID for exact device lookup
  - **Case-insensitive**: "LIGHT" = "light" = "Light"
  
- **Type Filter**: Dropdown to filter by device category:
  - All Types, Light, Thermostat, Smart TV, Smart Plug, Smart Blinds, Security Camera, Door Lock
  
- **Room Filter**: Dropdown to filter devices by room location
  - Dynamically populated from `home.getRooms()`
  
- **Clear Button**: Resets all filters (Lines 1223-1229)
  ```java
  clearBtn.setOnAction(e -> {
      searchField.clear();
      typeFilter.setValue("All Types");
      roomFilter.setValue("All Rooms");
      filterDeviceTable("", "All Types", "All Rooms");
  });
  ```

#### Device Table Columns
| Column | Description |
|--------|-------------|
| Device | Device name |
| Type | Device category |
| Room | Location in home |
| Status | Current state and details |
| Power (W) | Current power consumption |
| Actions | Control buttons |

#### Device Actions
- **ON Button** (Green): Turn device on
- **OFF Button** (Red): Turn device off
- **Remove Button** (Gray): Delete device with confirmation

#### Add New Device (Lines 1274-1343)

**📍 Implementation Details**:

**Dialog Creation**:
```java
private void showAddDeviceDialog() {
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Add New Device");
    dialog.setHeaderText("Create a new smart device");
    
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    
    // Form fields
    TextField nameField = new TextField();
    nameField.setPromptText("Device name");
    
    ComboBox<String> typeCombo = new ComboBox<>();
    typeCombo.getItems().addAll("Light", "Thermostat", "Smart TV", "Smart Plug", 
                                "Smart Blinds", "Security Camera", "Door Lock");
    
    ComboBox<String> roomCombo = new ComboBox<>();
    for (Room room : home.getRooms()) {
        roomCombo.getItems().add(room.getName());
    }
    
    TextField powerField = new TextField("50");
    powerField.setPromptText("Power consumption (W)");
    
    // Grid layout
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
}
```

**User Flow**:
1. Click **"+ Add Device"** button (top right, Line 1181)
2. Fill in the dialog:
   - **Device Name**: Custom name (e.g., "Kitchen Ceiling Light")
   - **Device Type**: Select from 7 types
   - **Room**: Choose installation location
   - **Power (W)**: Base power consumption (default: 50W)
3. Click **OK** to create
4. Device appears immediately in all views

**Validation & Creation** (Lines 1323-1342):
```java
dialog.showAndWait().ifPresent(response -> {
    if (response == ButtonType.OK) {
        String name = nameField.getText().trim();
        String type = typeCombo.getValue();
        String roomName = roomCombo.getValue();
        
        // Validation
        if (name.isEmpty() || type == null || roomName == null) {
            showAlert("Invalid Input", "Please fill in all required fields", 
                     Alert.AlertType.ERROR);
            return;
        }
        
        Room room = home.getRoom(roomName);
        if (room == null) {
            showAlert("Error", "Room not found", Alert.AlertType.ERROR);
            return;
        }
        
        try {
            // Create device
            SmartDevice newDevice = createDeviceByType(type, name, roomName, powerField);
            room.addDevice(newDevice);        // Add to room
            home.registerDevice(newDevice);   // Add to home registry
            refreshAllData();                 // Update UI
            showEnhancedAlert("Success", 
                "Device '" + name + "' added to " + roomName + "!", 
                Alert.AlertType.INFORMATION);
        } catch (Exception ex) {
            showAlert("Error", "Failed to create device: " + ex.getMessage(), 
                     Alert.AlertType.ERROR);
        }
    }
});
```

**Device Creation by Type** (Lines 1345-1379):
```java
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
```

**What Happens**:
1. Device created with type-specific constructor
2. Light: Creates with 100% brightness default (removed brightness field for simplicity)
3. Thermostat: Creates with 22°C default temperature
4. Smart Plug: Connects device with specified power rating
5. Device assigned unique UUID automatically (in SmartDevice constructor)
6. Added to room's device list via `room.addDevice()`
7. Registered in home's global device registry via `home.registerDevice()`
8. `refreshAllData()` updates all UI elements
9. Device immediately appears in device table, room cards, and charts
10. Power consumption calculations automatically include new device

**Related Files**:
- `src/main/java/com/smarthome/model/SmartDevice.java` - Base class with UUID generation
- `src/main/java/com/smarthome/model/devices/*.java` - All 7 device implementations
- `src/main/java/com/smarthome/model/Room.java` - `addDevice()` method
- `src/main/java/com/smarthome/model/Home.java` - `registerDevice()` method

#### Remove Device (Lines 1902-1948)

**📍 Implementation Details**:

**Confirmation Dialog** (Lines 1902-1926):
```java
private void showRemoveDeviceConfirmation(SmartDevice device) {
    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
    confirmation.setTitle("Remove Device");
    confirmation.setHeaderText("Are you sure you want to remove this device?");
    confirmation.setContentText("Device: " + device.getName() + "\n" +
                               "Type: " + device.getType() + "\n" +
                               "Location: " + device.getLocation() + "\n\n" +
                               "This action cannot be undone.");
    
    // Style buttons
    DialogPane dialogPane = confirmation.getDialogPane();
    dialogPane.lookupButton(ButtonType.OK).setStyle(
        "-fx-background-color: #ef4444; -fx-text-fill: white; " +
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
```

**Removal Logic** (Lines 1928-1948):
```java
private void removeDevice(SmartDevice device) {
    // Find room containing the device
    Room room = home.getRoom(device.getLocation());
    if (room != null) {
        // Remove from room
        boolean removed = room.removeDevice(device);
        if (removed) {
            // Remove from home registry
            home.getAllDevices().remove(device);
            
            // Refresh UI
            refreshAllData();
            
            showEnhancedAlert("Device Removed", 
                "Device '" + device.getName() + "' has been removed from " + 
                device.getLocation() + ".\n\n" +
                "Remaining devices in room: " + room.getDevices().size(),
                Alert.AlertType.INFORMATION);
        } else {
            showAlert("Error", "Failed to remove device from room", 
                     Alert.AlertType.ERROR);
        }
    } else {
        showAlert("Error", "Room not found: " + device.getLocation(), 
                 Alert.AlertType.ERROR);
    }
}
```

**User Flow**:
1. Click **Remove** button in device table (gray button in Actions column)
2. Confirmation dialog shows:
   - Device name, type, location
   - Warning: "This action cannot be undone"
   - Red OK button, Gray Cancel button
3. Click **OK** to confirm
4. Device removed from:
   - Room's device list (`room.removeDevice()`)
   - Home's global registry (`home.getAllDevices().remove()`)
   - All UI displays (table, room cards, charts)
5. Success message shows remaining device count

**What Happens**:
1. Finds room by `device.getLocation()`
2. Calls `room.removeDevice(device)` - removes from room's ArrayList
3. Calls `home.getAllDevices().remove(device)` - removes from home's registry
4. `refreshAllData()` updates all UI elements:
   - Device table refreshes (device disappears)
   - Room cards update (device removed from room)
   - Statistics recalculate (power consumption decreases)
   - Charts update (energy distribution changes)
5. Observable lists automatically trigger UI updates (no manual refresh needed)

**Related Files**:
- `src/main/java/com/smarthome/model/Room.java` - `removeDevice()` method (uses ArrayList.remove())
- `src/main/java/com/smarthome/model/Home.java` - Device registry management

### 3. 🚪 Rooms Tab
**Purpose**: Room-by-room device management

**Features**:
- **Room Cards**: Each room displayed as an IoT-styled card with:
  - Room name and icon
  - Device list with status indicators
  - Pulsing green dots for active devices
  - Toggle buttons for each device

**How Device Toggle Works**:
1. Click **"Turn ON"** or **"Turn OFF"** button
2. Device state changes immediately
3. Button updates with fade animation
4. Status indicator changes color (green = on, gray = off)
5. Power consumption updates in real-time
6. All statistics refresh automatically

**Card Animations**:
- Hover effect: Card scales up (1.02x)
- Gradient borders with blue-purple theme
- Drop shadow effects for depth
- Pulsing animation on active devices

### 4. ⚙️ Automation Tab
**Purpose**: Create and manage automation rules

**📁 Implementation Files**: 
- Main UI: `src/main/java/com/smarthome/ui/javafx/ModernSmartHomeDashboard.java` (Lines 1752-2100)
- Rule Engine: `src/main/java/com/smarthome/controller/AutomationEngine.java`
- Rule Classes: `src/main/java/com/smarthome/automation/` (Rule.java, Trigger.java, Action.java, Condition.java)
- Built-in Rules: `src/main/java/com/smarthome/automation/builtins/`

**Features**:

#### Automation Guide Box (Lines 1770-1820)
**Implementation**:
```java
VBox guideBox = new VBox(10);
guideBox.setPadding(new Insets(15));
guideBox.setStyle(
    "-fx-background-color: #eff6ff; " +
    "-fx-border-color: #3b82f6; " +
    "-fx-border-width: 1; " +
    "-fx-border-radius: 8; " +
    "-fx-background-radius: 8;"
);

Label guideTitle = new Label("💡 Automation Guide");
guideTitle.setFont(Font.font("System", FontWeight.BOLD, 14));

Label guideText = new Label(
    "• Create rules with IF-THEN logic\n" +
    "• Triggers: Time, Motion, Temperature, Device State\n" +
    "• Actions: Control lights, locks, cameras, etc.\n" +
    "• Example: 'IF motion detected THEN turn on lights'\n" +
    "• Rules evaluated every 5 seconds\n" +
    "• Test rules before enabling"
);
guideText.setFont(Font.font(12));
guideText.setStyle("-fx-text-fill: #1e40af;");

guideBox.getChildren().addAll(guideTitle, guideText);
```
Quick reference showing how triggers work, available actions, example rules, and tips.

#### Active Rules List (Lines 1822-1920)
**Implementation**:
```java
FlowPane rulesContainer = new FlowPane();
rulesContainer.setHgap(15);
rulesContainer.setVgap(15);

for (Rule rule : automationEngine.getRules()) {
    VBox ruleCard = createRuleCard(rule);
    rulesContainer.getChildren().add(ruleCard);
}

private VBox createRuleCard(Rule rule) {
    VBox card = new VBox(12);
    card.setPrefWidth(320);
    card.setPadding(new Insets(15));
    
    String borderColor = rule.isEnabled() ? SUCCESS : PRIMARY;
    card.setStyle(
        "-fx-background-color: white; " +
        "-fx-background-radius: 10; " +
        "-fx-border-color: " + borderColor + "; " +
        "-fx-border-width: 2; " +
        "-fx-border-radius: 10; " +
        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);"
    );
    
    // Rule header
    HBox header = new HBox(10);
    header.setAlignment(Pos.CENTER_LEFT);
    
    Label nameLabel = new Label(rule.getName());
    nameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
    
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    
    // Enable/Disable toggle
    ToggleButton toggleBtn = new ToggleButton();
    toggleBtn.setSelected(rule.isEnabled());
    toggleBtn.setText(rule.isEnabled() ? "ON" : "OFF");
    toggleBtn.setStyle(
        "-fx-background-color: " + (rule.isEnabled() ? SUCCESS : "#94a3b8") + "; " +
        "-fx-text-fill: white; " +
        "-fx-font-size: 10px; " +
        "-fx-font-weight: bold;"
    );
    
    toggleBtn.setOnAction(e -> {
        rule.setEnabled(toggleBtn.isSelected());
        toggleBtn.setText(rule.isEnabled() ? "ON" : "OFF");
        toggleBtn.setStyle(
            "-fx-background-color: " + (rule.isEnabled() ? SUCCESS : "#94a3b8") + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 10px; " +
            "-fx-font-weight: bold;"
        );
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: " + (rule.isEnabled() ? SUCCESS : PRIMARY) + "; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);"
        );
    });
    
    header.getChildren().addAll(nameLabel, spacer, toggleBtn);
    
    // Trigger info
    Label triggerLabel = new Label("⏰ Trigger: " + rule.getTrigger().getDescription());
    triggerLabel.setFont(Font.font(12));
    triggerLabel.setWrapText(true);
    
    // Actions info
    Label actionsLabel = new Label("⚡ Actions: " + rule.getActions().size() + " action(s)");
    actionsLabel.setFont(Font.font(12));
    
    // Last fired timestamp
    Label lastFiredLabel = new Label("🕒 Last Fired: " + 
        (rule.getLastFired() != null ? rule.getLastFired().toString() : "Never"));
    lastFiredLabel.setFont(Font.font(11));
    lastFiredLabel.setStyle("-fx-text-fill: " + TEXT_SECONDARY + ";");
    
    // Action buttons
    HBox actionButtons = new HBox(8);
    
    Button editBtn = new Button("✏️ Edit");
    Button testBtn = new Button("▶️ Test");
    Button deleteBtn = new Button("🗑️ Delete");
    
    editBtn.setOnAction(e -> showEditRuleDialog(rule));
    testBtn.setOnAction(e -> testRule(rule));
    deleteBtn.setOnAction(e -> deleteRule(rule));
    
    actionButtons.getChildren().addAll(editBtn, testBtn, deleteBtn);
    
    card.getChildren().addAll(header, triggerLabel, actionsLabel, lastFiredLabel, actionButtons);
    return card;
}
```

Displays all automation rules with:
- **Rule Name**: Custom identifier (Lines 1840-1842)
- **Status**: Enabled/Disabled with colored border (Lines 1828-1837)
  - Green border = Active rule (SUCCESS color #10b981)
  - Blue border = Disabled rule (PRIMARY color #3b82f6)
- **Trigger**: Condition that activates the rule (Lines 1890-1892)
- **Actions**: What happens when triggered (Lines 1894-1895)
- **Last Fired**: Timestamp of last execution (Lines 1897-1899)

#### Rule Actions (Lines 1901-1920)
- **✏️ Edit**: Modify rule name, trigger, and actions (calls `showEditRuleDialog()`)
- **▶️ Test**: Manually trigger rule to verify behavior (calls `testRule()`)
- **🗑️ Delete**: Remove rule with confirmation (calls `deleteRule()`)
- **Toggle Switch**: Enable/disable rule (Lines 1850-1875)
  - Updates rule state via `rule.setEnabled()`
  - Changes button text ("ON" / "OFF")
  - Updates button color (green / gray)
  - Updates card border color
  - No page refresh needed (JavaFX property binding)

**Rule Toggle Implementation** (Lines 1850-1875):
```java
toggleBtn.setOnAction(e -> {
    rule.setEnabled(toggleBtn.isSelected());
    toggleBtn.setText(rule.isEnabled() ? "ON" : "OFF");
    toggleBtn.setStyle(
        "-fx-background-color: " + (rule.isEnabled() ? SUCCESS : "#94a3b8") + "; " +
        "-fx-text-fill: white;"
    );
    card.setStyle(
        "-fx-border-color: " + (rule.isEnabled() ? SUCCESS : PRIMARY) + "; " +
        /* ... other styles ... */
    );
});
```

#### Create New Rule (Lines 1960-2100)

**📍 Implementation Details**:

**Dialog Creation** (Lines 1960-2050):
```java
private void showAddRuleDialog() {
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Create New Automation Rule");
    dialog.setHeaderText("🎯 Guided Rule Creation");
    
    VBox content = new VBox(15);
    content.setPadding(new Insets(20));
    content.setPrefWidth(600);
    
    // STEP 1: Rule Name
    Label step1Label = new Label("STEP 1: Rule Name");
    step1Label.setFont(Font.font("System", FontWeight.BOLD, 14));
    step1Label.setStyle("-fx-text-fill: " + PRIMARY + ";");
    
    TextField nameField = new TextField();
    nameField.setPromptText("e.g., Evening Lights, Security Alert, etc.");
    nameField.setStyle("-fx-font-size: 13px; -fx-padding: 8;");
    
    // STEP 2: Trigger Type Selection
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
    
    RadioButton darkRB = new RadioButton("🌙 Dark Condition - When light level is low");
    darkRB.setToggleGroup(triggerGroup);
    darkRB.setUserData("dark");
    
    triggerBox.getChildren().addAll(timeRB, motionRB, tempRB, darkRB);
    
    // Time settings (shown when time-based selected)
    HBox timeSettings = new HBox(10);
    timeSettings.setAlignment(Pos.CENTER_LEFT);
    Label timeLabel = new Label("Time:");
    TextField timeField = new TextField("07:00");
    timeField.setPromptText("HH:MM");
    timeField.setPrefWidth(100);
    timeSettings.getChildren().addAll(timeLabel, timeField);
    timeSettings.setVisible(true);
    
    // Show/hide time field based on trigger selection
    triggerGroup.selectedToggleProperty().addListener((obs, old, newVal) -> {
        String triggerType = (String) newVal.getUserData();
        timeSettings.setVisible("time".equals(triggerType));
    });
    
    // STEP 3: Actions Selection (Multiple)
    Label step3Label = new Label("3️⃣ What to Do (Select Multiple)");
    step3Label.setFont(Font.font("System", FontWeight.BOLD, 14));
    
    VBox actionsBox = new VBox(8);
    
    CheckBox lightsOnCB = new CheckBox("☀️ Turn ON all lights");
    CheckBox lightsOffCB = new CheckBox("🌑 Turn OFF all lights");
    CheckBox dimLightsCB = new CheckBox("🔅 Dim all lights to 20%");
    CheckBox setTempCB = new CheckBox("🌡️ Set temperature to 22°C");
    CheckBox lockDoorsCB = new CheckBox("🔒 Lock all doors");
    CheckBox enableCamerasCB = new CheckBox("📹 Enable cameras");
    CheckBox alarmCB = new CheckBox("🔔 Trigger alarm");
    
    actionsBox.getChildren().addAll(lightsOnCB, lightsOffCB, dimLightsCB, 
                                     setTempCB, lockDoorsCB, enableCamerasCB, alarmCB);
    
    content.getChildren().addAll(step1Label, nameField, 
                                  step2Label, triggerBox, timeSettings,
                                  step3Label, actionsBox);
    
    dialog.getDialogPane().setContent(content);
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
}
```

**User Flow**:
1. Click **"+ Create New Rule"** button (top right of Automation tab)
2. Complete guided dialog:

**Step 1: Rule Name** (Lines 1970-1978)
- Enter descriptive name (e.g., "Evening Lights", "Security Alert")
- TextField with prompt text for examples

**Step 2: When to Trigger** (Select One) (Lines 1980-2020)
- ⏰ **Time-based**: Specific time (HH:MM format)
  - Shows time input field when selected
  - Default: "07:00"
  - Validates format HH:MM
- 🚶 **Motion detected**: When sensor detects movement
  - Uses `MotionSensor.isMotionDetected()`
- 🌡️ **Temperature threshold**: When temp exceeds limit
  - Checks `TemperatureSensor.getCurrentReading()`
- 🌙 **Dark condition**: When light level is low
  - Uses `LightSensor.getCurrentReading() < 100`

**Step 3: What to Do** (Select Multiple) (Lines 2022-2040)
- ☀️ Turn ON all lights - Calls `home.turnOnAllLights()`
- 🌑 Turn OFF all lights - Calls `home.turnOffAllDevices()` for lights
- 🔅 Dim all lights to 20% - Sets `light.setBrightness(20)` for all SmartLight devices
- 🌡️ Set temperature to 22°C - Sets `thermostat.setTargetTemperature(22.0)`
- 🔒 Lock all doors - Calls `doorLock.lock()` for all DoorLock devices
- 📹 Enable cameras - Turns on all SecurityCamera devices
- 🔔 Trigger alarm - Activates security service alarm

3. Click **Create Rule**
4. Rule appears in automation list immediately

**Rule Creation Logic** (Lines 2050-2100):
```java
dialog.showAndWait().ifPresent(response -> {
    if (response == ButtonType.OK) {
        String ruleName = nameField.getText().trim();
        if (ruleName.isEmpty()) {
            showAlert("Error", "Please enter a rule name", Alert.AlertType.ERROR);
            return;
        }
        
        // Get selected trigger
        Toggle selectedTrigger = triggerGroup.getSelectedToggle();
        String triggerType = (String) selectedTrigger.getUserData();
        
        Trigger trigger = null;
        switch (triggerType) {
            case "time":
                String time = timeField.getText();
                trigger = new Trigger() {
                    public boolean evaluate(Context ctx) {
                        LocalTime now = LocalTime.now();
                        LocalTime target = LocalTime.parse(time);
                        return now.getHour() == target.getHour() && 
                               now.getMinute() == target.getMinute();
                    }
                    public String getDescription() {
                        return "Time: " + time;
                    }
                };
                break;
            case "motion":
                trigger = new Trigger() {
                    public boolean evaluate(Context ctx) {
                        for (Room room : home.getRooms()) {
                            for (Sensor sensor : room.getSensors()) {
                                if (sensor instanceof MotionSensor) {
                                    MotionSensor ms = (MotionSensor) sensor;
                                    if (ms.isMotionDetected()) {
                                        return true;
                                    }
                                }
                            }
                        }
                        return false;
                    }
                    public String getDescription() {
                        return "Motion detected";
                    }
                };
                break;
            // ... other trigger types ...
        }
        
        // Create rule with trigger
        Rule rule = new Rule(ruleName, trigger);
        
        // Add selected actions
        if (lightsOnCB.isSelected()) {
            rule.addAction(ctx -> home.turnOnAllLights());
        }
        if (lightsOffCB.isSelected()) {
            rule.addAction(ctx -> {
                for (SmartDevice device : home.getAllDevices()) {
                    if (device instanceof SmartLight) {
                        device.turnOff();
                    }
                }
            });
        }
        if (dimLightsCB.isSelected()) {
            rule.addAction(ctx -> {
                for (SmartDevice device : home.getAllDevices()) {
                    if (device instanceof SmartLight) {
                        SmartLight light = (SmartLight) device;
                        light.turnOn();
                        light.setBrightness(20);
                    }
                }
            });
        }
        // ... other actions ...
        
        // Add rule to automation engine
        automationEngine.addRule(rule);
        
        // Refresh UI
        refreshAutomationTab();
        
        showEnhancedAlert("Rule Created", 
            "Automation rule '" + ruleName + "' has been created and is now active!",
            Alert.AlertType.INFORMATION);
    }
});
```

**How Rules Work** (AutomationEngine.java, Lines 40-80):

**Rule Evaluation** (Every 5 seconds):
```java
public void tick() {
    Context context = new Context(home);
    
    for (Rule rule : rules) {
        if (!rule.isEnabled()) {
            continue; // Skip disabled rules
        }
        
        try {
            // Evaluate trigger condition
            if (rule.getTrigger().evaluate(context)) {
                // Execute all actions
                for (Action action : rule.getActions()) {
                    action.execute(context);
                }
                
                // Update last fired timestamp
                rule.setLastFired(LocalDateTime.now());
                
                // Log execution
                System.out.println("[Rule] " + rule.getName() + " fired at " + 
                                 LocalDateTime.now());
            }
        } catch (Exception e) {
            System.err.println("Error executing rule: " + rule.getName());
            e.printStackTrace();
        }
    }
}
```

**Scheduler Setup** (ModernSmartHomeDashboard.java, Lines 260-270):
```java
scheduler = Executors.newScheduledThreadPool(1);
scheduler.scheduleAtFixedRate(() -> {
    Platform.runLater(() -> {
        automationEngine.tick();  // Evaluate all rules
        refreshAllData();         // Update UI
    });
}, 0, 5, TimeUnit.SECONDS);  // Run every 5 seconds
```

**What Happens**:
- `AutomationEngine.tick()` called every 5 seconds
- Iterates through all rules in `rules` list
- Skips disabled rules (`rule.isEnabled() == false`)
- Evaluates trigger condition via `trigger.evaluate(context)`
- If trigger returns true:
  1. Executes all associated actions sequentially
  2. Updates "Last Fired" timestamp to `LocalDateTime.now()`
  3. Logs execution to console with timestamp
  4. UI automatically updates via `refreshAllData()`
- Disabled rules are skipped during evaluation
- Errors caught and logged without crashing engine

**Related Files**:
- `src/main/java/com/smarthome/automation/Rule.java` - Rule data structure
- `src/main/java/com/smarthome/automation/Trigger.java` - Trigger interface
- `src/main/java/com/smarthome/automation/Action.java` - Action interface
- `src/main/java/com/smarthome/automation/Context.java` - Execution context
- `src/main/java/com/smarthome/automation/builtins/` - Pre-built triggers and actions

#### Edit Existing Rule
1. Click **✏️ Edit** button on any rule
2. Dialog shows current settings pre-filled
3. Modify any field:
   - Change rule name
   - Switch trigger type
   - Add/remove actions
4. Click **Save Changes**
5. Old rule removed, new rule created with updates

**Implementation**:
- Rules use `Trigger`, `Condition`, and `Action` interfaces
- Built-in implementations in `automation.builtins` package
- Custom triggers/actions can be added
- Context object passed during evaluation

---

## 🔍 Search Functionality (Devices Tab)

### How Search Works

#### By Name
- Type "Light" → Shows all devices with "Light" in name
- Case-insensitive matching
- Partial match supported

#### By Type
- Use Type Filter dropdown
- Categories: Light, Thermostat, Smart TV, Smart Plug, Smart Blinds, Security Camera, Door Lock
- Combines with text search

#### By Room
- Use Room Filter dropdown
- Shows devices only in selected room
- Combines with text and type filters

#### By ID
- Type device UUID in search field
- Exact match on device identifier
- Useful for technical debugging

### Live Filtering
- Results update as you type
- All filters work simultaneously
- Empty search shows all devices
- "Clear" button resets everything

**Technical Implementation**:
```java
filterDeviceTable(searchText, typeFilter, roomFilter)
→ Checks each device against all criteria
→ Updates ObservableList<DeviceData>
→ TableView refreshes automatically
```

---

## ⚡ Real-Time Updates & Data Binding

**📁 Implementation File**: `src/main/java/com/smarthome/ui/javafx/ModernSmartHomeDashboard.java`

### How It Works

**Scheduled Updates** (Every 5 seconds) (Lines 260-270):
```java
scheduler = Executors.newScheduledThreadPool(1);
scheduler.scheduleAtFixedRate(() -> {
    Platform.runLater(() -> {
        automationEngine.tick();       // Evaluate automation rules
        refreshAllData();              // Update all statistics
        simulateSensorChanges();       // Update sensor readings
    });
}, 0, 5, TimeUnit.SECONDS);
```

**Implementation Details**:
- Uses `ScheduledExecutorService` from `java.util.concurrent`
- Single thread pool dedicated to scheduled tasks
- `Platform.runLater()` ensures UI updates happen on JavaFX Application Thread
- Initial delay: 0 seconds (runs immediately on startup)
- Period: 5 seconds (repeats every 5 seconds continuously)
- Runs until application closes (`scheduler.shutdown()` on exit)

**What Gets Updated**:

**1. refreshAllData() Method** (Lines 2458-2476):
```java
private void refreshAllData() {
    // Update properties (Lines 2459-2464)
    totalPower.set(home.getTotalPowerConsumption());
    hourlyCost.set(home.getEstimatedHourlyCost());
    activeDevices.set(home.getTotalActiveDevices());
    totalDevices.set(home.getAllDevices().size());
    securityArmed.set(home.isSecuritySystemArmed());
    
    // Update device data with current filters applied (Line 2467)
    filterDeviceTable(currentSearchText, currentTypeFilter, currentRoomFilter);
    
    // Update room data (Lines 2469-2473)
    roomData.clear();
    for (Room room : home.getRooms()) {
        roomData.add(new RoomData(room));
    }
    
    // Update charts with real-time data (Lines 2475-2476)
    updatePowerChart();
    updateEnergyPieChart();
}
```

**What Each Line Does**:
- **Line 2460**: `totalPower.set()` - Calls `home.getTotalPowerConsumption()` which sums power from all active devices
- **Line 2461**: `hourlyCost.set()` - Calls `home.getEstimatedHourlyCost()` which calculates `totalPower * electricityRate * (1/1000)`
- **Line 2462**: `activeDevices.set()` - Calls `home.getTotalActiveDevices()` which counts devices where `isOn() == true`
- **Line 2463**: `totalDevices.set()` - Returns size of `home.getAllDevices()` list
- **Line 2464**: `securityArmed.set()` - Returns `home.isSecuritySystemArmed()` boolean
- **Line 2467**: `filterDeviceTable()` - Reapplies current filters to device table (preserves search state!)
- **Lines 2469-2473**: Clears and rebuilds room data for Rooms tab
- **Lines 2475-2476**: Updates both power consumption chart and energy pie chart

**2. Total Power Calculation** (Home.java):
```java
public double getTotalPowerConsumption() {
    double total = 0;
    for (SmartDevice device : getAllDevices()) {
        if (device.isOn()) {
            total += device.getCurrentPowerConsumption();
        }
    }
    return total;
}
```
- Iterates all devices
- Only counts active devices (`isOn() == true`)
- Calls polymorphic `getCurrentPowerConsumption()` for each device type

**3. Hourly Cost Calculation** (Home.java):
```java
public double getEstimatedHourlyCost() {
    double totalPowerKW = getTotalPowerConsumption() / 1000.0; // Convert W to kW
    return totalPowerKW * electricityRate; // Rate per kWh
}
```
- Converts watts to kilowatts
- Multiplies by electricity rate (default: $0.12/kWh)
- Returns cost per hour

**4. Active Devices Count** (Home.java):
```java
public int getTotalActiveDevices() {
    int count = 0;
    for (SmartDevice device : getAllDevices()) {
        if (device.isOn()) {
            count++;
        }
    }
    return count;
}
```

**5. Chart Updates** (Lines 2478-2535):

**Power Consumption Chart** (Lines 2478-2503):
```java
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
```
**What Happens**:
- Clears existing data points
- Adds new data point for each room showing current power consumption
- Adds "Total" data point at the end
- Chart automatically animates to new values
- X-axis: Room names + "Total"
- Y-axis: Power in Watts

**Energy Pie Chart** (Lines 2505-2535):
```java
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
```
**What Happens**:
- Clears existing pie slices
- Calculates power consumption for each room
- Only shows rooms with active devices (power > 0)
- Labels show room name and wattage: "Living Room (350W)"
- Pie chart automatically calculates percentages
- Shows placeholder if no devices are active

**6. Automation Rules Evaluation** (AutomationEngine.java):
```java
public void tick() {
    Context context = new Context(home);
    
    for (Rule rule : rules) {
        if (!rule.isEnabled()) continue;
        
        if (rule.getTrigger().evaluate(context)) {
            for (Action action : rule.getActions()) {
                action.execute(context);
            }
            rule.setLastFired(LocalDateTime.now());
        }
    }
}
```
- Evaluates all enabled rules every 5 seconds
- Executes actions when trigger conditions are met
- Updates "Last Fired" timestamp

**JavaFX Property Bindings** (Lines 68-72):
```java
// Property declarations
private DoubleProperty totalPower = new SimpleDoubleProperty(0);
private DoubleProperty hourlyCost = new SimpleDoubleProperty(0);
private IntegerProperty activeDevices = new SimpleIntegerProperty(0);
private IntegerProperty totalDevices = new SimpleIntegerProperty(0);
private BooleanProperty securityArmed = new SimpleBooleanProperty(false);
```

**How Property Bindings Work**:
- `DoubleProperty totalPower` → Dashboard power card label (Lines 420-430)
  - Binding: `label.textProperty().bind(totalPower.asString("%.0fW"))`
  - When `totalPower.set()` is called, label automatically updates
  
- `IntegerProperty activeDevices` → Device count card (Lines 445-455)
  - Binding: `label.textProperty().bind(activeDevices.asString())`
  - Updates automatically when count changes
  
- `BooleanProperty securityArmed` → Security indicator (Lines 460-470)
  - Binding: `indicator.fillProperty().bind(Bindings.when(securityArmed).then(Color.RED).otherwise(Color.GREEN))`
  - Circle color changes automatically (red=armed, green=disarmed)
  
- `ObservableList<DeviceData>` → Device table (Lines 1390-1420)
  - Binding: `table.setItems(deviceData)`
  - Table rows automatically add/remove/update when list changes
  - No manual refresh needed

**Observable Pattern Implementation**:
1. Properties store data with built-in change notification
2. UI components bind to properties
3. When property value changes, all bound components automatically update
4. No manual UI refresh code needed
5. Thread-safe updates via `Platform.runLater()`

**Data Flow Diagram**:
```
Timer (5 seconds)
    ↓
Platform.runLater()
    ↓
automationEngine.tick() → Evaluate rules → Execute actions
    ↓
refreshAllData()
    ↓
home.getTotalPowerConsumption() → Calculate power
    ↓
totalPower.set(value) → Property update
    ↓
JavaFX Binding → Automatic UI update
    ↓
User sees updated values (no page refresh!)
```

Changes propagate automatically to all UI elements through JavaFX property bindings and observable lists.

---

## 🎨 Particle Background Effect

**📁 Implementation File**: `src/main/java/com/smarthome/ui/javafx/ModernSmartHomeDashboard.java` (Lines 2563-2610)

### Visual Enhancement

**What It Does**:
- 30 floating blue particles animate across the background
- Creates depth and modern IoT aesthetic
- Subtle opacity (15-30%)
- Smooth floating motion
- Continuous animation loop

**📍 Implementation Details**:

**Particle Creation** (Lines 2563-2610):
```java
private void addParticleBackground(Pane container) {
    // Create 30 particles
    for (int i = 0; i < 30; i++) {
        Circle particle = new Circle();
        
        // Random size between 2-6 pixels
        particle.setRadius(2 + Math.random() * 4);
        
        // IoT blue color
        particle.setFill(Color.web("#3b82f6"));
        
        // Random opacity between 0.15 and 0.30 (subtle)
        particle.setOpacity(0.15 + Math.random() * 0.15);
        
        // Random starting position
        particle.setTranslateX(Math.random() * 1400); // X position
        particle.setTranslateY(Math.random() * 900);  // Y position
        
        // Make non-interactive (don't block clicks)
        particle.setMouseTransparent(true);
        
        // Add to container (behind all other elements)
        container.getChildren().add(0, particle); // Index 0 = background layer
        
        // Create animation
        Timeline animation = new Timeline();
        
        // Random animation duration (15-25 seconds)
        double duration = 15 + Math.random() * 10;
        
        // Keyframes for smooth animation
        KeyFrame start = new KeyFrame(
            Duration.ZERO,
            new KeyValue(particle.translateYProperty(), particle.getTranslateY()),
            new KeyValue(particle.translateXProperty(), particle.getTranslateX()),
            new KeyValue(particle.opacityProperty(), particle.getOpacity())
        );
        
        // End position: float upward, slight horizontal drift
        KeyFrame end = new KeyFrame(
            Duration.seconds(duration),
            new KeyValue(particle.translateYProperty(), -50), // Float upward
            new KeyValue(particle.translateXProperty(), 
                        particle.getTranslateX() + (Math.random() - 0.5) * 100), // Drift
            new KeyValue(particle.opacityProperty(), 0) // Fade out
        );
        
        animation.getKeyFrames().addAll(start, end);
        
        // Loop animation infinitely
        animation.setCycleCount(Timeline.INDEFINITE);
        
        // Restart from bottom when reaching top
        animation.setOnFinished(e -> {
            particle.setTranslateY(900 + Math.random() * 100); // Respawn at bottom
            particle.setOpacity(0.15 + Math.random() * 0.15);  // New opacity
            animation.playFromStart();
        });
        
        // Start animation with random delay (stagger effect)
        animation.setDelay(Duration.seconds(Math.random() * duration));
        animation.play();
    }
}
```

**How It Works**:
1. **Particle Creation Loop** (30 iterations):
   - Creates `Circle` node with radius 2-6px
   - Sets fill color to `#3b82f6` (IoT blue theme)
   - Random opacity 0.15-0.30 for subtle effect
   - Random starting X position (0-1400px)
   - Random starting Y position (0-900px)
   - `setMouseTransparent(true)` allows clicks to pass through
   - Added to container at index 0 (background layer, behind all content)

2. **Animation Setup**:
   - `Timeline` with two keyframes (start and end)
   - Random duration 15-25 seconds for variety
   - **Start KeyFrame** (Duration.ZERO):
     - Current Y position
     - Current X position  
     - Current opacity
   - **End KeyFrame** (Duration.seconds(duration)):
     - Y position: -50 (above screen, floating upward)
     - X position: Drifts left or right by ±50px for natural movement
     - Opacity: 0 (fades out completely)

3. **Animation Properties**:
   - `setCycleCount(Timeline.INDEFINITE)` - Loops forever
   - `setOnFinished()` - When particle reaches top:
     - Respawn at bottom (Y = 900-1000px)
     - Assign new random opacity
     - Restart animation (`playFromStart()`)
   - Random delay (0 to duration seconds) creates staggered effect
   - All particles animate simultaneously but at different phases

**Technical Details**:
- **JavaFX Components**: `Timeline`, `KeyFrame`, `KeyValue`, `Circle`
- **Properties Animated**:
  - `translateYProperty()` - Vertical movement (upward drift)
  - `translateXProperty()` - Horizontal movement (natural sway)
  - `opacityProperty()` - Fade in/out effect
- **Color**: `#3b82f6` - Professional blue matching IoT/tech theme
- **Layer Management**: Added at index 0 ensures particles stay behind all UI elements
- **Performance**: Lightweight - only 30 small circles with simple animations
- **Non-Interactive**: `setMouseTransparent(true)` prevents interference with UI controls

**Animation Math**:
- **Y-axis**: Moves from random starting position (0-900) to -50 (above screen)
  - Total distance: ~900-950px upward
  - Speed: 900px / 15-25 seconds = 36-60 px/second
- **X-axis**: Drifts ±50px for natural floating effect
  - Simulates air currents
- **Opacity**: Fades from 0.15-0.30 to 0.0
  - Creates ethereal disappearing effect

**Visual Effect Achieved**:
- ✅ Depth perception (layered background)
- ✅ Modern IoT aesthetic (tech-forward design)
- ✅ Subtle animation (not distracting)
- ✅ Continuous motion (living interface)
- ✅ Professional appearance (clean and polished)

**Related Styling**:
- Main container background: Gradient from `#f0f9ff` to `#e0f2fe` (light blue)
- Particles blend naturally with gradient background
- Consistent color scheme throughout dashboard

---

## 🏗️ Architecture

### Physical Architecture

```
User Interface (JavaFX)
       ↓
Controller Layer (HomeController, AutomationEngine)
       ↓
Service Layer (SceneManager, SecurityService, EnergyMonitor)
       ↓
Model Layer (Home → Rooms → Devices, Sensors)
       ↓
Data Persistence (JSON save/load)
```

### Logical Architecture

**Model-View-Controller (MVC) Pattern**:

**Model**:
- `SmartDevice` (abstract base class)
- Device implementations (SmartLight, Thermostat, etc.)
- `Room`, `Home` (composition)
- `Sensor` hierarchy

**View**:
- `ModernSmartHomeDashboard` (JavaFX UI)
- Charts, tables, cards
- Data binding with Observable properties

**Controller**:
- `HomeController` (device operations)
- `AutomationEngine` (rule evaluation)
- `SceneManager` (scene orchestration)
- `SecurityService` (security logic)

### Key Design Patterns

1. **Abstract Factory**: Device creation
2. **Observer**: JavaFX property bindings
3. **Strategy**: Automation triggers/actions
4. **Singleton**: Home instance
5. **Composite**: Home → Rooms → Devices

---

## 🎯 OOP Concepts Demonstrated

### 1. Abstraction
```java
public abstract class SmartDevice {
    public abstract double getCurrentPowerConsumption();
    public abstract String getStatus();
}
```

### 2. Inheritance
```java
SmartLight extends SmartDevice
Thermostat extends SmartDevice
// 7 device types total
```

### 3. Polymorphism
```java
for (SmartDevice device : home.getAllDevices()) {
    device.turnOn();  // Calls overridden method
}
```

### 4. Encapsulation
```java
private double brightness;
public void setBrightness(int value) {
    if (value < 0 || value > 100) throw IllegalArgumentException();
    this.brightness = value;
}
```

### 5. Interfaces
```java
public interface Controllable {
    void turnOn();
    void turnOff();
}

public interface EnergyConsumer {
    double getCurrentPowerConsumption();
}
```

### 6. Composition
```java
Home has-many Rooms
Room has-many Devices
Room has-many Sensors
```

---

## 📋 Requirements Validation

### Functional Requirements ✅

| Requirement | Implementation | Status |
|-------------|----------------|--------|
| Smart devices with basic operations | turnOn(), turnOff(), getStatus() | ✅ Complete |
| Home/Room structure | Home → Rooms → Devices | ✅ Complete |
| Add/remove devices | GUI dialog + Remove button | ✅ Complete |
| Search by ID | Text search includes UUID matching | ✅ Complete |
| Search by type | Type filter dropdown | ✅ Complete |
| Search by name | Live text search | ✅ Complete |
| Central controller | HomeController + AutomationEngine | ✅ Complete |
| Device listing | Devices tab with table view | ✅ Complete |
| Execute actions | Quick actions panel | ✅ Complete |
| Scheduled tasks | Scheduler with 5-second tick | ✅ Complete |
| Automation rules | IF-THEN logic with triggers/actions | ✅ Complete |

### Non-Functional Requirements ✅

| Requirement | Implementation | Status |
|-------------|----------------|--------|
| Modular code | Package structure, separation of concerns | ✅ Complete |
| Readable | Clear naming, comments, documentation | ✅ Complete |
| OOP best practices | All principles demonstrated | ✅ Complete |
| Exception handling | DeviceNotFoundException, validation | ✅ Complete |
| Collections framework | ArrayList, HashMap, ObservableList | ✅ Complete |
| JavaFX GUI | Professional IoT-themed dashboard | ✅ Complete |
| Energy dashboard | Real-time charts and statistics | ✅ Complete |

### OOP Requirements ✅

| Requirement | Implementation | Status |
|-------------|----------------|--------|
| Abstract class with 2+ methods | SmartDevice (getCurrentPowerConsumption, getStatus) | ✅ Complete |
| 3+ concrete subclasses | 7 device types (Light, Thermostat, TV, Plug, Blinds, Camera, Lock) | ✅ Complete |
| Controllable interface | turnOn(), turnOff(), toggle() | ✅ Complete |
| EnergyConsumer interface | getCurrentPowerConsumption() | ✅ Complete |
| Schedulable interface | schedule(time, action) | ✅ Complete |
| Inheritance & polymorphism | Method overriding throughout | ✅ Complete |
| Custom exception | DeviceNotFoundException | ✅ Complete |
| Collections usage | ArrayList, HashMap extensively used | ✅ Complete |

---

## 🔧 Technical Details

### Device Types

1. **SmartLight**
   - Brightness control (0-100%)
   - Color temperature (2700K-6500K)
   - RGB color setting
   - Power: 10W × (brightness/100)

2. **Thermostat**
   - Target temperature setting
   - Heating/Cooling/Auto modes
   - Fan speed control
   - Power: 1500W (heat) / 2000W (cool)

3. **SmartTV**
   - Volume control (0-100)
   - Channel navigation
   - App streaming
   - Power: 70W idle + 20W streaming

4. **SmartPlug**
   - Connected device tracking
   - Energy usage monitoring
   - Schedule support
   - Power: 0.3W + connected device power

5. **SmartBlinds**
   - Position control (0-100%)
   - Open/Close/Stop commands
   - Power: 15W when moving

6. **SecurityCamera**
   - Motion detection
   - Recording status
   - Resolution settings
   - Power: 8W

7. **DoorLock**
   - Lock/Unlock commands
   - Access log
   - Power: 2W

### Sensors

1. **TemperatureSensor**: Reads ambient temperature
2. **MotionSensor**: Detects movement
3. **LightSensor**: Measures luminosity
4. **HumiditySensor**: Tracks humidity levels

### Scene Automation

**Morning Scene**:
```java
- All lights → 80% brightness
- Blinds → 100% open
- Thermostat → 22°C
```

**Movie Scene**:
```java
- Lights → 30% (dim)
- Blinds → 0% (closed)
- Ambient mode activated
```

**Night Scene**:
```java
- All devices → OFF
- Security → ARMED
- Doors → LOCKED
- Cameras → ENABLED
```

**Away Scene**:
```java
- Energy-saving mode
- Security → FULL ALERT
- Unnecessary devices → OFF
```

---

## 📊 Data Flow

### Device Control Flow
```
User clicks button
    ↓
Event handler triggered
    ↓
device.turnOn() / device.turnOff()
    ↓
Device state changes
    ↓
lastUpdated timestamp set
    ↓
refreshAllData() called
    ↓
ObservableList updated
    ↓
UI automatically refreshes
    ↓
Charts update
    ↓
Statistics recalculated
```

### Automation Rule Execution
```
Scheduler tick (every 5 seconds)
    ↓
AutomationEngine.tick()
    ↓
For each enabled rule:
    ↓
Evaluate trigger condition
    ↓
If true → Execute all actions
    ↓
Update "Last Fired" timestamp
    ↓
Log execution to console
```

---

## 🎬 Demo Scenario

### Complete Workflow Example

**Scenario**: "Morning Routine Setup"

1. **Initial State**: All devices OFF
2. **Add New Device**:
   - Click "+ Add Device"
   - Name: "Coffee Maker Light"
   - Type: Light
   - Room: Kitchen
   - Brightness: 100%
   - Click OK
3. **Search Device**:
   - Type "Coffee" in search
   - Device appears in results
   - Clear search
4. **Create Automation Rule**:
   - Click "+ Create New Rule"
   - Name: "Morning Coffee"
   - Trigger: Time-based → 07:00
   - Actions: Turn ON all lights, Set temperature 22°C
   - Click Create
5. **Test Rule**:
   - Click "▶️ Test" on "Morning Coffee" rule
   - All lights turn ON
   - Success notification appears
6. **Apply Scene**:
   - Click "Morning" scene button
   - Lights set to 80%
   - Blinds open
   - Thermostat adjusts
7. **Monitor Energy**:
   - View power consumption chart
   - Check energy by room pie chart
   - Total power displayed
8. **Manual Control**:
   - Go to Rooms tab
   - Toggle individual devices
   - See real-time updates
9. **Security**:
   - Click "Arm Security"
   - All doors lock
   - Cameras enable
   - Security indicator updates
10. **Remove Device**:
    - Go to Devices tab
    - Click "Remove" on test device
    - Confirm deletion
    - Device removed from all views

---

## 🐛 Troubleshooting

### Application Won't Start
```bash
# Check Java version
java -version  # Should be 17+

# Clean and rebuild
mvn clean install

# Run with debug
mvn javafx:run -X
```

### Devices Not Responding
- Check if device is connected (`isConnected()`)
- Verify device is added to a room
- Ensure room is added to home

### Rules Not Firing
- Check if rule is enabled (toggle switch)
- Verify trigger condition
- Look at console logs for errors
- Test rule manually first

### Charts Not Updating
- Scheduler should run every 5 seconds
- Check console for errors
- Verify data bindings are intact

---

## 📚 Code Examples

### Add Device Programmatically
```java
SmartLight light = new SmartLight("My Light", "Living Room");
Room room = home.getRoom("Living Room");
room.addDevice(light);
home.registerDevice(light);
```

### Create Custom Automation Rule
```java
Trigger motionTrigger = new Trigger() {
    public boolean evaluate(Context ctx) {
        return motionSensor.isMotionDetected();
    }
};

Action lightAction = ctx -> {
    light.turnOn();
    light.setBrightness(80);
};

Rule rule = new Rule("Motion Light", motionTrigger);
rule.addAction(lightAction);
automationEngine.addRule(rule);
```

### Search Devices
```java
// By ID
SmartDevice device = home.getDeviceById("uuid-here");

// By name
SmartDevice device = home.getDeviceByName("Main Light");

// By type
List<SmartDevice> lights = home.getDevicesByType(SmartLight.class);
```

---

## 🎓 Educational Value

This project demonstrates:

1. **OOP Fundamentals**: All 4 pillars implemented
2. **Design Patterns**: Factory, Observer, Strategy, Composite
3. **Java Collections**: ArrayList, HashMap, ObservableList
4. **JavaFX**: Modern UI development
5. **Event-Driven Programming**: Button handlers, property listeners
6. **Multithreading**: ScheduledExecutorService
7. **Exception Handling**: Custom exceptions, validation
8. **Code Organization**: Package structure, separation of concerns
9. **Data Binding**: Observable properties, automatic UI updates
10. **Real-World Application**: Practical IoT simulation

---

## 📞 Support

For issues or questions:
- Check console output for error messages
- Review this guide for functionality explanations
- Verify all prerequisites are installed
- Ensure Java 17+ and Maven 3.6+ are available

---

## 🎉 Conclusion

The Smart Home Automation Simulator is a complete, production-quality application demonstrating advanced OOP concepts in Java. Every requirement has been implemented with professional-grade code, modern UI design, and comprehensive functionality.

**Key Achievements**:
✅ All functional requirements met
✅ All OOP requirements demonstrated
✅ Professional GUI with IoT aesthetic
✅ Real-time data binding and updates
✅ Complete CRUD operations for devices
✅ Advanced search and filtering
✅ Automation rule engine
✅ Energy monitoring and analytics
✅ Security system integration
✅ Scene-based automation

**Ready for presentation, demonstration, and submission!** 🚀
