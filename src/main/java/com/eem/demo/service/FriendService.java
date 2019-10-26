package com.eem.demo.service;

import com.eem.demo.entity.User;

import java.util.List;

/**
 * @author Administrator
 */
public interface FriendService {
    /**
     * 添加好友,需要两个用户id
     * @param userId
     * @param friendId
     * @return
     */
    int addFriend(String userId, String friendId);

    /**
     * 根据用户id查询好友id集合
     * @param userId
     * @return
     */
    List<Integer> findFriendId(String userId);

    /**
     * 删除好友关系,需要用户id和好友姓名
     * @param userId
     * @param friendName
     * @return
     */
    int deleteFriend(String userId, String friendName);

    /**
     * 根据用户id查找用户的好友集
     * @param userId
     * @return
     */
    List<User> findFriends(String userId);
}
