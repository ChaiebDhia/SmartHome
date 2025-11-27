package com.smarthome;

import com.smarthome.model.devices.SmartLight;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DeviceTests {
    @Test
    void smartLightPowerAndBrightness() {
        SmartLight light = new SmartLight("Test Light", "Test Room");
        assertEquals(0.0, light.getCurrentPowerConsumption(), 0.0001);
        light.turnOn();
        light.setBrightness(50);
        assertTrue(light.isOn());
        assertEquals(5.0, light.getCurrentPowerConsumption(), 0.001); // 10W * 0.5
    }
}
