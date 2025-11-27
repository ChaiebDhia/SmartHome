package com.smarthome.oop;

import java.time.LocalTime;

public interface Schedulable {
    void schedule(LocalTime time, String action); // action examples: on, off, toggle
}
