package com.smarthome.automation.builtins;

import com.smarthome.automation.Context;
import com.smarthome.automation.Trigger;

import java.time.LocalTime;

public class TimeAfterTrigger implements Trigger {
    private final LocalTime time;

    public TimeAfterTrigger(LocalTime time) { this.time = time; }

    @Override
    public boolean evaluate(Context context) {
        LocalTime now = LocalTime.now();
        return now.isAfter(time);
    }

    @Override
    public String describe() { return "time after " + time; }
}
