package alert_generation;

import com.data_management.DataStorage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SingletonTest {

    @Test
    void testDataStorageSingletonReturnsSameInstance() {
        DataStorage instance1 = DataStorage.getInstance();
        DataStorage instance2 = DataStorage.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    void testDataStorageInstanceNotNull() {
        assertNotNull(DataStorage.getInstance());
    }

    @Test
    void testDataStorageDataPersistsBetweenCalls() {
        DataStorage storage1 = DataStorage.getInstance();
        storage1.addPatientData(999, 85.0, "HeartRate", 1000L);
        DataStorage storage2 = DataStorage.getInstance();
        assertFalse(storage2.getAllPatients().isEmpty());
    }
}