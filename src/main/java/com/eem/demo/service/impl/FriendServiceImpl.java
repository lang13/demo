package com.eem.demo.service.impl;

import com.eem.demo.entity.Friend;
import com.eem.demo.repository.FriendRepository;
import com.eem.demo.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Administrator
 */
@Service
public class FriendServiceImpl implements FriendService {
    @Autowired
    FriendRepository friendRepository;

    @Override
    public int addFriend(String userId, String friendId) {
        int user = Integer.valueOf(userId);
        int fended = Integer.valueOf(friendId);
        //新建Friend类,存入数据
        Friend friend1 = new Friend();
        //数据库中,userId<friendId
        if(user <= fended){
            friend1.setUserId(user);
            friend1.setFriendId(fended);
        }else{
            friend1.setUserId(fended);
            friend1.setUserId(user);
        }
        //插入数据
        //插入数据前先判断是否已经有数据
        Friend isFriend = friendRepository.isFriend(userId, friendId);
        if(isFriend == null){
            Friend friend = friendRepository.save(friend1);
            if (friend != null){
                return 1;
            }else{
                return 0;
            }
        }else{
            return 1;
        }
    }

    @Override
    public List<Integer> findFriendId(String userId) {
        return friendRepository.findFriendIdByUserId(userId);
    }
}
