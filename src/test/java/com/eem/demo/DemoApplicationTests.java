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
        int i = userRepository.updatePhoto("C:/emm/1875b54c-252f-431f-936a-3156f611e143.jpg", "张三");
    }

    @Test
    public void test_01(){
        String username = JwtUtil.getUsername("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJsb2dpbk5hbWUiOiLlvKDkuIkiLCJleHAiOjE1NzA1MzMyNTUsInVzZXJJZCI6IjEifQ.B-TQ5sFMZ7ge4e7n8dzrG3t5wu_m-Da0RemQU_kR_M8");
        System.out.println(username);
    }
}
