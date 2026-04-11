package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Generates simulated alert events for patients.
 *
 * <p>
 * Each patient has an independent alert state.
 */
// Added class Javadoc
public class AlertGenerator implements PatientDataGenerator {

    // Changed variable name to UPPER_SNAKE_CASE
    public static final Random RANDOM_GENERATOR = new Random();
    // Changed variable name to camelCase
    private boolean[] alertStates; // false = resolved, true = pressed

    /**
     * Creates a new {@code AlertGenerator} for the given number of patients.
     *
     * @param patientCount the total number of patients; indices 1..patientCount are
     *                     used
     */
    // Added constructor Javadoc with @param
    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }

    /**
     * Generates an alert event for the specified patient and forwards it to the
     * output strategy.
     * 
     * @param patientId      the patient identifier number
     * @param outputStrategy the strategy used to generate the alert
     */
    // Added method Javadoc wit @param
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {
                if (RANDOM_GENERATOR.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                // Changed variable name to lowerCamelCase
                double lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                double p = -Math.expm1(-lambda); // Probability of at least one alert in the period
                boolean alertTriggered = RANDOM_GENERATOR.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
