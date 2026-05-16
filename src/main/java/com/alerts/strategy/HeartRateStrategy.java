package com.alerts.strategy;

import com.data_management.PatientRecord;
import java.util.List;

public class HeartRateStrategy implements AlertStrategy {

    private static final double HIGH_HEART_RATE = 100.0;
    private static final double LOW_HEART_RATE = 50.0;

    @Override
    public boolean checkAlert(List<PatientRecord> records) {
        for (PatientRecord record : records) {
            if (record.getRecordType().equals("HeartRate")) {
                double value = record.getMeasurementValue();
                if (value > HIGH_HEART_RATE || value < LOW_HEART_RATE) {
                    return true;
                }
            }
        }
        return false;
    }
}