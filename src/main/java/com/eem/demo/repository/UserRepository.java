package com.eem.demo.repository;

import com.eem.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    @Modifying
    @Query(value = "update user set photo = ?1 where username = ?2", nativeQuery = true)
    public int updatePhoto(String filePath, String username);
}
