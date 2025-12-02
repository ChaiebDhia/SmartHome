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
**Purpose**: Real-time overview and quick actions

**Components**:
- **Live Statistics Cards**:
  - Total Power Consumption (Watts)
  - Hourly Energy Cost ($)
  - Active/Total Devices count
  - Security System status

- **Quick Actions Panel**:
  - **All Lights ON**: Turns on every light in all rooms
  - **All Lights OFF**: Turns off every light in all rooms
  - **All Devices ON**: Powers on all controllable devices
  - **All Devices OFF**: Shuts down all devices
  - **Arm Security**: Activates security system, locks doors, enables cameras
  - **Disarm Security**: Deactivates security features

- **Scene Buttons**:
  - **Morning**: Lights at 80%, blinds open, comfortable temperature
  - **Movie**: Dim lights (30%), close blinds, ambient mode
  - **Night**: All off, security armed, doors locked
  - **Away**: Energy-saving mode, full security enabled

- **Real-Time Charts**:
  - **Power Consumption Chart**: Line graph showing power usage over time
  - **Energy by Room Chart**: Pie chart displaying energy distribution across rooms

### 2. 🔌 Devices Tab
**Purpose**: Complete device management with search and CRUD operations

**Features**:

#### Search & Filter Bar
- **🔍 Search Field**: Type to search by device name, type, or ID
- **Type Filter**: Dropdown to filter by device category:
  - All Types
  - Light
  - Thermostat
  - Smart TV
  - Smart Plug
  - Smart Blinds
  - Security Camera
  - Door Lock
- **Room Filter**: Dropdown to filter devices by room location
- **Clear Button**: Reset all filters

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

#### Add New Device
1. Click **"+ Add Device"** button (top right)
2. Fill in the dialog:
   - **Device Name**: Custom name (e.g., "Kitchen Ceiling Light")
   - **Device Type**: Select from 7 types
   - **Room**: Choose installation location
   - **Power (W)**: Base power consumption
   - **Brightness** (Lights only): 0-100%
3. Click **OK** to create
4. Device appears immediately in all views

**How It Works**:
- New device is added to selected room
- Registered in home device registry
- Assigned unique UUID identifier
- Fully controllable via ON/OFF buttons
- Contributes to energy statistics

#### Remove Device
1. Click **Remove** button in device table
2. Confirmation dialog shows:
   - Device details
   - Warning about permanent deletion
3. Click **OK** to confirm
4. Device removed from:
   - Room device list
   - Home registry
   - All UI displays

**How It Works**:
- `Room.removeDevice()` removes from room
- Home registry updated
- All data bindings refresh automatically
- Energy calculations updated

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

**Features**:

#### Automation Guide Box
Quick reference showing:
- How triggers work
- Available actions
- Example automation rules
- Tips for effective automation

#### Active Rules List
Displays all automation rules with:
- **Rule Name**: Custom identifier
- **Status**: Enabled/Disabled with colored border
  - Green border = Active rule
  - Blue border = Disabled rule
- **Trigger**: Condition that activates the rule
- **Actions**: What happens when triggered
- **Last Fired**: Timestamp of last execution

#### Rule Actions
- **✏️ Edit**: Modify rule name, trigger, and actions
- **▶️ Test**: Manually trigger rule to verify behavior
- **🗑️ Delete**: Remove rule with confirmation
- **Toggle Switch**: Enable/disable rule

#### Create New Rule
1. Click **"+ Create New Rule"** button
2. Complete guided dialog:

**Step 1: Rule Name**
- Enter descriptive name (e.g., "Evening Lights", "Security Alert")

**Step 2: When to Trigger** (Select One)
- ⏰ **Time-based**: Specific time (HH:MM format)
- 🚶 **Motion detected**: When sensor detects movement
- 🌡️ **Temperature threshold**: When temp exceeds limit
- 🌙 **Dark condition**: When light level is low
- 🔄 **Always active**: Continuously active

**Step 3: What to Do** (Select Multiple)
- ☀️ Turn ON all lights
- 🌑 Turn OFF all lights
- 🔅 Dim all lights to 20%
- 🌡️ Set temperature to 22°C
- 🔒 Lock all doors
- 📹 Enable cameras
- 🔔 Trigger alarm

3. Click **Create Rule**
4. Rule appears in automation list immediately

**How Rules Work**:
- `AutomationEngine` evaluates triggers every 5 seconds
- When trigger condition is met:
  - All associated actions execute
  - "Last Fired" timestamp updates
  - Actions logged to console
- Disabled rules are skipped during evaluation

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

### How It Works

**Scheduled Updates** (Every 5 seconds):
```java
scheduler.scheduleAtFixedRate(() -> {
    automationEngine.tick();       // Evaluate automation rules
    refreshAllData();              // Update all statistics
    updateCharts();                // Refresh graphs
}, 0, 5, TimeUnit.SECONDS);
```

**What Gets Updated**:
1. **Total Power**: Sum of all active devices
2. **Hourly Cost**: Power × electricity rate
3. **Active Devices**: Count of ON devices
4. **Charts**: New data points added
5. **Device Status**: Current states
6. **Automation Rules**: Trigger evaluation

**JavaFX Property Bindings**:
- `DoubleProperty totalPower` → Dashboard power card
- `IntegerProperty activeDevices` → Device count card
- `BooleanProperty securityArmed` → Security indicator
- `ObservableList<DeviceData>` → Device table

Changes propagate automatically to all UI elements.

---

## 🎨 Particle Background Effect

### Visual Enhancement

**What It Does**:
- 30 floating blue particles animate across the background
- Creates depth and modern IoT aesthetic
- Subtle opacity (15-30%)
- Smooth floating motion

**How It Works**:
```java
addParticleBackground(root)
→ Creates 30 Circle nodes
→ Random size (2-6px), position, opacity
→ Timeline animations (15-25 second cycles)
→ Particles float upward and fade
→ Auto-restart on completion
```

**Technical Details**:
- Uses JavaFX `Timeline` with `KeyFrame`/`KeyValue`
- Particles move in Y-axis (upward drift)
- Slight X-axis variation for natural movement
- Color: `#3b82f6` (blue matching IoT theme)
- Non-interactive (`setMouseTransparent(true)`)

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
