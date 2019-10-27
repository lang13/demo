package com.eem.demo.repository;

import com.eem.demo.entity.Friend;
import com.eem.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
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
     * 根据好友id集合查询好友集合
     * @param userId
     * @return
     */
    @Query(value =
            "SELECT u.`id`,u.`username`,u.`photo`,s.`state`,n.friend_memo FROM state s \n" +
            "RIGHT JOIN USER u ON u.`id` = s.`id`\n" +
            "RIGHT JOIN \n" +
            "(SELECT friend_id,user_memo AS friend_memo FROM friend WHERE user_id = ?1\n" +
            "UNION ALL\n" +
            "SELECT user_id,friend_memo FROM friend WHERE friend_id = ?1) AS n\n" +
            "ON u.`id` = n.friend_id\n" +
            "WHERE u.`id` IN(n.friend_id)", nativeQuery = true)
    public List<Object> findFriends(String userId);

    /**
     * 根据用户id和好友id删除好友关系
     * @param userId
     * @param friendId
     * @return
     */
    public int deleteByUserIdAndFriendId(int userId, int friendId);

    /**
     * 根据用户id查询用户基础信息
     * 不包括标注名
     * @param friendId
     * @return
     */
    @Query(value =
            "SELECT u.`id`,u.`username`,u.`photo`,u.address,u.gender,u.signature,s.`state` FROM state s\n" +
            "RIGHT JOIN USER u ON u.`id` = s.`id`\n" +
            "WHERE u.`id` = ?1", nativeQuery = true)
    public List<Object> findFriend(String friendId);

    /**
     * 查询用户好友的备注名
     * @param friendId
     * @param userId
     * @return
     */
    @Query(value =
            "SELECT friend_memo FROM friend WHERE user_id = ?1 AND friend_id = ?2\n" +
            "UNION ALL\n" +
            "SELECT user_memo AS friend_memo FROM friend WHERE friend_id = ?1 AND user_id = ?2", nativeQuery = true)
    public String findMemo(String friendId, String userId);
}
