package com.eem.demo.service;

import com.eem.demo.entity.Temp;

public interface TempService {
    /**
     * 保存文件信息到数据库
     * @param file
     * @return
     */
    Temp saveFile(Temp file);

    /**
     * 根据文件id查找文件名
     * @param id
     * @return
     */
    String findFilePath(String id);
}
