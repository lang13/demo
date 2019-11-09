package com.eem.demo.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eem.demo.entity.User;
import com.eem.demo.service.RoomService;
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
 * @author Administrator
 */
@ServerEndpoint("/websocket/test/{username}")
@Component
public class TestWebsocket {
    private static RoomService roomServiceImpl = (RoomService) SpringUtil.getBean("roomServiceImpl");
    private static UserService userServiceImpl = (UserService) SpringUtil.getBean("userServiceImpl");

    /**
     * 房间信息
     */
    private static Map<String, ConcurrentHashMap<String, Session>> rooms;
    static{
        rooms = new ConcurrentHashMap<>();
    }

    /**
     * 每个房间拥有的人
     */
    public static Map<String, List<String>> membersName;
    static{
        membersName = new ConcurrentHashMap<>();
    }
    /**
     * 存放未登录时接收到的信息
     */
    private static Map<String, List<JSONObject>> temp;

    static{
        temp = new ConcurrentHashMap();
    }
    private static Logger logger = Logger.getLogger(RoomWebSocket.class);
    private Session session;
    private String username;
    private List<Integer> roomIds;

    @OnOpen
    public void onOpen(@PathParam("username") String username, Session session){
        //获取用户已经加入的所有roomId
        User user = userServiceImpl.findByUsername(username);
        List<Integer> roomIds = roomServiceImpl.findRoomId(String.valueOf(user.getId()));
        this.roomIds = roomIds;

        //放入所有的Session
        for (Integer id:roomIds) {
            //房间id
            String roomId = String.valueOf(id);

            ConcurrentHashMap<String, Session> users = rooms.get(roomId);
            //判断房间是否是空的
            if(users == null){
                //如果是空的,就创建一个房间,用于存放session
                users = new ConcurrentHashMap<>();
                logger.info("创建房间" + roomId);
                rooms.put(roomId, users);
            }
            //把自己塞进房间
            users.put(username, session);
            //把房间塞入rooms
            rooms.put(roomId, users);
            logger.info(roomId + "房间包含的成员: " + users);
            //保存username,roomId,session,和members
            this.session = session;
            this.username = username;

            //建立一个Map,用于存放membersName
            List<String> names = membersName.get(roomId);
            if (names == null || names.isEmpty()){
                names = roomServiceImpl.findMemberName(roomId);
                logger.info("房间包含的用户名: " + names);
                membersName.put(roomId, names);
            }

            //获取temp查看是否有temp信息
            String key = username + "in" + roomId;
            List<JSONObject> jsonObjects = temp.get(key);
            if (jsonObjects != null && !jsonObjects.isEmpty()){
                logger.info("temp里面的内容: " + jsonObjects);
                for (int i = 0; i < jsonObjects.size(); i++) {
                    try {
                        JSONObject msg = jsonObjects.get(i);
                        //发送temp信息
                        this.session.getBasicRemote().sendText(msg.toString());
                        //保存聊天记录
                        saveRecord(msg, msg.getString("toRoom"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //发送完成就删除temp的内容
                temp.remove(key);
            }
        }
    }

    @OnClose
    public void onClose(){
        //移除所有的session
        for (Integer id:roomIds) {
            String roomId = String.valueOf(id);

            ConcurrentHashMap<String, Session> users = rooms.get(roomId);
            if (users.containsKey(this.username)){
                users.remove(username);
                logger.info(roomId + "房间包含的用户: " + users);
            }
        }
    }

    @OnMessage
    public void onMessage(String msg){
        JSONObject object = JSON.parseObject(msg);
        String username = object.getString("username");
        String roomId = object.getString("roomId");
        //监测Ping
        if (object.get("type").equals("ping")) {
            object.put("type", "pong");
            session.getAsyncRemote().sendText(object.toJSONString());
            logger.info(this.username + "的RoomWebSocket的心跳包");
            return;
        }
        logger.info("收到的msg为: " + object);

        //发送信息
        sendMsg(username, roomId, object);
    }

    @OnError
    public void onError(Throwable error) {
        logger.info("TestWebSocket发生错误" + new Date());
        error.printStackTrace();
    }

    /**
     * @param username
     * @param roomId
     * @param object
     */
    public static void sendMsg(String username, String roomId, JSONObject msg){
        //监测Pong
        if (msg.get("type").equals("ping")) {
            return;
        }

        //新建线程保存聊天记录
        Thread thread = new Thread(){
            @Override
            public void run() {
                saveRecord(msg, roomId);
            }
        };
        thread.start();
        //获取要发送信息的房间里的用户session
//        logger.info("调试: " + roomId);
        ConcurrentHashMap<String, Session> users = rooms.get(roomId);

        //获取房间的群成员名字
        List<String> names = membersName.get(roomId);
        for (String name:names) {
            Session session = users.get(name);
            //不为空时发送信息
            if (session != null){
                session.getAsyncRemote().sendText(msg.toJSONString());
            }
            //为空时存放temp
            if (session == null){
                //拼接房间用户的key
                String key = name + "in" + roomId;
                logger.info("key为: " + key);
                //获取temp
                List<JSONObject> jsonObjects = temp.get(key);
                //判空
                if (jsonObjects == null){
                    jsonObjects = new ArrayList<>();
                }
                jsonObjects.add(msg);
                temp.put(key, jsonObjects);
                logger.info("群聊temp的值为: " + temp);

                //消息提醒的websocket发送提醒信息
                TempWebSocket.sendMsg(msg, name);
            }
        }
    }

    /**
     * 将某人从某个群中移除
     * @param username
     * @param roomId
     */
    public static void deleteOne(String username, String roomId){
        ConcurrentHashMap<String, Session> users = rooms.get(roomId);
        if (users.containsKey(username)){
            users.remove(username);
        }
    }

    /**
     * 保存聊天记录
     * @param object
     * @param RoomId
     */
    private static void saveRecord(JSONObject object, String RoomId){
        //判断json数据里面的type类型
        List<String> types = new ArrayList<>();
        types.add("requestFriend");
        types.add("addFriend");
        types.add("deleteFriend");
        types.add("sendState");
        types.add("addRoomMember");
        types.add("deleteRoomMember");
        if (object.getString("type") == null || types.contains(object.getString("type"))){
            return;
        }

        File file = new File("C:/emm/record/room/"+RoomId+".txt");
        try(
                FileWriter fileWriter = new FileWriter(file,true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        ) {
            //保存记录
            bufferedWriter.write(","+object.toJSONString());
            bufferedWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
