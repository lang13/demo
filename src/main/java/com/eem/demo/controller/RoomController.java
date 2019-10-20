package com.eem.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.eem.demo.entity.Room;
import com.eem.demo.entity.RoomMember;
import com.eem.demo.entity.User;
import com.eem.demo.pojo.ReturnObj;
import com.eem.demo.service.RoomService;
import com.eem.demo.service.UserService;
import com.eem.demo.util.JwtUtil;
import com.eem.demo.websocket.RoomWebSocket;
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

    @RequestMapping("/deleteRoomMember")
    public ReturnObj deleteRoomMember(String username, String roomId, HttpServletRequest request){
        ReturnObj obj = null;
        //获取请求人的username
        String token = request.getHeader("token");
        String username1 = JwtUtil.getUsername(token);
        //判断是否是管理员
        if (!roomServiceImpl.isMaster(username,roomId)){
            obj = ReturnObj.fail();
            obj.setMsg("您不是管理员!!!");
            return obj;
        }
        //如果是管理员就进行删除操作
        int i = roomServiceImpl.deleteMember(username, roomId);
        if(i > 0){
            obj = ReturnObj.success();
            //移除session
            RoomWebSocket.deleteOne(username, roomId);
        }else{
            obj = ReturnObj.fail();
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
            //查询群主
            User roomMaster = roomServiceImpl.findRoomMaster(roomId);
            obj.add("roomMember", roomMember);
            obj.add("roomMaster", roomMaster);
        }
        return obj;
    }

    /**
     * 更改群主
     * @param request
     * @param masterName
     * @param roomId
     * @return
     */
    @RequestMapping("/updateRoomMaster")
    public ReturnObj updateRoomMaster(HttpServletRequest request, String masterName, String roomId){
        ReturnObj obj;
        String token = request.getHeader("token");
        String username = JwtUtil.getUsername(token);
        if(!roomServiceImpl.isMaster(username, roomId)){
            obj = ReturnObj.fail();
            obj.setMsg("您不是群主!!!");
        }else{
            int i = roomServiceImpl.updateRoomMaster(masterName, roomId);
            if (i > 0){
                obj = ReturnObj.success();
            }else{
                obj = ReturnObj.fail();
            }
        }
        return obj;
    }

    /**
     * 修改群聊房间名
     * @param roomName
     * @param roomId
     * @param request
     * @return
     */
    public ReturnObj updateRoomName(String roomName, String roomId, HttpServletRequest request){
        ReturnObj obj;
        //获取用户名
        String token = request.getHeader("token");
        String username = JwtUtil.getUsername(token);
        if(!roomServiceImpl.isMaster(username, roomId)){
            obj = ReturnObj.fail();
            obj.setMsg("您不是群主!!!");
        }else{
            int i = roomServiceImpl.updateRoomName(roomName, roomId);
            if (i > 0){
                obj = ReturnObj.success();
            }else{
                obj = ReturnObj.fail();
            }
        }
        return obj;
    }
    //发送文件
}
