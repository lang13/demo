package com.eem.demo.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eem.demo.entity.User;
import com.eem.demo.service.FriendService;
import com.eem.demo.service.StateService;
import com.eem.demo.service.UserService;
import com.eem.demo.util.SpringUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * showdoc
 * @catalog EMM考核项目/websocket
 * @title 单对单聊天
 * @method websocket
 * @url ws://2700v9g607.zicp.vip:18340/websocket/websocket/user/{username}
 * @param username 必须 string 用户名
 * @remark websocket.send()方法接收的是json字符串,不能是json对象,json字符串中,必须包含toName(接收者用户名),toId(接收者id),fromName(发送者用户名),fromId(发送者Id),发送文本信息时type的值必须是"msg"
 * @author Administrator
 */
@Component
@ServerEndpoint("/websocket/user/{username}")
public class UserWebSocket {
    /**获取stateServiceImpl*/
    private static StateService stateServiceImpl = (StateService)SpringUtil.getBean("stateServiceImpl");
    private static UserService userServiceImpl = (UserService) SpringUtil.getBean("userServiceImpl");
    private static FriendService friendServiceImpl = (FriendService) SpringUtil.getBean("friendServiceImpl");
    /**
     * 用于储存用户session
     * 验证用户是否在线
     * 以及用户发送信息
     */

    private static Map<String, Session> users;
    static{
        users  = new ConcurrentHashMap<>();
    }

    /**
     * 用于储存用户不在线时接收到的数据
     */
    private static Map<String, List<JSONObject>> temp;
    static{
        temp = new ConcurrentHashMap<>();
    }

    /**日志功能*/
    private static Logger logger = Logger.getLogger(UserWebSocket.class);

    private List<String> friends;

    private String username;

    private String id;

    @OnOpen
    public void onOpen(Session session, @PathParam("username")String username){
        //获取好友列表
        friends = findFriends(username);
        this.username = username;
        this.id = String.valueOf(userServiceImpl.findByUsername(username).getId());

        logger.info(username + "登陆");
        logger.info(username + "的好友列表: " + friends);
        //给好友发送在线信息
        sendState(this.friends, "在线", this.username, this.id);

        //放入session
        users.put(username,session);
        //设置用户状态为在线
        stateServiceImpl.updateState("在线", username);
        //发送不在线时收到的数据
        if (temp.get(username) != null){
            List<JSONObject> jsonObjects = temp.get(username);
            logger.info("获取到的jsonObjects: " + jsonObjects);
            for (int i = 0; i < jsonObjects.size(); i++) {
                try {
                    JSONObject msg = jsonObjects.get(i);
                    //同步异步的区别
                    users.get(username).getBasicRemote().sendText(msg.toString());
                    //保存聊天记录
                    if ("msg".equals(msg.getString("type")) || "file".equals(msg.getString("type"))){
                        saveRecord(msg, msg.getString("toId"), msg.getString("formId"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //发送完就删除temp里面的内容
            temp.remove(username);
        }
        logger.info(users);
    }

    @OnClose
    public void onClose(@PathParam("username")String username){
        logger.info(username + "断开连接!!!");
        //除去session
        users.remove(username);
        logger.info(users);

        //给好友发送在线信息
        sendState(this.friends, "离线", this.username, this.id);

        //设置用户状态为离线
        stateServiceImpl.updateState("离线", username);
    }

    /**
     * 前端发送的ws信息会在这个方法里面处理
     * 发送给前端不同的用户
     * 打算弃用这个方式来接收信息
     */
    @OnMessage
    public void onMessage(String msg, Session session){

        logger.info("接收到的msg: " + msg);
        JSONObject jsonObject = JSON.parseObject(msg);
        logger.info("转化为json格式后的msg: " + jsonObject);

        //监测Pong
        if (jsonObject.get("type").equals("ping")) {
            jsonObject.put("type", "pong");
            session.getAsyncRemote().sendText(jsonObject.toJSONString());
        }

        //发送信息
        sendMsg(jsonObject.getString("toName"), jsonObject);
    }

    @OnError
    public void onError(Throwable error) {
        logger.info("发生错误" + new Date());
        error.printStackTrace();
    }

    /**
     * 发送信息的方法
     * 单独抽出来
     * @param toName
     * @param msg
     */
    public static void sendMsg(String toName, JSONObject msg){

        //新建线程保存聊天记录
        Thread thread = new Thread(){
            @Override
            public void run() {
                if ("msg".equals(msg.getString("type"))){
                    saveRecord(msg, (String)msg.get("toId"), (String)msg.get("fromId"));
                }
            }
        };
        thread.start();
        Session session = users.get(toName);
        if (session != null){
            session.getAsyncRemote().sendText(msg.toJSONString());
        }else{
            if (null == temp.get(toName)){
                List<JSONObject> jsonObjects = new ArrayList<>();
                jsonObjects.add(msg);
                //添加进去
                temp.put(toName,jsonObjects);
                logger.info("temp里面的内容: " + temp);
            }else{
                List<JSONObject> jsonObjects = temp.get(toName);
                jsonObjects.add(msg);
                //再放回去
                temp.put(toName,jsonObjects);
                logger.info("temp里面的内容: " + temp);
            }
        }
    }

    /**
     * 保存聊天记录
     * @param object
     * @param toId
     * @param fromId
     */
    private static void saveRecord(JSONObject object, String toId, String fromId){
        //新建文件类
        String fileName;
        if (Integer.parseInt(toId) < Integer.parseInt(fromId)){
            fileName = toId + "and" + fromId;
        }else{
            fileName = fromId + "and" + toId;
        }
        File file = new File("c:/emm/record/user/" + fileName + ".txt");
        try (
                FileWriter fileWriter = new FileWriter(file, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)
                ){
            //保存记录
            bufferedWriter.write(","+object.toJSONString());
            bufferedWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据用户名查抄用户的好友的username
     * @param username
     * @return
     */
    private static List<String> findFriends(String username){
        List<String> friends = new ArrayList<>();

        User user = userServiceImpl.findByUsername(username);
        List<User> friend = friendServiceImpl.findFriends(String.valueOf(user.getId()));
        for (int i = 0; i < friend.size(); i++) {
            friends.add(friend.get(i).getUsername());
        }
        return friends;
    }

    /**
     * 向好友发送状态信息
     * @param friends
     * @param state
     */
    public static void sendState(List<String> friends, String state, String username, String id){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "sendState");
        jsonObject.put("id", id);
        jsonObject.put("username", username);
        jsonObject.put("state", state);

        //发送给好友
        for (int i = 0; i < friends.size(); i++) {
            Session session = users.get(friends.get(i));
            if (session != null){
                try {
                    session.getBasicRemote().sendText(jsonObject.toJSONString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

