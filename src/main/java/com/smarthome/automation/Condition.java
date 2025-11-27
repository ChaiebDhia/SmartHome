package com.smarthome.automation;

public interface Condition {
    boolean check(Context context);
    default String describe() { return "condition"; }
}
