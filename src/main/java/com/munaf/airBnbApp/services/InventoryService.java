package com.munaf.airBnbApp.services;

import com.munaf.airBnbApp.entities.Room;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteFutureInventories(Room room);
}
