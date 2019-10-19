package com.eem.demo.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Administrator
 */
@ServerEndpoint("/websocket/room/{roomId}/{username}")
@Component
public class RoomWebSocket {

    /**
     * 房间信息
     */
    private static Map<String, ConcurrentHashMap<String, Session>> rooms;
    static{
        rooms = new ConcurrentHashMap<>();
    }
    private Logger logger = Logger.getLogger(RoomWebSocket.class);
    private Session session;
    private String username;
    private String roomId;

    @OnOpen
    public void onOpen(@PathParam("username") String username, @PathParam("roomId") String roomId,
                       Session session){
        ConcurrentHashMap<String, Session> users = rooms.get("roomId");
        //判断房间是否是空的
        if(users == null){
            //如果是空的,就创建一个房间
            users = new ConcurrentHashMap<>();
            rooms.put(roomId, users);
        }
        //把自己塞进房间
        users.put(username, session);
        logger.info(roomId + "房间包含的成员: " + users);
        //保存username,roomId,和session
        this.session = session;
        this.username = username;
        this.roomId = roomId;
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
        JSONObject object = JSON.parseObject(msg);
        String username = object.getString("from");
        String roomId = object.getString("to");
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
    public static void sendMsg(String username, String roomId, JSONObject object){
        //获取要发送信息的房间里的用户
        ConcurrentHashMap<String, Session> users = rooms.get(roomId);
        //获取发送者的session
        Session mySession = users.get(username);
        //遍历users
        for (Session session: users.values()) {
            //发送信息时,将自己排除
            if (!mySession.equals(session)){
                session.getAsyncRemote().sendText(object.toJSONString());
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
}
