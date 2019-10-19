package com.eem.demo.service;

import com.eem.demo.entity.Room;
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
}
