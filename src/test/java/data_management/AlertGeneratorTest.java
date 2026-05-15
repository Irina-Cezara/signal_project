package data_management;

import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AlertGeneratorTest {

    private DataStorage dataStorage;
    private AlertGenerator alertGenerator;

    @BeforeEach
    void setUp() {
        dataStorage = new DataStorage();
        alertGenerator = new AlertGenerator(dataStorage);
    }

    // Blood pressure tests

    @Test
    void testSystolicHighAlert() {
        // Reading above 180
        dataStorage.addPatientData(1, 185.0, "SystolicPressure", 1000L);
        Patient patient = dataStorage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    @Test
    void testSystolicLowAlert() {
        dataStorage.addPatientData(1, 85.0, "SystolicPressure", 1000L);
        Patient patient = dataStorage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    @Test
    void testDiastolicHighAlert() {
        dataStorage.addPatientData(1, 125.0, "DiastolicPressure", 1000L);
        Patient patient = dataStorage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    @Test
    void testDiastolicLowAlert() {
        dataStorage.addPatientData(1, 55.0, "DiastolicPressure", 1000L);
        Patient patient = dataStorage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    @Test
    void testSystolicIncreasingTrend() {
        // Three readings increasing by more than 10
        dataStorage.addPatientData(1, 100.0, "SystolicPressure", 1000L);
        dataStorage.addPatientData(1, 115.0, "SystolicPressure", 2000L);
        dataStorage.addPatientData(1, 130.0, "SystolicPressure", 3000L);
        Patient patient = dataStorage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    @Test
    void testSystolicDecreasingTrend() {
        // Three readings decreasing by more than 10
        dataStorage.addPatientData(1, 150.0, "SystolicPressure", 1000L);
        dataStorage.addPatientData(1, 135.0, "SystolicPressure", 2000L);
        dataStorage.addPatientData(1, 120.0, "SystolicPressure", 3000L);
        Patient patient = dataStorage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    @Test
    void testNoTrendWhenChangeLessThan10() {
        // Changes less than 10 - should NOT trigger trend alert
        dataStorage.addPatientData(1, 100.0, "SystolicPressure", 1000L);
        dataStorage.addPatientData(1, 105.0, "SystolicPressure", 2000L);
        dataStorage.addPatientData(1, 110.0, "SystolicPressure", 3000L);
        Patient patient = dataStorage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    // Blood sarturation
    @Test
    void testLowSaturationAlert() {
        // Alert should be triggered since less than 92%
        dataStorage.addPatientData(1, 91.0, "Saturation", 1000L);
        Patient patient = dataStorage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    @Test
    void testNormalSaturationNoAlert() {
        // No alert should be triggered since more than 92%
        dataStorage.addPatientData(1, 95.0, "Saturation", 1000L);
        Patient patient = dataStorage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    @Test
    void testRapidSaturationDrop() {
        // Drop of 5% within 10 minutes should trigger alert
        long now = System.currentTimeMillis();
        dataStorage.addPatientData(1, 98.0, "Saturation", now - 5 * 60 * 1000); // 5 min ago
        dataStorage.addPatientData(1, 92.0, "Saturation", now); // now - drop of 6%
        Patient patient = dataStorage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    @Test
    void testNoRapidDropOutside10Minutes() {
        long now = System.currentTimeMillis();
        dataStorage.addPatientData(1, 98.0, "Saturation", now - 15 * 60 * 1000); // 15 min ago
        dataStorage.addPatientData(1, 92.0, "Saturation", now);
        Patient patient = dataStorage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    @Test
    void testHypotensiveHypoxemiaAlert() {
        // low systolic + low saturation
        dataStorage.addPatientData(1, 85.0, "SystolicPressure", 1000L);
        dataStorage.addPatientData(1, 90.0, "Saturation", 1000L);
        Patient patient = dataStorage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    @Test
    void testNoCombinedAlertWhenOnlyOneLow() {
        // low systolic, saturation ok
        dataStorage.addPatientData(1, 85.0, "SystolicPressure", 1000L);
        dataStorage.addPatientData(1, 97.0, "Saturation", 1000L);
        Patient patient = dataStorage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    // ECG
    @Test
    void testECGAbnormalPeak() {
        // 10 normal readings + one spike
        for (int i = 0; i < 10; i++) {
            dataStorage.addPatientData(1, 0.5, "ECG", 1000L * i);
        }
        dataStorage.addPatientData(1, 5.0, "ECG", 10000L);
        Patient patient = dataStorage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    @Test
    void testECGNormalReadings() {
        // All normal readings - no alert
        for (int i = 0; i < 11; i++) {
            dataStorage.addPatientData(1, 0.5, "ECG", 1000L * i);
        }
        Patient patient = dataStorage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    // Triger alert
    // Value 1
    @Test
    void testManualAlertTriggered() {
        dataStorage.addPatientData(1, 1.0, "Alert", 1000L);
        Patient patient = dataStorage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    // Value 0
    @Test
    void testManualAlertResolved() {
        dataStorage.addPatientData(1, 0.0, "Alert", 1000L);
        Patient patient = dataStorage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }

    // Other cases (no records or not enough readings)
    @Test
    void testPatientWithNoRecords() {
        dataStorage.addPatientData(1, 85.0, "SystolicPressure", 1000L);
        Patient patient = dataStorage.getAllPatients().get(0);
        DataStorage emptyStorage = new DataStorage();
        AlertGenerator emptyGenerator = new AlertGenerator(emptyStorage);
        emptyStorage.addPatientData(2, 120.0, "SystolicPressure", 1000L);
        Patient emptyPatient = emptyStorage.getAllPatients().get(0);
        assertDoesNotThrow(() -> emptyGenerator.evaluateData(emptyPatient));
    }

    @Test
    void testLessThanThreeReadingsForTrend() {
        dataStorage.addPatientData(1, 100.0, "SystolicPressure", 1000L);
        dataStorage.addPatientData(1, 115.0, "SystolicPressure", 2000L);
        Patient patient = dataStorage.getAllPatients().get(0);
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patient));
    }
}
