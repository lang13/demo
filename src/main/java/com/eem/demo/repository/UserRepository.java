package com.eem.demo.repository;

import com.eem.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * jpa
 * @author Administrator
 */
public interface UserRepository extends JpaRepository<User,Integer> {
    /**
     * 根据用户名和密码查询用户
     * @param username
     * @param password
     * @return
     */
    public User findByUsernameAndPassword(String username,String password);

    /**
     * 根据用户名判断用户是否存在
     * @param username
     * @return
     */
    public boolean existsByUsername(String username);

    /**
     * 根据用户名更改照片路径
     * @param filePath
     * @param username
     * @return
     */
    @Modifying
    @Query(value = "update user set photo = ?1 where username = ?2", nativeQuery = true)
    public int updatePhoto(String filePath, String username);

    /**
     * 根据用户id修改用户名
     * @param username
     * @param userId
     * @return
     */
    @Modifying
    @Query(value = "update user set username = ?1 where id = ?2", nativeQuery = true)
    public int updateUsername(String username, String userId);

    /**
     * 根据用户id修改密码(功能未完成)
     * @param userId
     * @param password
     * @return
     */
    @Modifying
    @Query(value = "update user set password = ?1 where id = ?2", nativeQuery = true)
    public int updatePassword(String password, String userId);

    /**
     * 根据用户名查找用户信息
     * @param username
     * @return
     */
    public User findByUsername(String username);
}
