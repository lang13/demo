package com.eem.demo.repository;

import com.eem.demo.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @author Administrator
 */
public interface FriendRepository extends JpaRepository<Friend, Integer> {

    /**
     * 根据用户id和朋友id查找
     * @param userId
     * @param friendId
     * @return
     */
    @Query(value = "SELECT * FROM friend WHERE friend_id = ?1 AND user_id = ?2\n" +
            "UNION ALL\n" +
            "SELECT * FROM friend WHERE friend_id = ?2 AND user_id = ?1",nativeQuery = true)
    public Friend isFriend(String userId, String friendId);
}
