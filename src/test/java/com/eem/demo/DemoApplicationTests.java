package com.eem.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.eem.demo.entity.RoomMember;
import com.eem.demo.entity.State;
import com.eem.demo.entity.User;
import com.eem.demo.pojo.Message;
import com.eem.demo.repository.*;
import com.eem.demo.service.FriendService;
import com.eem.demo.service.RoomService;
import com.eem.demo.service.StateService;
import com.eem.demo.service.UserService;
import com.eem.demo.util.JwtUtil;
import com.eem.demo.util.Md5Util;
import com.google.gson.Gson;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.tree.DefaultDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
        List<State> states = stateServiceImpl.findFriendState("GDUT");
        System.out.println(states);
    }

    @Test
    public void test_08(){
        File file = new File("c:/emm/test/test.txt");
        if (!file.exists()){
            file.getParentFile().mkdirs();
        }
        try(
                FileWriter fileWriter = new FileWriter(file,true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                ){
            for (int i = 0; i < 10; i++) {
                Message message = new Message();
                message.setFrom(String.valueOf(i));
                message.setTo(String.valueOf(i+1));
                message.setMsg("这是信息内容: " + i);
                //转化为json字符窜
                String string = ",";
                string = string + JSON.toJSONString(message);
                //存入文件
                bufferedWriter.write(string);
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_09(){
        File file = new File("c:/emm/test/test.txt");
        try(
                FileReader fileReader = new FileReader(file);
                ){
            char[] chars = new char[(int)file.length()];
            fileReader.read(chars);
            String msg = new String(chars);
            msg = msg.replaceFirst(",","[");
            msg = msg + "]";
            //转化为json数组
            JSONArray objects = JSON.parseArray(msg);
            System.out.println(msg);
            System.out.println("object: " + objects);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_10(){
        String data = "2019/10/15";
        Date date = new Date();
        String from = "张三";
        String to = "李四";
        String msg = "这是聊天内容";
        String record = date + ": " +  "\"" + from + "\"" + "给" + "\"" + to + "\"" + "发送了: " +"\"" + msg + "\"";
//        String record = "%s: \"%s\" 给 \"%s\" 发送了: \"%s\" %n";
        System.out.printf(record,data,from,to,msg);
    }

    @Autowired
    RoomMemberRepository roomMemberRepository;

    @Autowired
    RoomService roomServiceImpl;
    @Test
    public void test_11(){
        System.out.println(roomServiceImpl.isMaster("张三", "2"));
    }

    @Test
    public void test_12(){
        File file = new File("C:/emm/record/room/1.txt");
        try {
            FileReader fileReader = new FileReader(file);
            char[] chars = new char[(int)file.length()];
            fileReader.read(chars);
            String msg = new String(chars);
            System.out.println(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_13(){
//        List<Object> objects = friendRepository.findFriend("3");
//        List<User> users = new ArrayList<>();
//        for (Object object: objects) {
//            Object[] rowArray = (Object[])object;
//
//            User user = new User();
//            Integer id = (Integer) rowArray[0];
//            String username = (String) rowArray[1];
//            String photo = (String) rowArray[2];
//            String address = (String) rowArray[3];
//            String gender = (String) rowArray[4];
//            String signature = (String) rowArray[5];
//            String state = (String) rowArray[6];
//            String memoName = friendRepository.findMemo("3","4");
//            user.setId(id);
//            user.setUsername(username);
//            user.setPhoto(photo);
//            user.setAddress(address);
//            user.setGender(gender);
//            user.setSignature(signature);
//            user.setState(state);
//            user.setMemoName(memoName);
//
//            users.add(user);
//        }
//        System.out.println(users);

        friendServiceImpl.updateMemo("2","3","小四");
        friendServiceImpl.updateMemo("3","2","小G");
        User user1 = friendServiceImpl.fiendFriend("2", "3");
        User user = friendServiceImpl.fiendFriend("3", "2");
        System.out.println(user);
        System.out.println(user1);
    }

    @Test
    public void test_14(){
        Specification<User> specification = new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Path<Object> username = root.get("username");
                Path<Object> id = root.get("id");

                Predicate p1 = criteriaBuilder.equal(username, "张三");
                Predicate p2 = criteriaBuilder.equal(id, "1");

                Predicate predicate = criteriaBuilder.or(p1, p2);
                return predicate;
            }
        };

        List<User> all = userRepository.findAll(specification);
        System.out.println(all);
    }
}
