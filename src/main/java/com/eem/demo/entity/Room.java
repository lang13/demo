package com.eem.demo.entity;

import javax.persistence.*;

/**
 * 房间基本信息表
 * 不包括群成员
 * @author Administrator
 */
@Table
@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String master;

    @Column
    private int masterId;

    @Column
    private String roomName;

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMasterId() {
        return masterId;
    }

    public void setMasterId(int masterId) {
        this.masterId = masterId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", master='" + master + '\'' +
                ", masterId=" + masterId +
                ", roomName='" + roomName + '\'' +
                '}';
    }
}
