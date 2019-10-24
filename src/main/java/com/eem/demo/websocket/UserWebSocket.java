package com.eem.demo.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eem.demo.service.StateService;
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
@Component
@ServerEndpoint("/websocket/user/{username}")
public class UserWebSocket {
    /**获取stateServiceImpl*/
    private StateService stateServiceImpl = (StateService)SpringUtil.getBean("stateServiceImpl");
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

    @OnOpen
    public void onOpen(Session session, @PathParam("username")String username){
        logger.info(username + "成功登陆!!!");
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
                    //同步异步的区别
                    users.get(username).getBasicRemote().sendText(jsonObjects.get(i).toString());
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

        //设置用户状态为离线
        stateServiceImpl.updateState("离线", username);
    }

    /**
     * 前端发送的ws信息会在这个方法里面处理
     * 发送给前端不同的用户
     * 打算弃用这个方式来接收信息
     */
    @OnMessage
    public void onMessage(String msg){
        logger.info("接收到的meg: " + msg);
        JSONObject jsonObject = JSON.parseObject(msg);
        logger.info("转化为json格式后的msg: " + jsonObject);
        //发送信息
        sendMsg(jsonObject.getString("to"), jsonObject);
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
}

