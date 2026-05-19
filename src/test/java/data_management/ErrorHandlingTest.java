package data_management;

import com.data_management.DataStorage;
import com.data_management.FileDataReader;
import com.data_management.WebSocketClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ErrorHandlingTest {

    @Test
    void testMalformedLineIsSkipped(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("data.txt");
        try (FileWriter writer = new FileWriter(file.toFile())) {
            writer.write("bad line\n");
            writer.write("Patient ID: 1, Timestamp: 1000, Label: HeartRate, Data: 85.0\n");
        }

        DataStorage storage = new DataStorage();
        FileDataReader reader = new FileDataReader(tempDir.toString());
        assertDoesNotThrow(() -> reader.readData(storage));
        assertFalse(storage.getAllPatients().isEmpty());
    }

    @Test
    void testEmptyDirectoryDoesNotCrash(@TempDir Path tempDir) {
        DataStorage storage = new DataStorage();
        FileDataReader reader = new FileDataReader(tempDir.toString());
        assertDoesNotThrow(() -> reader.readData(storage));
    }

    @Test
    void testInvalidDirectoryThrowsException() {
        DataStorage storage = new DataStorage();
        FileDataReader reader = new FileDataReader("nonexistent/directory");
        assertThrows(IOException.class, () -> reader.readData(storage));
    }

    @Test
    void testFileDataReaderDoesNotSupportWebSocket(@TempDir Path tempDir) {
        DataStorage storage = new DataStorage();
        FileDataReader reader = new FileDataReader(tempDir.toString());
        assertThrows(UnsupportedOperationException.class, () -> {
            reader.readData("ws://localhost:8080", storage);
        });
    }

    @Test
    void testInvalidWebSocketUri() {
        assertThrows(URISyntaxException.class, () -> {
            new WebSocketClient("not valid $$$$");
        });
    }
}