package com.eem.demo.service;

import com.eem.demo.entity.User;

/**
 * @author Administrator
 */
public interface UserService {
    /**
     * 判断用户是否已经注册
     * @param username
     * @return
     */
    boolean isRegister(String username);

    /**
     * 根据用户名和密码新建用户
     * @param user
     * @return
     */
    User register(User user);


    /**
     * 根据账号密码登陆
     * @param username
     * @param password
     * @return
     */
    User login(String username, String password);

    /**
     * 根据用户名修改或者保存照片路径
     * @param filePath
     * @param username
     * @return
     */
    int updatePhoto(String filePath, String username);

    /**
     * 根据用户id修改昵称
     * @param nickname
     * @param userId
     * @return
     */
    int updateNickname(String nickname, String userId);
}
