package com.eem.demo.service;

import com.eem.demo.entity.State;

import java.util.List;

/**
 * @author Administrator
 */
public interface StateService {
    /**
     * 根据用户名修改用户状态
     * 在线,离线,隐身等
     * @param state
     * @param username
     * @return
     */
    int updateState(String state, String username);

    /**
     * 根据用户名查找用户好友的状态信息
     * @param userId
     * @return
     */
    List<State> findFriendState(String userId);
}
