package com.alerts.decorator;

import com.alerts.Alert;

public class RepeatedAlertDecorator extends AlertDecorator {

    private int repeatCount;
    private long intervalMs;

    public RepeatedAlertDecorator(Alert alert, int repeatCount, long intervalMs) {
        super(alert);
        this.repeatCount = repeatCount;
        this.intervalMs = intervalMs;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public long getIntervalMs() {
        return intervalMs;
    }

    @Override
    public String getCondition() {
        return decoratedAlert.getCondition() + " [Repeated " + repeatCount + " times]";
    }
}