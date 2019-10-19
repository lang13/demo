package com.eem.demo.repository;

import com.eem.demo.entity.RoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author Administrator
 */
public interface RoomMemberRepository extends JpaRepository<RoomMember, Integer> {
    /**
     * 根据群聊id查询群聊用户id
     * @param roomId
     * @return
     */
    @Query(value = "SELECT member_id FROM room_member WHERE room_id = ?1", nativeQuery = true)
    public List<Integer> findMemberId(String roomId);

    /**
     * 根据房间id和用户id
     * 查询用户是否已在群聊
     * @param memberId
     * @param roomId
     * @return
     */
    public RoomMember findRoomMemberByMemberIdAndRoomId(Integer memberId, Integer roomId);


}
