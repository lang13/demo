package com.eem.demo.repository;

import com.eem.demo.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

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

    /**
     * 根据房间id修改房间名字
     * @param roomName
     * @param roomId
     * @return
     */
    @Modifying
    @Query(value = "update room set room_name = ?1 where id = ?2", nativeQuery = true)
    public int updateRoomName(String roomName, String roomId);

    /**
     * 修改房间的管理员姓名和管理员id
     * @param masterId
     * @param masterName
     * @param roomId
     * @return
     */
    @Query(value = "update room set master_id = ?1, master = ?2 where id = ?3", nativeQuery = true)
    public int updateRoomMaster(String masterId, String masterName, String roomId);

    /**
     * 根据名字查询是否是管理员
     * @param master
     * @param id
     * @return
     */
    public Room findByMasterAndId(String master, Integer id);
}
