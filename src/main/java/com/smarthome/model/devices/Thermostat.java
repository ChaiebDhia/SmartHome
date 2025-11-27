package com.smarthome.model.devices;

import com.smarthome.model.SmartDevice;

/**
 * Smart Thermostat with heating/cooling modes and scheduling
 */
public class Thermostat extends SmartDevice {
    private double targetTemperature; // Celsius
    private double currentTemperature; // Celsius
    private ThermostatMode mode;
    private FanSpeed fanSpeed;
    private static final double HEATING_POWER = 1500.0; // Watts
    private static final double COOLING_POWER = 2000.0; // Watts
    private static final double FAN_POWER = 50.0; // Watts

    public enum ThermostatMode {
        OFF, HEAT, COOL, AUTO, FAN_ONLY
    }

    public enum FanSpeed {
        LOW, MEDIUM, HIGH, AUTO
    }

    public Thermostat(String name, String location, double initialTemp) {
        super(name, "Thermostat", location);
        this.currentTemperature = initialTemp;
        this.targetTemperature = 22.0; // Default comfortable temperature
        this.mode = ThermostatMode.OFF;
        this.fanSpeed = FanSpeed.AUTO;
    }

    @Override
    public void turnOn() {
        super.turnOn();
        this.mode = ThermostatMode.AUTO;
        System.out.println(name + " turned ON in AUTO mode");
    }

    @Override
    public void turnOff() {
        super.turnOff();
        this.mode = ThermostatMode.OFF;
        System.out.println(name + " turned OFF");
    }

    public void setTargetTemperature(double temperature) {
        if (temperature < 15.0 || temperature > 30.0) {
            throw new IllegalArgumentException("Temperature must be between 15°C and 30°C");
        }
        this.targetTemperature = temperature;
        this.lastUpdated = java.time.LocalDateTime.now();
        System.out.printf("%s target temperature set to %.1f°C%n", name, temperature);
        adjustMode();
    }

    public void setMode(ThermostatMode mode) {
        this.mode = mode;
        if (mode != ThermostatMode.OFF) {
            this.isOn = true;
        }
        System.out.println(name + " mode set to " + mode);
    }

    public void setFanSpeed(FanSpeed speed) {
        this.fanSpeed = speed;
        System.out.println(name + " fan speed set to " + speed);
    }

    /**
     * Simulate temperature change over time
     */
    public void updateTemperature(double ambient) {
        if (!isOn || mode == ThermostatMode.OFF) {
            // Temperature drifts towards ambient
            double drift = (ambient - currentTemperature) * 0.1;
            currentTemperature += drift;
        } else {
            // Active heating/cooling
            switch (mode) {
                case HEAT:
                    if (currentTemperature < targetTemperature) {
                        currentTemperature += 0.5;
                    }
                    break;
                case COOL:
                    if (currentTemperature > targetTemperature) {
                        currentTemperature -= 0.5;
                    }
                    break;
                case AUTO:
                    if (currentTemperature < targetTemperature - 1) {
                        currentTemperature += 0.5;
                    } else if (currentTemperature > targetTemperature + 1) {
                        currentTemperature -= 0.5;
                    }
                    break;
            }
        }
        currentTemperature = Math.round(currentTemperature * 10.0) / 10.0;
    }

    private void adjustMode() {
        if (mode == ThermostatMode.AUTO) {
            if (currentTemperature < targetTemperature - 2) {
                System.out.println(name + " automatically switched to HEAT mode");
            } else if (currentTemperature > targetTemperature + 2) {
                System.out.println(name + " automatically switched to COOL mode");
            }
        }
    }

    @Override
    public double getCurrentPowerConsumption() {
        if (!isOn || mode == ThermostatMode.OFF) return 0.0;
        
        double power = FAN_POWER;
        
        if (mode == ThermostatMode.HEAT && currentTemperature < targetTemperature) {
            power += HEATING_POWER;
        } else if (mode == ThermostatMode.COOL && currentTemperature > targetTemperature) {
            power += COOLING_POWER;
        } else if (mode == ThermostatMode.AUTO) {
            if (currentTemperature < targetTemperature - 1) {
                power += HEATING_POWER;
            } else if (currentTemperature > targetTemperature + 1) {
                power += COOLING_POWER;
            }
        }
        
        return power;
    }

    @Override
    public String getStatus() {
        return String.format("Mode: %s, Current: %.1f°C, Target: %.1f°C, Fan: %s",
            mode, currentTemperature, targetTemperature, fanSpeed);
    }

    // Getters
    public double getTargetTemperature() {
        return targetTemperature;
    }

    public double getCurrentTemperature() {
        return currentTemperature;
    }

    public ThermostatMode getMode() {
        return mode;
    }

    public FanSpeed getFanSpeed() {
        return fanSpeed;
    }
}
