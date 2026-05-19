package alert_generation;

import com.alerts.Alert;
import com.alerts.decorator.PriorityAlertDecorator;
import com.alerts.decorator.RepeatedAlertDecorator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AlertDecoratorTest {

    @Test
    void testRepeatedAlertContainsOriginalCondition() {
        Alert base = new Alert("1", "High Blood Pressure", 1000L);
        RepeatedAlertDecorator repeated = new RepeatedAlertDecorator(base, 3, 5000L);
        assertTrue(repeated.getCondition().contains("High Blood Pressure"));
    }

    @Test
    void testRepeatedAlertRepeatCount() {
        Alert base = new Alert("1", "High Blood Pressure", 1000L);
        RepeatedAlertDecorator repeated = new RepeatedAlertDecorator(base, 3, 5000L);
        assertEquals(3, repeated.getRepeatCount());
    }

    @Test
    void testRepeatedAlertInterval() {
        Alert base = new Alert("1", "High Blood Pressure", 1000L);
        RepeatedAlertDecorator repeated = new RepeatedAlertDecorator(base, 3, 5000L);
        assertEquals(5000L, repeated.getIntervalMs());
    }

    @Test
    void testPriorityAlertContainsOriginalCondition() {
        Alert base = new Alert("1", "Low Saturation", 1000L);
        PriorityAlertDecorator priority = new PriorityAlertDecorator(base, "HIGH");
        assertTrue(priority.getCondition().contains("Low Saturation"));
    }

    @Test
    void testPriorityAlertContainsPriorityTag() {
        Alert base = new Alert("1", "Low Saturation", 1000L);
        PriorityAlertDecorator priority = new PriorityAlertDecorator(base, "HIGH");
        assertTrue(priority.getCondition().contains("HIGH PRIORITY"));
    }

    @Test
    void testPriorityLevel() {
        Alert base = new Alert("1", "Low Saturation", 1000L);
        PriorityAlertDecorator priority = new PriorityAlertDecorator(base, "HIGH");
        assertEquals("HIGH", priority.getPriority());
    }

    @Test
    void testPatientIdPreserved() {
        Alert base = new Alert("42", "ECG Alert", 1000L);
        PriorityAlertDecorator priority = new PriorityAlertDecorator(base, "HIGH");
        assertEquals("42", priority.getPatientId());
    }

    @Test
    void testTimestampPreserved() {
        Alert base = new Alert("1", "ECG Alert", 9999L);
        PriorityAlertDecorator priority = new PriorityAlertDecorator(base, "LOW");
        assertEquals(9999L, priority.getTimestamp());
    }
}