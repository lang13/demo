package com.eem.demo.repository;

import com.eem.demo.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Administrator
 */
public interface RoomRepository extends JpaRepository<Room, Integer> {

    /**
     * 根据房间名查询房间是否存在
     * @param roomName
     * @return
     */
    public Room findByRoomName(String roomName);
}
