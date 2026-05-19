package alert_generation;

import com.alerts.strategy.BloodPressureStrategy;
import com.alerts.strategy.HeartRateStrategy;
import com.alerts.strategy.OxygenSaturationStrategy;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AlertStrategyTest {

    @Test
    void testBloodPressureHighSystolic() {
        BloodPressureStrategy strategy = new BloodPressureStrategy();
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 185.0, "SystolicPressure", 1000L));
        assertTrue(strategy.checkAlert(records));
    }

    @Test
    void testBloodPressureLowSystolic() {
        BloodPressureStrategy strategy = new BloodPressureStrategy();
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 85.0, "SystolicPressure", 1000L));
        assertTrue(strategy.checkAlert(records));
    }

    @Test
    void testBloodPressureNormalNoAlert() {
        BloodPressureStrategy strategy = new BloodPressureStrategy();
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 120.0, "SystolicPressure", 1000L));
        assertFalse(strategy.checkAlert(records));
    }

    @Test
    void testHeartRateTooHigh() {
        HeartRateStrategy strategy = new HeartRateStrategy();
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 110.0, "HeartRate", 1000L));
        assertTrue(strategy.checkAlert(records));
    }

    @Test
    void testHeartRateTooLow() {
        HeartRateStrategy strategy = new HeartRateStrategy();
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 45.0, "HeartRate", 1000L));
        assertTrue(strategy.checkAlert(records));
    }

    @Test
    void testHeartRateNormalNoAlert() {
        HeartRateStrategy strategy = new HeartRateStrategy();
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 75.0, "HeartRate", 1000L));
        assertFalse(strategy.checkAlert(records));
    }

    @Test
    void testLowOxygenSaturation() {
        OxygenSaturationStrategy strategy = new OxygenSaturationStrategy();
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 90.0, "Saturation", 1000L));
        assertTrue(strategy.checkAlert(records));
    }

    @Test
    void testNormalOxygenSaturationNoAlert() {
        OxygenSaturationStrategy strategy = new OxygenSaturationStrategy();
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 97.0, "Saturation", 1000L));
        assertFalse(strategy.checkAlert(records));
    }
}