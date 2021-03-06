package com.eem.demo.websocket;

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
 * showdoc
 * @catalog EMM考核项目/websocket
 * @title 群聊的消息提醒
 * @description 群聊通知的websocket，当初脑子抽了才这么写的,现在来不及改了
 * @method websocket
 * @url ws://2700v9g607.zicp.vip:18340/websocket/temp/{username}
 * @param username 必须 string 用户名
 * @remark 此websocket需要在登陆时就连接
 * @author Administrator
 */
@Component
@ServerEndpoint("/websocket/temp/{username}")
public class TempWebSocket {
    private String username;

    /**
     * 日志功能
     */
    private static Logger logger = Logger.getLogger(TempWebSocket.class);

    /**
     * 用户的session
     */
    private static Map<String, Session> users;
    static{
        users  = new ConcurrentHashMap<>();
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("username")String username){
        this.username = username;
        //放入session
        users.put(username,session);
        logger.info("TempWebSocket包含的成员: " + users);
    }

    @OnMessage
    public void onMessage(String msg, Session session){
        JSONObject object = JSONObject.parseObject(msg);
        //监测Ping
        if (object.get("type").equals("ping")) {
            object.put("type", "pong");
            session.getAsyncRemote().sendText(object.toJSONString());
            logger.info(this.username + "的TempWebSocket的心跳包");
        }
    }

    @OnClose
    public void onClose(@PathParam("username")String username){
        //除去session
        users.remove(username);
    }

    @OnError
    public void onError(Throwable error){
        logger.info("TempWebSocket发生错误" + new Date());
        error.printStackTrace();
    }

    /**
     * 发送信息的方法
     * @param msg
     */
    public static void sendMsg(JSONObject msg, String username){
        Session session = users.get(username);
        if (session != null){
            logger.info("TempWebSocket里面的msg: " + msg);
            session.getAsyncRemote().sendText(msg.toJSONString());
        }
    }
}
