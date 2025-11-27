package com.smarthome.automation;

import com.smarthome.model.Home;

public class Context {
    private final Home home;
    private final long epochSeconds;

    public Context(Home home, long epochSeconds) {
        this.home = home;
        this.epochSeconds = epochSeconds;
    }

    public Home getHome() {
        return home;
    }

    public long getEpochSeconds() {
        return epochSeconds;
    }
}
