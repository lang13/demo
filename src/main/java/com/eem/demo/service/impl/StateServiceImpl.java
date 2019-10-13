package com.eem.demo.service.impl;

import com.eem.demo.repository.StateRepository;
import com.eem.demo.service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Administrator
 */
@Service
public class StateServiceImpl implements StateService {
    @Autowired
    StateRepository stateRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateState(String state, String username) {
        return stateRepository.updateState(state,username);
    }
}
