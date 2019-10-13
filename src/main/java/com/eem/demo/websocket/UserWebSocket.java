package com.eem.demo.websocket;

import com.eem.demo.pojo.Message;
import com.eem.demo.service.StateService;
import com.eem.demo.util.SpringUtil;
import com.google.gson.Gson;
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
@ServerEndpoint("/websocket/user/{username}")
@Component
public class UserWebSocket {
    //获取stateServiceImpl
    private StateService stateServiceImpl = (StateService)SpringUtil.getBean("stateServiceImpl");
    /**
     * 用于储存用户session
     * 验证用户是否在线
     * 以及用户发送信息
     */
    private static Map<String, Session> users = new ConcurrentHashMap<>();

    /**日志功能*/
    private static Logger logger = Logger.getLogger(UserWebSocket.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("username")String username){
        logger.info(username + "成功登陆!!!");
        //放入session
        users.put(username,session);
        //设置用户状态为在线
        stateServiceImpl.updateState("在线", username);

        logger.info(users);
    }

    @OnClose
    public void onClose(Session session, @PathParam("username")String username){
        logger.info(username + "断开连接!!!");
        //除去session
        users.remove(username);
        logger.info(users);

        //设置用户状态为离线
        stateServiceImpl.updateState("离线", username);
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
        if (users.get(message.getTo()) != null){
            users.get(message.getTo()).getAsyncRemote().sendText(message.getMsg());
        }else{
            users.get(message.getFrom()).getAsyncRemote().sendText("用户不在线");
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        logger.info("发生错误" + new Date());
        error.printStackTrace();
    }

}
