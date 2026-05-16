package com.alerts.factory;

import com.alerts.Alert;

public abstract class AlertFacatory {

    public abstract Alert createAlert(String patientId, String condition, long timestamp);

}
