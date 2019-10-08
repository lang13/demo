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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private Integer userId;

    @Column
    private Integer friendId;

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
}
