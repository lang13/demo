package com.eem.demo.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eem.demo.service.RoomService;
import com.eem.demo.util.SpringUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * showdoc
 * @catalog EMM考核项目/websocket
 * @title 群聊
 * @description 群聊发送信息的websocket
 * @method websocket
 * @url ws://2700v9g607.zicp.vip:18340/websocket/room/{roomId}/{username}
 * @param roomId 必须 string 群聊id
 * @param username 必须 string 自己的用户名
 * @remark websocket.send()方法发送的必须是json字符串,不能是json对象.字符串中,必须包含roomId,username,发送文本信息时,type的值必须为"msg"
 * @author Administrator
 */
@ServerEndpoint("/websocket/room/{roomId}/{username}")
@Component
public class RoomWebSocket {

    private static RoomService roomServiceImpl = (RoomService) SpringUtil.getBean("roomServiceImpl");

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
    private static Map<String, List<String>> membersName;
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
    private String roomId;

    @OnOpen
    public void onOpen(@PathParam("username") String username, @PathParam("roomId") String roomId,
                       Session session){
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
        this.roomId = roomId;

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
                    if("msg".equals(msg.getString("type"))){
                        saveRecord(msg, msg.getString("toRoom"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //发送完成就删除temp的内容
            temp.remove(key);
        }
    }

    @OnClose
    public void onClose(){
        ConcurrentHashMap<String, Session> users = rooms.get(this.roomId);
        if (users.containsKey(this.username)){
            //移除自己的session
            users.remove(username);
            logger.info(this.roomId + "房间包含的用户: " + users);
        }
    }

    @OnMessage
    public void onMessage(String msg){
        logger.info("收到的msg为: " + msg);
        JSONObject object = JSON.parseObject(msg);
        String username = object.getString("fromName");
        String roomId = object.getString("toRoom");
        //发送信息
        sendMsg(username, roomId, object);
    }

    @OnError
    public void onError(Throwable error) {
        logger.info("发生错误" + new Date());
        error.printStackTrace();
    }

    /**
     * 发送信息时要将自己排除
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
                if ("msg".equals(msg.getString("type"))){
                    saveRecord(msg, roomId);
                }
            }
        };
        thread.start();
        //获取要发送信息的房间里的用户session
        ConcurrentHashMap<String, Session> users = rooms.get(roomId);
        //获取发送者的session
        Session mySession = users.get(username);

        //获取房间的群成员名字
        List<String> names = membersName.get(roomId);
        for (String name:names) {
            Session session = users.get(name);
            //不为空时发送信息
            if (session != null && !mySession.equals(session)){
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
