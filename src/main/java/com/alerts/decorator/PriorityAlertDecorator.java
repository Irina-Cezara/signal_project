package com.alerts.decorator;

import com.alerts.Alert;

public class PriorityAlertDecorator extends AlertDecorator {

    private String priority;

    public PriorityAlertDecorator(Alert decoratedAlert, String priority) {
        super(decoratedAlert);
        this.priority = priority;
    }

    public String getPriority() {
        return priority;
    }

    @Override
    public String getCondition() {
        return "[" + priority + " PRIORITY] " + decoratedAlert.getCondition();
    }
}