package com.eem.demo.service;

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
}
