package com.eem.demo.service.impl;

import com.eem.demo.entity.Friend;
import com.eem.demo.entity.User;
import com.eem.demo.repository.FriendRepository;
import com.eem.demo.repository.UserRepository;
import com.eem.demo.service.FriendService;
import com.eem.demo.util.PinYinUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
@Service
public class FriendServiceImpl implements FriendService {
    @Autowired
    FriendRepository friendRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FriendService friendServiceImpl;

    @Override
    public User addFriend(String userId, String friendId) {
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
                return friendServiceImpl.findFriend(friendId, userId);
            }else{
                return null;
            }
        }else{
            return friendServiceImpl.findFriend(friendId, userId);
        }
    }

    @Override
    public List<Integer> findFriendId(String userId) {
        return friendRepository.findFriendIdByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFriend(String userId, String friendName) {
        //friendId < userId
        //查询好友id
        Integer friend = userRepository.findByUsername(friendName).getId();

        int user = Integer.parseInt(userId);
        if(friend < user){
            int i = friendRepository.deleteByUserIdAndFriendId(user, friend);
            if (i > 0){
                return 1;
            }else{
                return 0;
            }
        }else{
            int i = friendRepository.deleteByUserIdAndFriendId(friend, user);
            if (i > 0){
                return 1;
            }else{
                return 0;
            }
        }
    }

    @Override
    public List<User> findFriends(String userId) {
        List<Object> objects = friendRepository.findFriends(userId);
        List<User> users = new ArrayList<>();
        //将object类转化为User类
        for (Object object: objects) {
            Object[] rowArray = (Object[])object;

            User user = new User();
            Integer id = (Integer) rowArray[0];
            String username = (String) rowArray[1];
            String photo = (String) rowArray[2];
            String state = (String) rowArray[3];
            String memoName = (String)rowArray[4];

            user.setId(id);
            user.setUsername(username);
            user.setPhoto(photo);
            user.setState(state);
            user.setMemoName(memoName);
            //设置名字拼音
            user.setPinYin(PinYinUtil.toPinYin(username));
            if (memoName != null){
                user.setPinYin(PinYinUtil.toPinYin(memoName));
            }

            users.add(user);
        }
        return users;
    }

    @Override
    public User findFriend(String friendId, String userId) {
        List<Object> objects = friendRepository.findFriend(friendId);
        String memo = friendRepository.findMemo(friendId, userId);

        //先判断是否是好友
        Friend friend = friendRepository.isFriend(userId, friendId);
        if (friend == null){
            return null;
        }

        //对object进行解析
        User user = new User();
        for (Object object: objects) {
            Object[] rowArray = (Object[]) object;
            Integer id = (Integer) rowArray[0];
            String username = (String) rowArray[1];
            String photo = (String) rowArray[2];
            String address = (String) rowArray[3];
            String gender = (String) rowArray[4];
            String signature = (String) rowArray[5];
            String state = (String) rowArray[6];
            String memoName = memo;

            user.setId(id);
            user.setUsername(username);
            user.setPhoto(photo);
            user.setAddress(address);
            user.setGender(gender);
            user.setSignature(signature);
            user.setState(state);
            user.setMemoName(memoName);
            //设置名字拼音
            user.setPinYin(PinYinUtil.toPinYin(username));
            if (memoName != null){
                user.setPinYin(PinYinUtil.toPinYin(memoName));
            }
        }
        return user;
    }

    @Override
    public User updateMemo(String friendId, String userId, String memo) {
        //friendId < userId
        Friend friend = friendRepository.isFriend(userId, friendId);
        if (friend == null){
            return null;
        }else{
            int f_id = Integer.parseInt(friendId);
            int u_id = Integer.parseInt(userId);
            if (f_id < u_id){
                friend.setUserMemo(memo);
            }else{
                friend.setFriendMemo(memo);
            }
            friendRepository.save(friend);
        }
        return findFriend(friendId, userId);
    }
}
