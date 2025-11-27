package com.smarthome.automation;

public interface Trigger {
    boolean evaluate(Context context);
    default String describe() { return "trigger"; }
}
