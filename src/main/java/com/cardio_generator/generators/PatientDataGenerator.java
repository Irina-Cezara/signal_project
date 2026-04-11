package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Specifies the interface for all patient data generators classes
 */
public interface PatientDataGenerator {

    /**
     * Generates health data for a patient and sends it to an output destination
     * 
     * @param patientId      the patient identifier
     * @param outputStrategy the destination of the generated datd (output)
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
