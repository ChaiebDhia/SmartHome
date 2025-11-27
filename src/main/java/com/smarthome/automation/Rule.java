package com.smarthome.automation;

import java.util.ArrayList;
import java.util.List;

public class Rule {
    private final String name;
    private final Trigger trigger;
    private final List<Condition> conditions = new ArrayList<>();
    private final List<Action> actions = new ArrayList<>();
    private boolean enabled = true;

    public Rule(String name, Trigger trigger) {
        this.name = name;
        this.trigger = trigger;
    }

    public Rule addCondition(Condition condition) {
        conditions.add(condition);
        return this;
    }

    public Rule addAction(Action action) {
        actions.add(action);
        return this;
    }

    public void evaluateAndExecute(Context context) {
        if (!enabled) return;
        if (!trigger.evaluate(context)) return;
        for (Condition c : conditions) {
            if (!c.check(context)) return;
        }
        for (Action a : actions) {
            a.execute(context);
        }
    }

    public String getName() { return name; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    @Override
    public String toString() {
        return "Rule{" + name + ", enabled=" + enabled + "}";
    }
}
