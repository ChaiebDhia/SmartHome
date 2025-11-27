package com.smarthome.scheduler;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    public static class ScheduledTask {
        public final LocalTime time;
        public final Runnable action;
        public final String description;
        public ScheduledTask(LocalTime time, String description, Runnable action) {
            this.time = time; this.description = description; this.action = action; }
    }

    private final List<ScheduledTask> tasks = new ArrayList<>();
    private LocalTime lastRunCheck = LocalTime.MIN;

    public void add(LocalTime time, String description, Runnable action) {
        tasks.add(new ScheduledTask(time, description, action));
    }

    public void tick() {
        LocalTime now = LocalTime.now();
        for (ScheduledTask t : tasks) {
            if (!hasRun(t, now) && now.isAfter(t.time)) {
                System.out.println("[Scheduler] Executing: " + t.description);
                try { t.action.run(); } catch (Exception e) { System.out.println("[Scheduler] Task failed: " + e.getMessage()); }
            }
        }
        lastRunCheck = now;
    }

    private boolean hasRun(ScheduledTask t, LocalTime now) {
        return lastRunCheck.isAfter(t.time) || lastRunCheck.equals(t.time);
    }

    public List<ScheduledTask> getTasks() { return new ArrayList<>(tasks); }
}
