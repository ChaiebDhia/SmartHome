package com.smarthome.oop;

public interface Controllable {
    void turnOn();
    void turnOff();
    default void toggle() { if (isOn()) turnOff(); else turnOn(); }
    boolean isOn();
}
