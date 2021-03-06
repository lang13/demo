package com.eem.demo.service;

import com.eem.demo.entity.User;

import java.util.List;

/**
 * @author Administrator
 */
public interface UserService {
    /**
     * 判断用户是否已经注册
     * @param username
     * @return
     */
    boolean exists(String username);

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
     * @param username
     * @param userId
     * @return
     */
    int updateUsername(String username, String userId);

    /**
     * 根据用户名查找用户信息
     * @param username
     * @return
     */
    User findByUsername(String username);

    /**
     * 根据文件路径删除硬盘里的文件
     * @param filePath
     */
    void deleteFile(String filePath);

    /**
     * 根据账户id修改用户密码
     * @param password
     * @param userId
     * @return
     */
    int updatePassword(String password, String userId);

    /**
     * 修改用户信息
     * 除密码
     * @param user
     * @param userId
     * @return
     */
    User updateUser(User user, String userId);

    /**
     * 根据id集合查询User集合
     * @param userIds
     * @return
     */
    List<User> findAll(List<Integer> userIds);
}
