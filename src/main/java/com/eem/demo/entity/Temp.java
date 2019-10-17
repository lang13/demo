package com.eem.demo.entity;

import javax.persistence.*;

/**
 * @author Administrator
 */
@Table
@Entity
public class Temp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String sender;

    @Column
    private int senderId;

    @Column
    private String receive;

    @Column
    private int receiveId;

    @Column
    private String filePath;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getReceive() {
        return receive;
    }

    public void setReceive(String receive) {
        this.receive = receive;
    }

    public int getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(int receiveId) {
        this.receiveId = receiveId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
