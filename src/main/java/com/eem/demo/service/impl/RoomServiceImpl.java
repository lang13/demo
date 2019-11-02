package com.eem.demo.service.impl;

import com.eem.demo.entity.Room;
import com.eem.demo.entity.RoomMember;
import com.eem.demo.entity.User;
import com.eem.demo.repository.RoomMemberRepository;
import com.eem.demo.repository.RoomRepository;
import com.eem.demo.repository.UserRepository;
import com.eem.demo.service.FriendService;
import com.eem.demo.service.RoomService;
import com.eem.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Autowired
    FriendService friendServiceImpl;

    @Autowired
    UserService userServiceImpl;

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
    public List<User> findRoomMember(String roomId, String userId) {
        //房间里的所有用户id
        List<Integer> memberId = roomMemberRepository.findMemberId(roomId);
        //声明一个ArrayList存放所有的用户
        List<User> all;

        //存放好友Id
        List<Integer> friendIds = new ArrayList<>();
        //存放普通id
        List<Integer> userIds = new ArrayList<>();

        //遍历memberId,查询是否是好友关系
        //如果是,就查询信息然后放入all中
        for (int i = 0; i < memberId.size(); i++) {
            String friendId = memberId.get(i).toString();
            if (friendServiceImpl.isFriend(userId, friendId)){
                friendIds.add(memberId.get(i));
            }else{
                userIds.add(memberId.get(i));
            }
        }
        //普通用户
        all = userServiceImpl.findAll(userIds);
        //好友用户
        List<User> friends = new ArrayList<>();
        for (int i = 0; i < friendIds.size(); i++) {
            String friendId = String.valueOf(friendIds.get(i));
            User friend = friendServiceImpl.findFriend(friendId, userId);
            friends.add(friend);
        }
        //合并
        all.addAll(friends);
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRoomName(String roomName, String roomId) {
        return roomRepository.updateRoomName(roomName,roomId);
    }

    @Override
    public int updateRoomMaster(String masterName, String roomId) {
        User user = userRepository.findByUsername(masterName);
        int i =roomRepository.updateRoomMaster(String.valueOf(user.getId()), user.getUsername(), roomId);
        return i;
    }

    @Override
    public boolean isMaster(String username, String roomId) {
        Room room = roomRepository.findByMasterAndId(username, Integer.valueOf(roomId));
        if (room != null){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public List<Integer> findRoomId(String memberId) {
        return roomMemberRepository.findRoomId(memberId);
    }

    @Override
    public boolean exists(String roomId) {
        Room one = roomRepository.getOne(Integer.valueOf(roomId));
        if (one != null){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public List<String> findMemberName(String roomId) {
        List<Integer> memberId = roomMemberRepository.findMemberId(roomId);
        List<User> all = userRepository.findAll(memberId);
        //用户名
        List<String> userNames = new ArrayList<>();
        for (User user:all) {
            userNames.add(user.getUsername());
        }
        return userNames;
    }
}
