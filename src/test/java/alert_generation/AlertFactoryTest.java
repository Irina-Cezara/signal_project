package alert_generation;

import com.alerts.Alert;
import com.alerts.factory.AlertFactory;
import com.alerts.factory.BloodOxygenAlertFactory;
import com.alerts.factory.BloodPressureAlertFactory;
import com.alerts.factory.ECGAlertFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AlertFactoryTest {

    @Test
    void testBloodPressureAlertFactoryCreatesAlert() {
        AlertFactory factory = new BloodPressureAlertFactory();
        Alert alert = factory.createAlert("1", "High Systolic", 1000L);
        assertNotNull(alert);
        assertEquals("1", alert.getPatientId());
        assertTrue(alert.getCondition().contains("Blood Pressure Alert"));
        assertTrue(alert.getCondition().contains("High Systolic"));
        assertEquals(1000L, alert.getTimestamp());
    }

    @Test
    void testBloodOxygenAlertFactoryCreatesAlert() {
        AlertFactory factory = new BloodOxygenAlertFactory();
        Alert alert = factory.createAlert("2", "Low Saturation", 2000L);
        assertNotNull(alert);
        assertEquals("2", alert.getPatientId());
        assertTrue(alert.getCondition().contains("Blood Oxygen Alert"));
        assertTrue(alert.getCondition().contains("Low Saturation"));
        assertEquals(2000L, alert.getTimestamp());
    }

    @Test
    void testECGAlertFactoryCreatesAlert() {
        AlertFactory factory = new ECGAlertFactory();
        Alert alert = factory.createAlert("3", "Abnormal Peak", 3000L);
        assertNotNull(alert);
        assertEquals("3", alert.getPatientId());
        assertTrue(alert.getCondition().contains("ECG Alert"));
        assertTrue(alert.getCondition().contains("Abnormal Peak"));
        assertEquals(3000L, alert.getTimestamp());
    }

    @Test
    void testFactoryReturnsNewInstanceEachTime() {
        AlertFactory factory = new BloodPressureAlertFactory();
        Alert alert1 = factory.createAlert("1", "High", 1000L);
        Alert alert2 = factory.createAlert("1", "High", 1000L);
        assertNotSame(alert1, alert2);
    }
}