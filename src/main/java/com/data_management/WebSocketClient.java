package com.data_management;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;

/**
 * WebSocketClient connects to a WebSocket server and continuously
 * receives real-time patient data, parses it and stores it in DataStorage.
 */
public class WebSocketClient extends org.java_websocket.client.WebSocketClient implements DataReader {

    private DataStorage dataStorage;

    /**
     * Creates a WebSocketClient that connects to the given URI
     * 
     * @param serverUri the URI of the WebSocket server
     * @throws URISyntaxException if the URI is invalid
     */
    public WebSocketClient(String serverUri) throws URISyntaxException {
        super(new URI(serverUri));
    }

    /**
     * Called when connection to the server is established
     */
    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Connected to WebSocket server");
    }

    /**
     * Called when a message is received from the server
     * Parses the message and stores it in DataStorage
     * Format: patientId,timestamp,label,data
     */
    @Override
    public void onMessage(String message) {
        if (dataStorage == null)
            return;
        try {
            parseAndStore(message, dataStorage);
        } catch (Exception e) {
            System.err.println("Error processing message: " + message + " - " + e.getMessage());
        }
    }

    /**
     * Called when the connection is closed
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected from WebSocket server. Reason: " + reason);
    }

    /**
     * Called when an error occurs
     */
    @Override
    public void onError(Exception e) {
        System.err.println("WebSocket error: " + e.getMessage());
    }

    /**
     * Reads data from a file - not used in WebSocket mode
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        this.dataStorage = dataStorage;
        try {
            this.connectBlocking();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Connection interrupted", e);
        }
    }

    /**
     * Connects to a WebSocket server and continuously receives data
     * 
     * @param serverUri   the URI of the WebSocket server
     * @param dataStorage the storage where data will be stored
     */
    @Override
    public void readData(String serverUri, DataStorage dataStorage) throws IOException {
        this.dataStorage = dataStorage;
        try {
            this.connectBlocking();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Connection interrupted", e);
        }
    }

    /**
     * Parses a message and stores it in DataStorage
     * Format: patientId,timestamp,label,data
     * 
     * @param message     the message to parse
     * @param dataStorage where parsed data is stored
     */
    private void parseAndStore(String message, DataStorage dataStorage) {
        String[] parts = message.split(",");
        if (parts.length != 4) {
            System.err.println("Skipping malformed message: " + message);
            return;
        }

        int patientId = Integer.parseInt(parts[0].trim());
        long timestamp = Long.parseLong(parts[1].trim());
        String label = parts[2].trim();
        String rawData = parts[3].trim().replace("%", "");
        double measurementValue = Double.parseDouble(rawData);

        dataStorage.addPatientData(patientId, measurementValue, label, timestamp);
    }
}