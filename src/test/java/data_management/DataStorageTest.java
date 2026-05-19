package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;

import java.util.List;

class DataStorageTest {

    @Test
    void testAddAndGetRecords() {
        // TODO Perhaps you can implement a mock data reader to mock the test data?
        // DataReader reader
        DataStorage storage = new DataStorage(); // No need for reader??
        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
        storage.addPatientData(1, 200.0, "WhiteBloodCells", 1714376789051L);

        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);
        assertEquals(2, records.size()); // Check if two records are retrieved
        assertEquals(100.0, records.get(0).getMeasurementValue()); // Validate first record
    }

    @Test
    void testGetRecordsReturnsEmptyForUnknownPatient() {
        DataStorage storage = new DataStorage();
        List<PatientRecord> records = storage.getRecords(999, 0, Long.MAX_VALUE);
        assertTrue(records.isEmpty());
    }

    @Test
    void testGetAllPatients() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 85.0, "HeartRate", 1000L);
        storage.addPatientData(2, 90.0, "HeartRate", 1000L);
        assertEquals(2, storage.getAllPatients().size());
    }

    @Test
    void testGetInstanceReturnsSameInstance() {
        DataStorage instance1 = DataStorage.getInstance();
        DataStorage instance2 = DataStorage.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    void testGetInstanceNotNull() {
        assertNotNull(DataStorage.getInstance());
    }

    @Test
    void testRecordTypeIsCorrect() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 85.0, "HeartRate", 1000L);
        List<PatientRecord> records = storage.getRecords(1, 0, Long.MAX_VALUE);
        assertEquals("HeartRate", records.get(0).getRecordType());
    }
}