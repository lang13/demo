package com.eem.demo.entity;

import javax.persistence.*;

/**
 * 群聊成员表
 * @author Administrator
 */
@Table
@Entity
public class RoomMember{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private int roomId;

    @Column
    private int memberId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    @Override
    public String toString() {
        return "RoomMember{" +
                "id=" + id +
                ", roomId=" + roomId +
                ", memberId=" + memberId +
                '}';
    }
}
