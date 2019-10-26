package com.eem.demo.service.impl;

import com.eem.demo.entity.State;
import com.eem.demo.entity.User;
import com.eem.demo.repository.StateRepository;
import com.eem.demo.repository.UserRepository;
import com.eem.demo.service.FriendService;
import com.eem.demo.service.StateService;
import com.eem.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
@Service
public class StateServiceImpl implements StateService {
    @Autowired
    StateRepository stateRepository;

    @Autowired
    FriendService friendServiceImpl;

    @Autowired
    UserService userServiceImpl;
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateState(String state, String username) {
        return stateRepository.updateState(state,username);
    }

    @Override
    public List<State> findFriendState(String userId) {
        List<Integer> friendId = friendServiceImpl.findFriendId(userId);
        List<State> states = stateRepository.findAll(friendId);
        return states;
    }
}
