package com.cardio_generator.outputs;

/**
 * Specifies the interface for all output destination
 * 
 */
public interface OutputStrategy {
    /**
     * Sends a patient's data that was recorded to the output
     * 
     * @param patientId the patient identifier
     * @param timestamp the moment the measurement was taken, in milliseconds
     * @param label     the category of the measurement
     * @param data      the measured value
     */
    void output(int patientId, long timestamp, String label, String data);
}
