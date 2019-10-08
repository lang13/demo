package com.eem.demo.service.impl;

import com.eem.demo.entity.User;
import com.eem.demo.repository.UserRepository;
import com.eem.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Administrator
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public boolean isRegister(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public User register(User user) {
        return userRepository.save(user);
    }

    @Override
    public User login(String username, String password) {
        return userRepository.findByUsernameAndPassword(username,password);
    }

    @Override
    public int updatePhoto(String filePath, String username) {
        return userRepository.updatePhoto(filePath, username);
    }
}
