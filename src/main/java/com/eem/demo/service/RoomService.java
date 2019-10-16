package com.eem.demo.service;

import com.eem.demo.entity.Room;

/**
 * @author Administrator
 */
public interface RoomService {
    /**
     * 利用群名和群主名创建一个群聊
     * @param roomName
     * @param masterName
     * @return
     */
    Room createRoom(String roomName, String masterName);
}
