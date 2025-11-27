# 🏠 Smart Home Automation Simulator

An advanced, object-oriented Smart Home Automation System built in Java that simulates IoT devices, automation rules, energy monitoring, and security features.

## 🌟 Features
 - **Smart TV Added**: Volume, channel, app streaming state
- **Real-Time Monitoring**: Track device status, energy consumption, and sensor data
- **State Persistence**: Save and load home configurations
 - **Scheduler**: Time-of-day tasks (e.g., morning heating at 06:00)
 - **Swing GUI**: Lightweight dashboard for visual control
- **Temperature Control**: Smart thermostat with learning capabilities
- **Voice Command Simulation**: Simulate voice-controlled operations

- Abstract base class `SmartDevice` (abstract methods: `getCurrentPowerConsumption()`, `getStatus()`).
- Concrete subclasses: `SmartLight`, `Thermostat`, `SecurityCamera`, `DoorLock`, `SmartPlug`, `SmartBlinds`, `SmartTV`.
- Interfaces: `Controllable`, `EnergyConsumer`, `Schedulable` (available for extension), plus functional automation interfaces.
- Collections: `ArrayList`, `HashMap` used for room/device management and rule storage.
- Custom exception: `DeviceNotFoundException` thrown on invalid lookups.
- Polymorphism and inheritance throughout device hierarchy and automation actions.
- Encapsulation via getters/setters; search by name and ID supported.
## 📁 Project Structure

```
To launch the Swing dashboard instead of CLI, modify `SmartHomeApplication` to call:
```java
// After home setup
com.smarthome.ui.SwingDashboard.launch(home);
```
Or create an alternate main. Example temporary run:
```powershell
java -cp target\smart-home-simulator-1.0-SNAPSHOT.jar com.smarthome.SmartHomeApplication gui
```
Then inside `SmartHomeApplication` check `args` for "gui" and launch the dashboard.

GUI Features:
- Table view of all devices with status and power draw.
- Controls: ON / OFF / TOGGLE by name.
- Auto-refresh button.
SmartHome/
├── src/
Additional rule: `MotionLight-<room>` – Motion after 18:00 triggers lights at 70%.
│   ├── main/
 - **Interactive CLI**: Rich set of commands for controlling devices, scenes, persistence
`Scheduler` executes queued tasks once their time passes (e.g., 06:00 heating). Add tasks via `scheduler.add(LocalTime, description, runnable)`.
 - **JSON Persistence**: Export / import full home state
│   │   │           │   └── Room.java
- Invalid device lookups throw `DeviceNotFoundException`.
- Parameter validation (brightness, temperature, volume, positions) uses `IllegalArgumentException`.
│   │   │           ├── controller/
 Start the app and type `help` to view commands. Supported:

 - `status` – Full home status dashboard
 - `rooms` – Summary of rooms
 - `devices [room]` – List all devices or those in a room
 - `add device <type> <room> <name>` – Add device (types: light, thermostat, camera, lock, plug, blinds)
 - `on|off|toggle <deviceName>` – Power control
 - `lights <room> <brightness>` – Set brightness for all lights in room
 - `thermo set <deviceName> <temp>` – Adjust thermostat target temperature
 - `arm` / `disarm` – Control security system
 - `motion <room>` – Simulate motion (triggers security when armed)
 - `scene <away|movie|night|morning>` – Apply predefined home scene
 - `energy` – Show cumulative energy usage (kWh)
 - `save <file.json>` – Persist current home structure and states
 - `load <file.json>` – Load previously saved home state
 - `exit` – Quit the simulator
│   │   │           ├── service/
│   │   │           │   ├── EnergyMonitor.java
│   │   │           │   ├── SecurityService.java
│   │   │           │   └── NotificationService.java
│   │   │           ├── util/
│   │   │           │   ├── DataPersistence.java
 5. **Energy Tracking**: Periodically run `energy` to monitor use before applying conservation changes
│   │   │           │   └── Logger.java
│   │   │           └── ui/
│   │   │               └── ConsoleInterface.java

 Sample included rule:
 `Evening Lights` – After 18:00, if Living Room is dark, turn lights on to 60%.
│   └── test/
│       └── java/
│           └── com/
│               └── smarthome/

 Run tests:
 ```powershell
 mvn test
 ```

 Run without tests (faster iteration):
 ```powershell
 mvn -DskipTests=true clean package
 ```

 Execute application:
 ```powershell
 java -jar target\smart-home-simulator-1.0-SNAPSHOT.jar
 ```

 Save current home configuration:
 ```text
 save home.json
 ```
 Reload later:
 ```text
 load home.json
 ```
## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Building the Project
```bash
mvn clean compile
```

### Running the Application
```bash
mvn exec:java -Dexec.mainClass="com.smarthome.SmartHomeApplication"
```

Or build and run the JAR:
```bash
mvn clean package
java -jar target/smart-home-simulator-1.0-SNAPSHOT.jar
```

### Running Tests
```bash
mvn test
```

## 💡 Usage Examples

### CLI Commands
- `add device <type> <room> <name>` - Add a new device
- `control <device> <action>` - Control a device
- `create rule <name>` - Create automation rule
- `show status` - Display all devices and their status
- `energy report` - View energy consumption
- `activate scene <name>` - Activate a predefined scene
- `security status` - Check security system status

### Example Scenarios
1. **Morning Routine**: Lights gradually turn on, blinds open, coffee maker starts
2. **Away Mode**: All lights off, locks engaged, cameras active, thermostat lowered
3. **Movie Mode**: Lights dimmed, TV on, blinds closed
4. **Night Mode**: Security armed, outdoor lights on, indoor lights off

## 🏗️ Architecture

### Design Patterns Used
- **Strategy Pattern**: Device control strategies
- **Observer Pattern**: Event-driven device communication
- **Factory Pattern**: Device creation
- **Singleton Pattern**: Home controller instance
- **Command Pattern**: Automation actions
- **State Pattern**: Device states

### Key Components
- **Device Hierarchy**: Abstract `SmartDevice` base class with concrete implementations
- **Event System**: Asynchronous event handling between components
- **Automation Engine**: Rule evaluation and execution
- **Energy Monitor**: Power consumption tracking and analytics
- **Security Service**: Intrusion detection and alerts

## 🔧 Configuration

Default home configuration can be saved/loaded via JSON using the `save` and `load` commands (no bundled default config file in resources).

## 📊 Advanced Features Explained

### Automation Rules
Rules consist of:
- **Triggers**: Events that start rule evaluation (time, sensor, device state)
- **Conditions**: Requirements that must be met (temperature, time range, device status)
- **Actions**: Operations to perform when triggered and conditions met

### Energy Monitoring
- Real-time power consumption tracking
- Historical data analysis
- Cost calculation
- Peak usage identification
- Energy-saving recommendations

### Security System
- Motion detection simulation
- Door/window sensors
- Camera recording triggers
- Intrusion alerts
- System arming/disarming

## 🧪 Testing

The project includes comprehensive unit tests for:
- Device operations
- Automation rules
- Energy calculations
- Event handling
- Data persistence

## 📝 License

This project is created for educational purposes.

## 👥 Author

Smart Home Automation Simulator - Advanced Java Project

---
Built with ☕ and Java
