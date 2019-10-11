package com.eem.demo.repository;

import com.eem.demo.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author Administrator
 */
public interface FriendRepository extends JpaRepository<Friend, Integer> {

    /**
     * 根据用户id和朋友id查找,
     * 用于判断两人是否是好友关系
     * @param userId
     * @param friendId
     * @return
     */
    @Query(value = "SELECT * FROM friend WHERE friend_id = ?1 AND user_id = ?2\n" +
                   "UNION ALL\n" +
                   "SELECT * FROM friend WHERE friend_id = ?2 AND user_id = ?1",nativeQuery = true)
    public Friend isFriend(String userId, String friendId);

    /**
     * 根据用户id查找该用户的好友id
     * @param userId
     * @return
     */
    @Query(value = "SELECT friend_id FROM friend WHERE user_id = ?1\n" +
                   "UNION ALL\n" +
                   "SELECT user_id AS friendId FROM friend WHERE friend_id = ?1",nativeQuery = true)
    public List<Integer> findFriendIdByUserId(String userId);

    /**
     * 根据用户id和好友id删除好友关系
     * @param userId
     * @param friendId
     * @return
     */
    public int deleteByUserIdAndFriendId(int userId, int friendId);

}
