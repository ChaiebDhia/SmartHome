package com.smarthome.automation;

public interface Action {
    void execute(Context context);
    default String describe() { return "action"; }
}
