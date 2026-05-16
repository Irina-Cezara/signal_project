package com.alerts.strategy;

import com.data_management.PatientRecord;
import java.util.List;

public class OxygenSaturationStrategy implements AlertStrategy {

    private static final double LOW_SATURATION = 92.0;
    private static final double RAPID_DROP = 5.0;
    private static final long TEN_MINUTES = 10 * 60 * 1000;

    @Override
    public boolean checkAlert(List<PatientRecord> records) {
        for (int i = 0; i < records.size(); i++) {
            PatientRecord current = records.get(i);
            double value = current.getMeasurementValue();

            if (value < LOW_SATURATION)
                return true;

            for (int j = i - 1; j >= 0; j--) {
                PatientRecord earlier = records.get(j);
                if (current.getTimestamp() - earlier.getTimestamp() > TEN_MINUTES)
                    break;
                if (earlier.getMeasurementValue() - value >= RAPID_DROP)
                    return true;
            }
        }
        return false;
    }
}