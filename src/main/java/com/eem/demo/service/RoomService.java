package com.eem.demo.service;

import com.eem.demo.entity.Room;
import com.eem.demo.entity.RoomMember;
import com.eem.demo.entity.User;

import java.util.List;

/**
 * @author Administrator
 */
public interface RoomService {
    /**
     * 利用群名和群主名创建一个群聊
     * 返回的是群聊信息
     * 包括群聊名和群聊id
     * @param roomName
     * @param masterName
     * @param masterId
     * @return
     */
    Room createRoom(String roomName, String masterName, String masterId);


    /**
     * 根据群聊id查询群聊成员
     * @param roomId
     * @return
     */
    List<User> findRoomMember(String roomId);

    /**
     * 根据用户名和房间号
     * 拉用户进入聊天群
     * @param username
     * @param roomId
     * @return RoomMember
     */
    RoomMember addRoomMember(String username, String roomId);

    /**
     * 根据用户id和房间id
     * 把用户移除群聊
     * @param username
     * @param roomId
     * @return
     */
    int deleteMember(String username, String roomId);
}
