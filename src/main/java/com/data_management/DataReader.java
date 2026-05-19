package com.data_management;

import java.io.IOException;

public interface DataReader {
    /**
     * Reads data from a specified source and stores it in the data storage.
     * 
     * @param dataStorage the storage where data will be stored
     * @throws IOException if there is an error reading the data
     */
    void readData(DataStorage dataStorage) throws IOException;

    /**
     * Connects to a WebSocket server and continuously receives real-time data.
     * 
     * @param server      the URL of the WebSocket server
     * @param dataStorage the storage where data will be stored
     * @throws IOException if there is an error connecting to the server
     */
    void readData(String server, DataStorage dataStorage) throws IOException;
}
