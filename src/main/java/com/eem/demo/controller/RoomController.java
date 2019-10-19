package com.eem.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.eem.demo.entity.Room;
import com.eem.demo.entity.RoomMember;
import com.eem.demo.entity.User;
import com.eem.demo.pojo.ReturnObj;
import com.eem.demo.service.RoomService;
import com.eem.demo.service.UserService;
import com.eem.demo.util.JwtUtil;
import com.eem.demo.websocket.UserWebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Administrator
 */
@RestController
public class RoomController {
    @Autowired
    RoomService roomServiceImpl;

    @Autowired
    UserService userServiceImpl;

    /**
     * 根据管理员名字和群名创建群聊
     * @param roomName
     * @return
     */
    @RequestMapping("/createRoom")
    public ReturnObj createRoom(String roomName, HttpServletRequest request){
        ReturnObj obj;
        //获取token
        String token = request.getHeader("token");
        String masterName = JwtUtil.getUsername(token);
        String masterId = JwtUtil.getUserId(token);
        Room room = roomServiceImpl.createRoom(roomName, masterName, masterId);
        if (room != null){
            obj = ReturnObj.success();
            obj.setMsg("创建群成功!!!");
            obj.add("room",room);
        }else{
            obj = ReturnObj.fail();
        }
        return obj;
    }

    /**
     * 根据用户名和房间id
     * 拉用户进入群聊
     * @param username
     * @param roomId
     * @return
     */
    public ReturnObj addRoomMember(String username, String roomId, HttpServletRequest request){
        String token = request.getHeader("token");
        String from = JwtUtil.getUsername(token);
        ReturnObj obj;
        RoomMember roomMember = roomServiceImpl.addRoomMember(username, roomId);
        if (roomMember == null){
            obj = ReturnObj.fail();
        }else{
            obj = ReturnObj.success();
            //向被邀请者发送信息,告知他已经加入群聊,让小程序新建该房间的WebSocket
            JSONObject object = new JSONObject();
            object.put("type", "inform");
            object.put("value", from + "邀请你加入群聊");
            object.put("to", username);
            object.put("from", from);
            UserWebSocket.sendMsg(username, object);
        }
        return obj;
    }

    /**
     * 根据房间id查找房间成员
     * @param roomId
     * @return
     */
    @RequestMapping("/findRoomMember")
    public ReturnObj findRoomMember(String roomId){
        ReturnObj obj;
        List<User> roomMember = roomServiceImpl.findRoomMember(roomId);
        if (roomMember.isEmpty()){
            obj = ReturnObj.fail();
            obj.setMsg("查询群聊成员失败!!!");
        }else{
            obj = ReturnObj.success();
            obj.add("roomMember", roomMember);
        }
        return obj;
    }
}
