package com.eem.demo.entity;

import javax.persistence.*;
import java.util.List;

/**
 * 这是一个数据库表的类
 *
 * @author Administrator
 */
@Entity
@Table
public class User {
    @Id
    /** 这是一个主键*/
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /**设置为自增*/
    private Integer id;

    @Column
    private String username;

    @Column
    private String nickname;

    @Column
    private String password;

    @Column
    private String photo;

    /**
     * 不在数据库中创建这个列
     */
    @Transient
    private List<User> friends;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", password='" + password + '\'' +
                ", photo='" + photo + '\'' +
                ", friends=" + friends +
                '}';
    }
}




