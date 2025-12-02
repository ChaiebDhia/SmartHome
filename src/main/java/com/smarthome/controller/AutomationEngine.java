package com.smarthome.controller;

import com.smarthome.automation.Context;
import com.smarthome.automation.Rule;
import com.smarthome.model.Home;

import java.util.ArrayList;
import java.util.List;

public class AutomationEngine {
    private final Home home;
    private final List<Rule> rules = new ArrayList<>();

    public AutomationEngine(Home home) {
        this.home = home;
    }

    public void addRule(Rule rule) {
        rules.add(rule);
    }
    
    public void removeRule(Rule rule) {
        rules.remove(rule);
    }

    public List<Rule> getRules() {
        return new ArrayList<>(rules);
    }

    public void tick(long epochSeconds) {
        Context ctx = new Context(home, epochSeconds);
        for (Rule r : rules) {
            r.evaluateAndExecute(ctx);
        }
    }
}
