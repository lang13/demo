package com.eem.demo;

import com.eem.demo.entity.User;
import com.eem.demo.repository.UserRepository;
import com.eem.demo.util.JwtUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {
    @Autowired
    UserRepository userRepository;

    @Test
    public void contextLoads() {
        User user = userRepository.findByUsername("王五");
        System.out.println(user);
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
}
