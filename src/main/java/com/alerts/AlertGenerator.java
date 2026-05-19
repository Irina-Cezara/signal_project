package com.alerts;

import java.util.ArrayList;
import java.util.List;

import com.alerts.factory.AlertFactory;
import com.alerts.factory.BloodPressureAlertFactory;
import com.alerts.factory.BloodOxygenAlertFactory;
import com.alerts.factory.ECGAlertFactory;
import com.alerts.strategy.BloodPressureStrategy;
import com.alerts.strategy.OxygenSaturationStrategy;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;

    // Factories
    private AlertFactory bloodPressureFactory = new BloodPressureAlertFactory();
    private AlertFactory bloodOxygenFactory = new BloodOxygenAlertFactory();
    private AlertFactory ecgFactory = new ECGAlertFactory();

    // Strategies
    private BloodPressureStrategy bloodPressureStrategy = new BloodPressureStrategy();
    private OxygenSaturationStrategy oxygenStrategy = new OxygenSaturationStrategy();

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        // Get all records patient
        List<PatientRecord> records = dataStorage.getRecords(
                patient.getPatientId(), 0, Long.MAX_VALUE);

        // Separate records by type
        List<PatientRecord> systolicRecords = new ArrayList<>();
        List<PatientRecord> diastolicRecords = new ArrayList<>();
        List<PatientRecord> saturationRecords = new ArrayList<>();
        List<PatientRecord> ecgRecords = new ArrayList<>();
        List<PatientRecord> alertRecords = new ArrayList<>();

        for (PatientRecord record : records) {
            switch (record.getRecordType()) {
                case "SystolicPressure":
                    systolicRecords.add(record);
                    break;
                case "DiastolicPressure":
                    diastolicRecords.add(record);
                    break;
                case "Saturation":
                    saturationRecords.add(record);
                    break;
                case "ECG":
                    ecgRecords.add(record);
                    break;
                case "Alert":
                    alertRecords.add(record);
                    break;
            }
        }
        // Run each check type
        checkBloodPressureAlerts(patient, systolicRecords, diastolicRecords);
        checkSaturationAlerts(patient, saturationRecords);
        checkCombinedAlert(patient, systolicRecords, saturationRecords);
        checkECGAlerts(patient, ecgRecords);
        checkTriggeredAlert(patient, alertRecords);
    }

    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        System.out.println("ALERT TRIGGERED - Patient: " + alert.getPatientId()
                + " | Condition: " + alert.getCondition()
                + " | Time: " + alert.getTimestamp());
    }

    // Helper methods (Checking threshholds for all records type)

    /**
     * Checks all blood pressure related alerts
     * - Critical threholds (too high or too low)
     * - Trend alerts (3 consecutive readings changing by more than 10)
     */
    private void checkBloodPressureAlerts(Patient patient,
            List<PatientRecord> systolicRecords,
            List<PatientRecord> diastolicRecords) {
        String patientId = String.valueOf(patient.getPatientId());

        // Use strategy to check systolic
        if (bloodPressureStrategy.checkAlert(systolicRecords)) {
            for (PatientRecord record : systolicRecords) {
                double value = record.getMeasurementValue();
                if (value > 180) {
                    triggerAlert(bloodPressureFactory.createAlert(patientId,
                            "Critical Systolic High: " + value,
                            record.getTimestamp()));
                } else if (value < 90) {
                    triggerAlert(bloodPressureFactory.createAlert(patientId,
                            "Critical Systolic Low: " + value,
                            record.getTimestamp()));
                }
            }
        }

        // Use strategy to check diastolic
        if (bloodPressureStrategy.checkAlert(diastolicRecords)) {
            for (PatientRecord record : diastolicRecords) {
                double value = record.getMeasurementValue();
                if (value > 120) {
                    triggerAlert(bloodPressureFactory.createAlert(patientId,
                            "Critical Diastolic High: " + value,
                            record.getTimestamp()));
                } else if (value < 60) {
                    triggerAlert(bloodPressureFactory.createAlert(patientId,
                            "Critical Diastolic Low: " + value,
                            record.getTimestamp()));
                }
            }
        }

        // Check systolic trend
        checkTrend(patient, systolicRecords, "Systolic");

        // Check diastolic trend
        checkTrend(patient, diastolicRecords, "Diastolic");
    }

    /**
     * Checks if 3 consecutive readings show a consistent
     * increase or decrease of more than 10 each time
     */
    private void checkTrend(Patient patient,
            List<PatientRecord> records,
            String type) {
        String patientId = String.valueOf(patient.getPatientId());

        // Need at least 3 records to check a trend
        if (records.size() < 3)
            return;

        for (int i = 2; i < records.size(); i++) {
            double first = records.get(i - 2).getMeasurementValue();
            double second = records.get(i - 1).getMeasurementValue();
            double third = records.get(i).getMeasurementValue();

            boolean increasing = (second - first > 10) && (third - second > 10);
            boolean decreasing = (first - second > 10) && (second - third > 10);

            if (increasing) {
                triggerAlert(bloodPressureFactory.createAlert(patientId,
                        type + " Increasing Trend",
                        records.get(i).getTimestamp()));
            } else if (decreasing) {
                triggerAlert(bloodPressureFactory.createAlert(patientId,
                        type + " Decreasing Trend",
                        records.get(i).getTimestamp()));
            }
        }
    }

    /**
     * Checks blood oxygen saturation alerts
     * - Below 92% triggers low saturation alert
     * - Drop of 5% or more within 10 minutes triggers rapid drop alert
     */
    private void checkSaturationAlerts(Patient patient,
            List<PatientRecord> saturationRecords) {
        String patientId = String.valueOf(patient.getPatientId());
        long tenMinutes = 10 * 60 * 1000;

        // Use strategy to check saturation
        if (oxygenStrategy.checkAlert(saturationRecords)) {
            for (int i = 0; i < saturationRecords.size(); i++) {
                PatientRecord current = saturationRecords.get(i);
                double value = current.getMeasurementValue();

                // Low saturation alert
                if (value < 92) {
                    triggerAlert(bloodOxygenFactory.createAlert(patientId,
                            "Low Saturation: " + value + "%",
                            current.getTimestamp()));
                }

                for (int j = i - 1; j >= 0; j--) {
                    PatientRecord earlier = saturationRecords.get(j);

                    // Stop if more than 10 minutes passed
                    if (current.getTimestamp() - earlier.getTimestamp() > tenMinutes) {
                        break;
                    }

                    double drop = earlier.getMeasurementValue() - value;
                    if (drop >= 5) {
                        triggerAlert(bloodOxygenFactory.createAlert(patientId,
                                "Rapid Saturation Drop: " + drop + "%",
                                current.getTimestamp()));
                        break;
                    }
                }
            }
        }
    }

    /**
     * Checks hypotensive + hypoxemia alert
     * Triggers when both systolic < 90 AND saturation < 92 at the same time
     */
    private void checkCombinedAlert(Patient patient,
            List<PatientRecord> systolicRecords,
            List<PatientRecord> saturationRecords) {
        String patientId = String.valueOf(patient.getPatientId());
        boolean lowBloodPressure = systolicRecords.stream()
                .anyMatch(r -> r.getMeasurementValue() < 90);
        boolean lowSaturation = saturationRecords.stream()
                .anyMatch(r -> r.getMeasurementValue() < 92);

        if (lowBloodPressure && lowSaturation) {
            triggerAlert(bloodOxygenFactory.createAlert(patientId,
                    "Hypotensive Hypoxemia Alert",
                    System.currentTimeMillis()));
        }
    }

    /**
     * Checks ECG data for abnormal peaks
     * Uses a sliding window average. If a reading is far above
     * the average it is considered an abnormal peak
     */
    private void checkECGAlerts(Patient patient,
            List<PatientRecord> ecgRecords) {
        String patientId = String.valueOf(patient.getPatientId());
        int windowSize = 10;
        double peakThreshold = 2.0;

        if (ecgRecords.size() < windowSize)
            return;

        for (int i = windowSize; i < ecgRecords.size(); i++) {
            double sum = 0;
            for (int j = i - windowSize; j < i; j++) {
                sum += Math.abs(ecgRecords.get(j).getMeasurementValue());
            }
            double average = sum / windowSize;
            double currentValue = Math.abs(ecgRecords.get(i).getMeasurementValue());

            if (average > 0 && currentValue > peakThreshold * average) {
                triggerAlert(ecgFactory.createAlert(patientId,
                        "Abnormal ECG Peak: " + currentValue,
                        ecgRecords.get(i).getTimestamp()));
            }
        }
    }

    /**
     * Checks for manually triggered alerts from nurses or patients
     */
    private void checkTriggeredAlert(Patient patient,
            List<PatientRecord> alertRecords) {
        String patientId = String.valueOf(patient.getPatientId());

        for (PatientRecord record : alertRecords) {
            if (record.getMeasurementValue() == 1.0) {
                triggerAlert(bloodPressureFactory.createAlert(patientId,
                        "Manual Alert Triggered",
                        record.getTimestamp()));
            }
        }
    }
}