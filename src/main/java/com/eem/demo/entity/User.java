package com.eem.demo.entity;

import com.eem.demo.util.Md5Util;

import javax.persistence.*;

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
    private String password;

    @Column
    private String photo;

    @Column
    private String gender;

    @Column
    private String address;

    @Column
    private String signature;

    @Transient
    private String state;

    @Transient
    private String memoName;

    @Transient
    private  String pinYin;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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
        this.password = Md5Util.getMd5(password);
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getMemoName() {
        return memoName;
    }

    public void setMemoName(String memoName) {
        this.memoName = memoName;
    }

    public String getPinYin() {
        return pinYin;
    }

    public void setPinYin(String pinYin) {
        this.pinYin = pinYin;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", photo='" + photo + '\'' +
                ", gender='" + gender + '\'' +
                ", address='" + address + '\'' +
                ", signature='" + signature + '\'' +
                ", state='" + state + '\'' +
                ", memoName='" + memoName + '\'' +
                ", pinYin='" + pinYin + '\'' +
                '}';
    }
}




