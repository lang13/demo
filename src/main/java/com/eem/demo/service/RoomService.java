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
     * @param userId
     * @return
     */
    List<User> findRoomMember(String roomId, String userId);

    /**
     * 查询群聊的群主
     * @param roomId
     * @return
     */
    User findRoomMaster(String roomId);

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

    /**
     * 更改群名
     * @param roomName
     * @param roomId
     * @return
     */
    int updateRoomName(String roomName, String roomId);

    /**
     * 修改群管理员
     * @param masterName
     * @param roomId
     * @return
     */
    int updateRoomMaster(String masterName, String roomId);

    /**
     * 查询是否是群管理员
     * @param username
     * @param roomId
     * @return
     */
    boolean isMaster(String username, String roomId);

    /**
     * 根据用户id查询用户已加入的群聊
     * @param memberId
     * @return
     */
    List<Integer> findRoomId(String memberId);

    /**
     * 是否存在这个房间
     * @param roomId
     * @return
     */
    boolean exists(String roomId);

    /**
     * 查找房间里的用户id
     * @param roomId
     * @return
     */
    List<String> findMemberName(String roomId);

    /**
     * 查询群聊名
     * @param roomId
     * @return
     */
    String findRoomName(String roomId);
}
