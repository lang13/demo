package com.eem.demo.repository;

import com.eem.demo.entity.RoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Administrator
 */
public interface RoomMemberRepository extends JpaRepository<RoomMember, Integer> {

}
