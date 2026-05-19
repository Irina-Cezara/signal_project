package com.data_management;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileDataReader implements DataReader {

    private String directory;

    /**
     * Creates a FileDataReader that reads from the specified directory
     * 
     * @param directory the path to the directory containing output files
     */
    public FileDataReader(String directory) {
        this.directory = directory;
    }

    /**
     * Reads all .txt files in the directory and stores the data in DataStorage
     * 
     * @param dataStorage the storage where data will be saved
     * @throws IOException if the directory cannot be read
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        // Walk through every file in the directory
        try (Stream<Path> paths = Files.walk(Paths.get(directory))) {
            paths
                    .filter(Files::isRegularFile) // only files, not folders
                    .filter(p -> p.toString().endsWith(".txt"))
                    .forEach(path -> {
                        try {
                            readFile(path, dataStorage);
                        } catch (IOException e) {
                            System.err.println("Error reading file: " + path + " - " + e.getMessage());
                        }
                    });
        }
    }

    /**
     * Reads a single file line by line and parses each line
     * 
     * @param path        the file to read
     * @param dataStorage where parsed data is stored
     */
    private void readFile(Path path, DataStorage dataStorage) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    parseAndStore(line, dataStorage);
                }
            }
        }
    }

    /**
     * Parses a single line and stores it in DataStorage
     * Line format: "Patient ID: 1, Timestamp: 1700000000000, Label: HeartRate,
     * Data: 85.0"
     * 
     * @param line        the line to parse
     * @param dataStorage where parsed data is stored
     */
    private void parseAndStore(String line, DataStorage dataStorage) {
        try {
            // Split by comma to get the 4 parts
            String[] parts = line.split(", ");

            // Extract each value after the colon
            int patientId = Integer.parseInt(parts[0].split(": ")[1].trim());
            long timestamp = Long.parseLong(parts[1].split(": ")[1].trim());
            String label = parts[2].split(": ")[1].trim();
            String rawData = parts[3].split(": ")[1].trim();

            // Remove % sign if present (e.g. saturation values like "95.0%")
            rawData = rawData.replace("%", "");

            double measurementValue = Double.parseDouble(rawData);

            // Store in DataStorage
            dataStorage.addPatientData(patientId, measurementValue, label, timestamp);

        } catch (Exception e) {
            System.err.println("Skipping malformed line: " + line);
        }
    }

    // Not using interface method for webSocket
    @Override
    public void readData(String serverUri, DataStorage dataStorage) throws IOException {
        throw new UnsupportedOperationException("FileDataReader does not use WebSocket");
    }
}
