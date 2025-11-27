package com.smarthome.service;

import com.smarthome.model.Home;

public class EnergyMonitor {
    private double cumulativeKWh = 0.0; // total since start
    private long lastUpdateMillis = System.currentTimeMillis();
    private final Home home;

    public EnergyMonitor(Home home) { this.home = home; }

    public void update() {
        long now = System.currentTimeMillis();
        double hours = (now - lastUpdateMillis) / 3600000.0;
        lastUpdateMillis = now;
        double watts = home.getTotalPowerConsumption();
        cumulativeKWh += (watts / 1000.0) * hours;
    }

    public double getCumulativeKWh() { return cumulativeKWh; }

    public double estimateDailyCost(double ratePerKWh) {
        return cumulativeKWh * ratePerKWh;
    }
}
