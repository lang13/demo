package com.eem.demo.service.impl;

import com.eem.demo.entity.Room;
import com.eem.demo.entity.RoomMember;
import com.eem.demo.entity.User;
import com.eem.demo.repository.RoomMemberRepository;
import com.eem.demo.repository.RoomRepository;
import com.eem.demo.repository.UserRepository;
import com.eem.demo.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Administrator
 */
@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    RoomRepository roomRepository;

    @Autowired
    RoomMemberRepository roomMemberRepository;

    @Autowired
    UserRepository userRepository;

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

    @Override
    public List<User> findRoomMember(String roomId) {
        List<Integer> memberId = roomMemberRepository.findMemberId(roomId);
        List<User> all = userRepository.findAll(memberId);
        return all;
    }

    @Override
    public User findRoomMaster(String roomId) {
        Room room = roomRepository.findOne(Integer.valueOf(roomId));
        int id = room.getId();
        User one = userRepository.findOne(id);
        return one;
    }

    @Override
    public RoomMember addRoomMember(String username, String roomId) {
        User user = userRepository.findByUsername(username);
        RoomMember roomMember = new RoomMember();
        //判断是否已经在群聊
        RoomMember member =
                roomMemberRepository.findRoomMemberByMemberIdAndRoomId(user.getId(), Integer.valueOf(roomId));
        if (member == null){
            //设置roomMember类
            roomMember.setMemberId(user.getId());
            roomMember.setRoomId(Integer.parseInt(roomId));
            //保存roomMember类
            return roomMemberRepository.save(roomMember);
        }else{
            return member;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteMember(String username, String roomId) {
        User user = userRepository.findByUsername(username);
        int i = roomMemberRepository.deleteByMemberIdAndRoomId(user.getId(), Integer.valueOf(roomId));
        return i;
    }
}
