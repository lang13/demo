package com.eem.demo.websocket;

import com.eem.demo.pojo.Message;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Administrator
 */
@ServerEndpoint("/websocket/user/{username}")
@Component
public class UserWebSocket {
    /**
     * 用于储存用户session
     * 验证用户是否在线
     * 以及用户发送信息
     */
    public static Map<String, Session> users = new ConcurrentHashMap<>();

    /**日志功能*/
    private static Logger logger = Logger.getLogger(UserWebSocket.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("username")String username){
        logger.info(username + "成功登陆!!!");
        //放入session
        users.put(username,session);
        logger.info(users);
    }

    @OnClose
    public void onClose(@PathParam("username")String username){
        logger.info(username + "断开连接!!!");
        //除去session
        users.remove("username");
    }

    /**
     * 前端发送的ws信息会在这个方法里面处理
     * 发送给前端不同的用户
     */
    @OnMessage
    public void onMessage(String msg){
        Gson gson = new Gson();
        Message message = gson.fromJson(msg, Message.class);
        logger.info("接收到的msg: " + message);
        //发送信息
        users.get(message.getTo()).getAsyncRemote().sendText(message.getMsg());
    }
}
