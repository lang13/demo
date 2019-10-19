package com.eem.demo.controller;

import com.eem.demo.entity.Room;
import com.eem.demo.entity.User;
import com.eem.demo.pojo.ReturnObj;
import com.eem.demo.service.RoomService;
import com.eem.demo.service.UserService;
import com.eem.demo.util.JwtUtil;
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
