package com.eem.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.eem.demo.entity.Room;
import com.eem.demo.entity.RoomMember;
import com.eem.demo.entity.Temp;
import com.eem.demo.entity.User;
import com.eem.demo.pojo.ReturnObj;
import com.eem.demo.service.RoomService;
import com.eem.demo.service.TempService;
import com.eem.demo.service.UserService;
import com.eem.demo.util.JwtUtil;
import com.eem.demo.websocket.RoomWebSocket;
import com.eem.demo.websocket.UserWebSocket;
import org.apache.log4j.Logger;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * @author Administrator
 */
@RestController
public class RoomController {
    @Autowired
    RoomService roomServiceImpl;

    @Autowired
    UserService userServiceImpl;

    @Autowired
    TempService tempServiceImpl;

    private Logger logger = Logger.getLogger(this.getClass());

    /**
     * showdoc
     * @catalog EMM考核项目/群聊模块
     * @title 创建群聊
     * @description 根据管理员用户名和群名创建群聊
     * @method get/post
     * @url http://2700v9g607.zicp.vip:18340/createRoom
     * @param roomName 必须 string 群名
     * @return {"code":100,"msg":"创建群成功!!!","extend":{"room":{"id":4,"master":"张三","masterId":1,"roomName":"试验群3"}}}
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
     * showdoc
     * @catalog EMM考核项目/群聊模块
     * @title 群聊添加成员
     * @description 传入用户名和房间id就可以将用户加入群聊,暂时只能通过群成员拉好友进群,拉
     * @method get/post
     * @url http://2700v9g607.zicp.vip:18340/addRoomMember
     * @param username 必须 string 用户名
     * @param roomId 必须 string 房间id
     * @remark 邀请用户后,会给用户发送一个websocket,用户接收到websocket后需要新建一个该群聊的websocket
     */
    @RequestMapping("/addRoomMember")
    public ReturnObj addRoomMember(String username, String roomId, HttpServletRequest request){
        logger.info("需要添加成员的房间Id: " + roomId);
        logger.info("需要添加的成员: " + username);

        String token = request.getHeader("token");
        String from = JwtUtil.getUsername(token);
        ReturnObj obj;

        //对用户名和房间id进行判读
        if (!userServiceImpl.exists(username)){
            obj = ReturnObj.fail();
            obj.setMsg("用户不存在!!!");
            return obj;
        }
        if (!roomServiceImpl.exists(roomId)){
            obj = ReturnObj.fail();
            obj.setMsg("房间不存在!!!");
            return obj;
        }
        if (roomServiceImpl.isRoomMember(roomId, username)){
            obj = ReturnObj.fail();
            obj.setMsg("用户已是群聊成员!!!");
            return obj;
        }

        RoomMember roomMember = roomServiceImpl.addRoomMember(username, roomId);

        //更新群聊成员的roomMemberName
        List<String> memberName = roomServiceImpl.findMemberName(roomId);
        RoomWebSocket.membersName.put(roomId, memberName);
        logger.info(roomId + "里面的成员名字: " + memberName);

        logger.info(roomMember);
        if (roomMember == null){
            obj = ReturnObj.fail();
        }else{
            obj = ReturnObj.success();
            //向被邀请者发送信息,告知他已经加入群聊,让小程序新建该房间的WebSocket
            JSONObject object = new JSONObject();
            object.put("type", "addRoomMember");
            object.put("roomId", roomId);
            object.put("msg", "你已加入群聊");
            object.put("toName", username);
            object.put("fromName", from);
            object.put("roomName", roomServiceImpl.findRoomName(roomId));
            UserWebSocket.sendMsg(username, object);

            //发送群告知加入群聊
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("roomId", roomId);
            jsonObject.put("username", username);
            jsonObject.put("type", "addRoomMember");
            jsonObject.put("msg", username + "加入群聊");
            RoomWebSocket.sendMsg(from, roomId, jsonObject);
        }
        return obj;
    }

    /**
     * showdoc
     * @catalog EMM考核项目/群聊模块
     * @title 移出群聊
     * @description 根据用户id和群聊房间号的id将用户移出群聊,操作前,会对请求者进行验证其是否是群主
     * @method get/post
     * @url http://2700v9g607.zicp.vip:18340/deleteRoomMember
     * @param username 必须 string 需要删除的用户名
     * @param roomId 必须 string 房间号
     * @remark 如果不是管理员,就操作失败
     */
    @RequestMapping("/deleteRoomMember")
    public ReturnObj deleteRoomMember(String username, String roomId, HttpServletRequest request){
        logger.info("需要删除群成员的username: " + username);
        logger.info("需要删除群成员的roomId: " + roomId);

        ReturnObj obj = null;
        //获取请求人的username
        String token = request.getHeader("token");
        String username1 = JwtUtil.getUsername(token);

        //判断是否是管理员和自己
        if (!roomServiceImpl.isMaster(username1,roomId) && !username1.equals(username)){
            logger.info("不是管理员,操作失败");
            obj = ReturnObj.fail();
            obj.setMsg("不是管理员获操作失败!!!");
            return obj;
        }

        //管理员自己删除自己
        if (roomServiceImpl.isMaster(username1, roomId) && username1.equals(username)){
            logger.info("管理员自己删除自己");
            obj = ReturnObj.fail();
            obj.setMsg("管理员不能自己删除自己，请将管理员位置交付他人再进行退群");
            return obj;
        }

        //如果是管理员或者自己就进行删除操作
        int i = roomServiceImpl.deleteMember(username, roomId);
        if(i > 0){
            logger.info("删除成功,发送websocket信息");
            obj = ReturnObj.success();
            //发送Room websocket信息
            JSONObject msg = new JSONObject();
            msg.put("type", "deleteRoomMember");
            msg.put("roomId", roomId);
            msg.put("msg", username + "被管理员移出群聊");
            RoomWebSocket.sendMsg(username1, roomId, msg);

            //给被移出的用户发送信息
            String roomName = roomServiceImpl.findRoomName(roomId);
            JSONObject object = new JSONObject();
            object.put("toName", username);
            object.put("fromName", username1);
            object.put("roomId", roomId);
            object.put("roomName", roomName);
            object.put("type", "deleteRoomMember");
            object.put("msg", "你已被移出群聊 " + roomName);
            UserWebSocket.sendMsg(username, object);

            //移除Session
            RoomWebSocket.deleteOne(username, roomId);
        }else{
            obj = ReturnObj.fail();
        }
        return obj;
    }
    /**
     * showdoc
     * @catalog EMM考核项目/群聊模块
     * @title 查询群聊成员
     * @description 根据房间id,查询房间成员和管理员
     * @method get/post
     * @url http://2700v9g607.zicp.vip:18340/findRoomMember
     * @param roomId 必须 string 房间id
     * @return {"code":100,"msg":"处理成功！","extend":{"roomMaster":{"id":1,"username":"张三","password":"d8bacf6f768bbd18d2a5642d83edf916","photo":"C:/emm/1875b54c-252f-431f-936a-3156f611e143.jpg","gender":"女","address":"广东惠州","signature":"浪","state":null,"memoName":null,"pinYin":null},"roomMember":[{"id":1,"username":"张三","password":"d8bacf6f768bbd18d2a5642d83edf916","photo":"C:/emm/1875b54c-252f-431f-936a-3156f611e143.jpg","gender":"女","address":"广东惠州","signature":"浪","state":null,"memoName":null,"pinYin":"zhangsan"},{"id":2,"username":"李四","password":null,"photo":null,"gender":null,"address":null,"signature":null,"state":"离线","memoName":"张三设置的小四","pinYin":"zhangsanshezhidexiaosi"},{"id":3,"username":"11","password":null,"photo":"C:/emm/fdfa5384-46ea-4619-b93e-755d0f5a4359.jpg","gender":"男","address":"河北","signature":"啦啦啦","state":"离线","memoName":"小G","pinYin":"xiaoG"}]}}
     */
    @RequestMapping("/findRoomMember")
    public ReturnObj findRoomMember(String roomId, HttpServletRequest request){
        ReturnObj obj;
        //获取用户id
        String token = request.getHeader("token");
        String userId = JwtUtil.getUserId(token);
        //获取roomMember
        List<User> roomMember = roomServiceImpl.findRoomMember(roomId, userId);

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
     * showdoc
     * @catalog EMM考核项目/群聊模块
     * @title 修改群主
     * @description 群主转让群主的位置
     * @method get/post
     * @url http://2700v9g607.zicp.vip:18340/updateRoomMaster
     * @param masterName 必须 string 新群主的用户名
     * @param roomId 必须 string 群聊的房间id
     * @remark 只能是群主操作,会对请求者的身份进行验证
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
     * showdoc
     * @catalog EMM考核项目/群聊模块
     * @title 更改群名
     * @description 群主更改群聊的名字
     * @method get/post
     * @url http://2700v9g607.zicp.vip:18340/updateRoomName
     * @param roomName 必须 string 新的房间名
     * @param roomId 必须 string 群聊的房间id
     * @remark 会对请求者的身份进行验证,如果不是群主,就返回错误信息
     */
    @RequestMapping("/updateRoomName")
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

    /**
     * showdoc
     * @catalog EMM考核项目/群聊模块
     * @title 群聊发送文件
     * @description 统一一个接口上传需要发送的文件,单对单聊天发送文件那个url
     * @method post
     * @url http://2700v9g607.zicp.vip:18340/sendFile
     * @param file 必须 file 需要上传的文件
     * @param roomId 必须 string 发送群的id
     */
    @Deprecated
    @RequestMapping("/sendRoomFile")
    public ReturnObj sendRoomFile(@RequestParam("file") MultipartFile file, String roomId,
                                  HttpServletRequest request){
        String token = request.getHeader("token");
        String username = JwtUtil.getUsername(token);
        //获取文件名
        String filename = file.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        String string = UUID.randomUUID().toString();
        //新建文件类
        File dest = new File("C:/emm/test/" + string + suffix);

        //将文件写入硬盘
        Temp temp1;
        try {
            file.transferTo(dest);
            //新建temp类
            Temp temp = new Temp();
            temp.setSender(username);
            temp.setReceive(roomId);
            temp.setFilePath(dest.toString());
            //存入数据库
            temp1 = tempServiceImpl.saveFile(temp);
            //WebSocket发送信息
            logger.info("给" + roomId + "发送文件");
        } catch (IOException e) {
            e.printStackTrace();
            ReturnObj obj = ReturnObj.fail();
            obj.setMsg("发送文件失败!!!");
            return obj;
        }
        ReturnObj obj = ReturnObj.success();
        obj.add("url", "/receiveFile/" + temp1.getId());
        return obj;
    }

    /**
     * showdoc
     * @catalog EMM考核项目/群聊模块
     * @title 群聊接收文件
     * @description 统一一个接口下载文件
     * @method get/post
     * @url http://2700v9g607.zicp.vip:18340/websocket发过去的url
     * 所有文件统一一个接口上传和下载
     */
    @Deprecated
    @RequestMapping("/receiveRoomFile/{fileId}")
    public void receiveRoomFile(@PathVariable("fileId") String fileId, HttpServletResponse response){
        logger.info("接收到的fileId: " + fileId);
        String filePath = tempServiceImpl.findFilePath(fileId);
        //新建文件类
        File file = new File(filePath);
        if (file.exists()){
            try {
                byte[] bytes = FileUtil.readAsByteArray(file);
                ServletOutputStream stream = response.getOutputStream();
                stream.write(bytes);
                stream.flush();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * showdoc
     * @catalog EMM考核项目/群聊模块
     * @title 查询用户的所有已加入群聊的id
     * @description 用户刚刚登陆时,先请求已加入的群聊id,然后逐个新建该群聊的websocket
     * @method get/post
     * @url http://2700v9g607.zicp.vip:18340/fiendRoomId
     * @return {"code":100,"msg":"处理成功！","extend":{"roomId":[1,2,3,4]}}
     * @remark 刚登陆就要发送的请求
     */
    @RequestMapping("/findRoomId")
    public ReturnObj findRoomId(HttpServletRequest request){
        ReturnObj obj;
        String token = request.getHeader("token");
        String userId = JwtUtil.getUserId(token);
        logger.info("userId: " + userId);
        List<Integer> roomId = roomServiceImpl.findRoomId(userId);
        if (roomId.isEmpty()){
            obj = ReturnObj.fail();
            obj.setMsg("用户没加入任何群聊!!!");
        }else{
            obj = ReturnObj.success();
            logger.info("roomId: " + roomId);
            obj.add("roomId", roomId);
        }
        return  obj;
    }
}
