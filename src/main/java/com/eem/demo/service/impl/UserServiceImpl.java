package com.eem.demo.service.impl;

import com.eem.demo.entity.State;
import com.eem.demo.entity.User;
import com.eem.demo.repository.FriendRepository;
import com.eem.demo.repository.StateRepository;
import com.eem.demo.repository.UserRepository;
import com.eem.demo.service.UserService;
import com.eem.demo.util.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    StateRepository stateRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FriendRepository friendRepository;

    @Override
    public boolean exists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public User register(User user) {
        //设置用户状态
        State state = new State();
        state.setState("离线");
        state.setUsername(user.getUsername());
        stateRepository.save(state);

        return userRepository.save(user);
    }

    @Override
    public User login(String username, String password) {
        User user = userRepository.findByUsernameAndPassword(username, password);
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePhoto(String filePath, String username) {
        return userRepository.updatePhoto(filePath, username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateUsername(String nickname, String userId) {
        return userRepository.updateUsername(nickname,userId);
    }

    @Override
    public User findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        user.setPassword(null);
        return user;
    }

    @Override
    public void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()){
            file.delete();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePassword(String password, String userId) {
        password = Md5Util.getMd5(password);
        return userRepository.updatePassword(password, userId);
    }

    @Override
    public User updateUser(User user, String userId) {
        User one = userRepository.findOne(Integer.valueOf(userId));
        //判断传入的用户信息
        if (user.getSignature() != null){
            one.setSignature(user.getSignature());
        }
        if(user.getAddress() != null){
            one.setAddress(user.getAddress());
        }
        if (user.getGender() != null){
            one.setGender(user.getGender());
        }
        return userRepository.save(one);
    }
}
