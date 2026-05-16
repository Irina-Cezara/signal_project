package com.alerts.strategy;

import java.util.List;

import com.data_management.PatientRecord;

public class BloodPressureStrategy implements AlertStrategy {

    @Override
    public boolean checkAlert(List<PatientRecord> records) {

        for (PatientRecord record : records) {
            double value = record.getMeasurementValue();
            String type = record.getRecordType();

            if (type.equals("SystolicPressure")) {
                if (value > 180 || value < 90)
                    return true;
            }
            if (type.equals("DiastolicPressure")) {
                if (value > 120 || value < 60)
                    return true;
            }
        }

        if (records.size() >= 3) {
            for (int i = 2; i < records.size(); i++) {
                double first = records.get(i - 2).getMeasurementValue();
                double second = records.get(i - 1).getMeasurementValue();
                double third = records.get(i).getMeasurementValue();

                boolean increasing = (second - first > 10) && (third - second > 10);
                boolean decreasing = (first - second > 10) && (second - third > 10);

                if (increasing || decreasing)
                    return true;
            }
        }
        return false;
    }
}