package com.eem.demo.service;

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
}
