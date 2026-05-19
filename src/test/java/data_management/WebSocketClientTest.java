package data_management;

import com.data_management.DataStorage;
import com.data_management.WebSocketClient;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

public class WebSocketClientTest {

    @Test
    void testClientCanBeCreated() throws URISyntaxException {
        WebSocketClient client = new WebSocketClient("ws://localhost:8080");
        assertNotNull(client);
    }

    @Test
    void testInvalidUriThrowsException() {
        assertThrows(URISyntaxException.class, () -> {
            new WebSocketClient("not valid $$$$");
        });
    }

    @Test
    void testDataStorageWorksCorrectly() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 85.0, "HeartRate", 1000L);
        assertFalse(storage.getAllPatients().isEmpty());
    }

    @Test
    void testSaturationPercentRemoved() {
        String raw = "95.0%".replace("%", "");
        double value = Double.parseDouble(raw);
        assertEquals(95.0, value);
    }
}