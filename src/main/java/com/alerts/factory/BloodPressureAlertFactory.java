package com.alerts.factory;

import com.alerts.Alert;

public class BloodPressureAlertFactory extends AlertFacatory {

    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new Alert(patientId, "Blood Pressure Alert: " + condition, timestamp);
    }

}
