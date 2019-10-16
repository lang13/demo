package com.eem.demo.service.impl;

import com.eem.demo.entity.Room;
import com.eem.demo.repository.RoomRepository;
import com.eem.demo.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Administrator
 */
public class RoomServiceImpl implements RoomService {
    @Autowired
    RoomRepository roomRepository;

    @Override
    public Room createRoom(String roomName, String masterName) {
        Room room = new Room();
        room.setMaster(masterName);
        room.setRoomName(roomName);
        return roomRepository.save(room);
    }
}
