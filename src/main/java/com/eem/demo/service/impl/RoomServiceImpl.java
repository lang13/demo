package com.eem.demo.service.impl;

import com.eem.demo.entity.Room;
import com.eem.demo.entity.RoomMember;
import com.eem.demo.repository.RoomMemberRepository;
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

    @Autowired
    RoomMemberRepository roomMemberRepository;

    @Override
    public Room createRoom(String roomName, String masterName, String masterId) {
        Room room = new Room();
        //设置room类
        room.setMasterId(Integer.parseInt(masterId));
        room.setMaster(masterName);
        room.setRoomName(roomName);
        //设置roomMember类
        Room save = roomRepository.save(room);
        int roomId = save.getId();

        RoomMember roomMember = new RoomMember();
        roomMember.setMemberId(Integer.parseInt(masterId));
        roomMember.setRoomId(roomId);
        roomMemberRepository.save(roomMember);

        return roomRepository.save(room);
    }
}
