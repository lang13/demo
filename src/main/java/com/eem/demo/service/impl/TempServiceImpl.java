package com.eem.demo.service.impl;

import com.eem.demo.entity.Temp;
import com.eem.demo.repository.TempRepository;
import com.eem.demo.service.TempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Administrator
 */
@Service
public class TempServiceImpl implements TempService {
    @Autowired
    TempRepository tempRepository;

    @Override
    public Temp saveFile(Temp file) {
        return tempRepository.save(file);
    }

    @Override
    public String findFilePath(String id) {
        Temp one = tempRepository.findOne(Integer.valueOf(id));
        return one.getFilePath();
    }
}
