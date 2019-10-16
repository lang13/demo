package com.eem.demo.service.impl;

import com.eem.demo.entity.Room;
import com.eem.demo.repository.RoomRepository;
import com.eem.demo.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author Administrator
 */
@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    RoomRepository roomRepository;

    @Override
    public Room createRoom(String roomName, String masterName, String masterId) {
        Room room = new Room();
        //设置room类
        room.setMaster(masterName);
        room.setMasterId(Integer.parseInt(masterId));
        room.setMember(masterName);
        room.setMemberId(Integer.parseInt(masterId));
        //设置roomName和roomId
        room.setRoomName(roomName);
        String uuid = UUID.randomUUID().toString().substring(0,5);
        room.setRoomId(uuid);
        return roomRepository.save(room);
    }
}
