package com.eem.demo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.eem.demo.entity.Temp;
import com.eem.demo.repository.TempRepository;
import com.eem.demo.service.TempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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

    @Override
    public JSONArray requestRecord(String fileName) {
        File file = new File(fileName);
        if (!file.exists()){
            return null;
        }else{
            try(
                    FileReader fileReader = new FileReader(file);
                    ) {
                char[] chars = new char[(int)file.length()];
                fileReader.read(chars);
                String msg = new String(chars);
                msg = msg.replaceFirst(",","[");
                msg = msg + "]";
                //转化为json数组
                JSONArray objects = JSON.parseArray(msg);

                return objects;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
