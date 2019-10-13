package com.eem.demo.repository;

import com.eem.demo.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @author Administrator
 */
public interface StateRepository extends JpaRepository<State,Integer> {

    /**
     * 根据用户名更改用户的状态信息
     * @param state
     * @param username
     * @return
     */
    @Modifying
    @Query(value = "update state set state = ?1 where username = ?2", nativeQuery = true)
    public int updateState(String state, String username);

    /**
     * 根据用户名查询用户状态信息
     * @param username
     * @return
     */
    public State findByUsername(String username);
}
