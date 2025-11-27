package com.smarthome;

import com.smarthome.model.Home;
import com.smarthome.model.Room;
import com.smarthome.model.devices.SmartLight;
import com.smarthome.util.DataPersistence;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class PersistenceTests {
    @Test
    void saveAndLoadHome() throws Exception {
        Home home = new Home("PersistHome", "Addr");
        Room r = new Room("Living", "Floor", 20);
        home.addRoom(r);
        SmartLight light = new SmartLight("Lamp", r.getName());
        r.addDevice(light);
        light.setBrightness(40);
        light.turnOn();
        String path = System.getProperty("java.io.tmpdir") + File.separator + "home.json";
        DataPersistence.saveHome(home, path);
        Home loaded = DataPersistence.loadHome(path);
        assertEquals("PersistHome", loaded.getName());
        assertEquals(1, loaded.getRooms().size());
        assertNotNull(loaded.getRoom("Living"));
    }
}
