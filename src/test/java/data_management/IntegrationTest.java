package data_management;

import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.FileDataReader;
import com.data_management.Patient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

    @Test
    void testFileReaderStoresDataCorrectly(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("data.txt");
        try (FileWriter writer = new FileWriter(file.toFile())) {
            writer.write("Patient ID: 1, Timestamp: 1000, Label: HeartRate, Data: 85.0\n");
        }

        DataStorage storage = new DataStorage();
        FileDataReader reader = new FileDataReader(tempDir.toString());
        reader.readData(storage);

        assertFalse(storage.getAllPatients().isEmpty());
    }

    @Test
    void testAlertGeneratorRunsOnStoredData(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("data.txt");
        try (FileWriter writer = new FileWriter(file.toFile())) {
            writer.write("Patient ID: 1, Timestamp: 1000, Label: SystolicPressure, Data: 185.0\n");
        }

        DataStorage storage = new DataStorage();
        FileDataReader reader = new FileDataReader(tempDir.toString());
        reader.readData(storage);

        AlertGenerator alertGenerator = new AlertGenerator(storage);
        List<Patient> patients = storage.getAllPatients();
        assertDoesNotThrow(() -> alertGenerator.evaluateData(patients.get(0)));
    }
}