package com.eem.demo.websocket;

import com.alibaba.fastjson.JSONObject;
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
 * showdoc
 * @catalog EMM考核项目/websocket
 * @title 群聊的消息提醒
 * @description 群聊通知的websocket，受限于一个小程序页面最多只能打开5个websocket，因此多了此websocket接收群聊的信息，但不通过此websocket来发送信息
 * @method websocket
 * @url ws://2700v9g607.zicp.vip:18340/websocket/temp/{username}
 * @param username 必须 string 用户名
 * @remark 此websocket需要在登陆时就连接
 * @author Administrator
 */
@Component
@ServerEndpoint("/websocket/temp/{username}")
public class TempWebSocket {
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
        //放入session
        users.put(username,session);
    }

    @OnMessage
    public void onMessage(String msg, Session session){
        JSONObject object = JSONObject.parseObject(msg);
        //监测Ping
        if (object.get("type").equals("ping")) {
            object.put("type", "pong");
            session.getAsyncRemote().sendText(object.toJSONString());
            logger.info("TempWebSocket的心跳包");
        }
    }

    @OnClose
    public void onClose(@PathParam("username")String username){
        //除去session
        users.remove(username);
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
