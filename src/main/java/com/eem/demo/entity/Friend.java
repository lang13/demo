package com.eem.demo.entity;

import javax.persistence.*;

/**
 * 好友关系表
 * @author Administrator
 */
@Entity
@Table
public class Friend {
    @Id
    /** 这是一个主键*/
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /**设置为自增*/
    private Integer id;

    @Column
    private Integer userId;

    @Column
    private Integer friendId;

    @Column
    private String userMemo;

    @Column
    private String friendMemo;

    /**
     * 不在数据库中创建这个列
     */
    @Transient
    private Integer myFriend;

    public Integer getMyFriend() {
        return myFriend;
    }

    public void setMyFriend(Integer myFriend) {
        this.myFriend = myFriend;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getFriendId() {
        return friendId;
    }

    public void setFriendId(Integer friendId) {
        this.friendId = friendId;
    }

    public String getUserMemo() {
        return userMemo;
    }

    public void setUserMemo(String userMemo) {
        this.userMemo = userMemo;
    }

    public String getFriendMemo() {
        return friendMemo;
    }

    public void setFriendMemo(String friendMemo) {
        this.friendMemo = friendMemo;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "id=" + id +
                ", userId=" + userId +
                ", friendId=" + friendId +
                ", userMemo='" + userMemo + '\'' +
                ", friendMemo='" + friendMemo + '\'' +
                ", myFriend=" + myFriend +
                '}';
    }
}
