package com.eem.demo;

import com.eem.demo.entity.Friend;
import com.eem.demo.entity.State;
import com.eem.demo.entity.User;
import com.eem.demo.repository.FriendRepository;
import com.eem.demo.repository.StateRepository;
import com.eem.demo.repository.UserRepository;
import com.eem.demo.service.FriendService;
import com.eem.demo.service.StateService;
import com.eem.demo.service.UserService;
import com.eem.demo.service.impl.FriendServiceImpl;
import com.eem.demo.util.JwtUtil;
import com.eem.demo.util.Md5Util;
import com.eem.demo.websocket.UserWebSocket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoApplicationTests {
    @Autowired
    StateRepository stateRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FriendRepository friendRepository;

    @Autowired
    UserService userServiceImpl;

    @Autowired
    FriendService friendServiceImpl;

    @Autowired
    StateService stateServiceImpl;
    @Test
    public void contextLoads() {
        List<State> all = stateRepository.findAll();
        for (int i = 0; i < all.size(); i++) {
            all.get(i).setState("离线");
            stateRepository.save(all.get(i));
        }
    }

    @Test
    public void test_01(){
        String username = JwtUtil.getUsername("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJsb2dpbk5hbWUiOiLlvKDkuIkiLCJleHAiOjE1NzA1MzMyNTUsInVzZXJJZCI6IjEifQ.B-TQ5sFMZ7ge4e7n8dzrG3t5wu_m-Da0RemQU_kR_M8");
        System.out.println(username);
    }

    @Test
    public void test_02(){
        for(int i = 0; i < 9; i++){
            for(int y = 0; y <= i; y++){
                System.out.printf("* \t");//打印 * 和空格
            }
            System.out.println();//换行
        }
    }

    @Test
    public void test_03(){
//        System.out.println("fuck");
        List<Integer> friendId = friendRepository.findFriendIdByUserId("3");
        System.out.println(friendId);
    }

    @Test
    public void test_04(){
        List<User> friend = userServiceImpl.findFriend("3");
        System.out.println(friend);
    }

    @Test
    public void test_05(){
        String msg = "123";
        String md5 = Md5Util.getMd5(msg);
        System.out.println(md5);
    }

    @Test
    public void test_06(){
        friendServiceImpl.deleteFriend("3","李四");
    }

    @Test
    public void test_07(){
        System.out.println(stateServiceImpl);
    }
}
