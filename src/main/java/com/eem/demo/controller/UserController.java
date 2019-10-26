package com.eem.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.eem.demo.entity.State;
import com.eem.demo.entity.Temp;
import com.eem.demo.entity.User;
import com.eem.demo.pojo.ReturnObj;
import com.eem.demo.repository.UserRepository;
import com.eem.demo.service.FriendService;
import com.eem.demo.service.StateService;
import com.eem.demo.service.TempService;
import com.eem.demo.service.UserService;
import com.eem.demo.util.JwtUtil;
import com.eem.demo.util.Md5Util;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Administrator
 */
@RestController
public class UserController {
    /**
     * 日志功能
     * 用打印各种调试数据
     */
    private static final Logger logger = Logger.getLogger(UserController.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userServiceImpl;

    @Autowired
    FriendService friendServiceImpl;

    @Autowired
    UserService getUserServiceImpl;

    @Autowired
    StateService stateServiceImpl;

    @Autowired
    TempService tempServiceImpl;
    /**
     * 用户登陆
     * @param user
     * @return
     */
    @RequestMapping("/login")
    public ReturnObj login(User user){
        ReturnObj obj = ReturnObj.fail();
        User login = userServiceImpl.login(user.getUsername(), user.getPassword());
        if (login != null){
            //将代码设为成功
            obj.setCode(100);
            obj.setMsg("登陆成功!!!");

            obj.add("user",login);
            //token签名
            String token = JwtUtil.sign(login.getUsername(), String.valueOf(login.getId()));
            obj.add("token",token);
        }else{
            obj.setMsg("账号密码错误!!!");
        }
        return obj;
    }

    @RequestMapping("/register")
    public ReturnObj register(User user){
        ReturnObj obj = ReturnObj.success();
        if (userServiceImpl.exists(user.getUsername())){
            //用户已存在,设置状态码和错误信息
            obj.setCode(200);
            obj.setMsg("用户已存在!!!");

            return obj;
        }
        User register = userServiceImpl.register(user);
        obj.add("user",register);

        return obj;
    }

    /**
     * 客户端上传用户头像文件到服务器
     * @param file
     * @param request
     * @return
     */
    @RequestMapping("/photo/uploadPhoto")
    public ReturnObj uploadPhoto(@RequestParam("file") MultipartFile file, HttpServletRequest request){
        if(file.isEmpty()){
            ReturnObj obj = ReturnObj.fail();
            obj.setMsg("照片文件为空!!!");
            return  obj;
        }
        //获取文件名
        String filename = file.getOriginalFilename();
        logger.info("客户端上传的文件为: " + filename);
        //截取文件后缀
        String suffixName  = filename.substring(filename.lastIndexOf("."));
        logger.info("上传文件的后缀为: " + suffixName);
        //文件上传的路径
        String filePath = "C:/emm/";
        //拼接新的文件名
        filename = filePath + UUID.randomUUID() + suffixName;
        logger.info("拼接后的文件名为: " + filename);
        //新建文件类
        File dest = new File(filename);
        //创建文件目录
        if (dest.getParentFile() == null && !dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            //将文件保存至硬盘
            file.transferTo(dest);
            ReturnObj obj = ReturnObj.success();
            obj.setMsg("文件上传成功!!!");
            //获取用户名
            String username = JwtUtil.getUsername(request.getHeader("token"));
            //上传数据时,先将原文件删除
            String oldFile = userServiceImpl.findByUsername(username).getPhoto();
            userServiceImpl.deleteFile(oldFile);

            userServiceImpl.updatePhoto(filename,username);
            //返回上传的文件类
            obj.add("photo",dest);
            return obj;
        } catch (IOException e) {
            e.printStackTrace();
            ReturnObj obj = ReturnObj.fail();
            obj.setMsg("文件上传失败!!!");

            return obj;
        }
    }

    /**
     * 客户端下载用户头像
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/photo/downloadPhoto")
    public ReturnObj downloadPhoto(HttpServletRequest request, HttpServletResponse response){
        response.setCharacterEncoding("utf-8");
        //获取用户id
        String userId = JwtUtil.getUserId(request.getHeader("token"));
        //获取照片路径
        String photoPath = userRepository.findOne(Integer.valueOf(1)).getPhoto();

        //如果文件需要下载,那么就要设置响应头
//        response.setHeader("Content-Disposition",
//                "attachment; filename=" + username + photoPath.substring(photoPath.lastIndexOf(".")));
        File file = new File(photoPath);
        try {
            byte[] bytes = FileUtil.readAsByteArray(file);
            ServletOutputStream os = response.getOutputStream();
            os.write(bytes);
            os.flush();
            os.close();

            ReturnObj obj = ReturnObj.success();
            return obj;
        } catch (IOException e) {
            e.printStackTrace();
            ReturnObj obj = ReturnObj.fail();
            return obj;
        }
    }

    /**
     * 修改用户名
     * @param username
     * @param request
     * @return
     */
    @RequestMapping("/updateUsername")
    public ReturnObj updateUsername(String username, HttpServletRequest request){
        //获取用户token
        String token = request.getHeader("token");
        //获取token中的用户id
        String userId = JwtUtil.getUserId(token);
        //修改nickname
        logger.info("用户传过来的用户名: " + username);
        if (userServiceImpl.exists(username)){
            ReturnObj obj = ReturnObj.fail();
            obj.setMsg("用户名已存在!!!");
            return obj;
        }else{
            int i = userServiceImpl.updateUsername(username, userId);
            if (i > 0){
                ReturnObj obj = ReturnObj.success();
                obj.setMsg("用户名修改成功!!!");

                obj.add("newName",username);
                return obj;
            }else{
                ReturnObj obj = ReturnObj.fail();
                obj.setMsg("用户名修改失败!!!");
                return obj;
            }
        }
    }


    /**
     * 根据账户id修改账户密码
     * 需要验证输入的密码是否正确
     * @param oldPassword
     * @param newPassword
     * @param request
     * @return
     */
    @RequestMapping("/updatePassword")
    public ReturnObj updatePassword(String oldPassword, String newPassword, HttpServletRequest request){
        ReturnObj obj = null;
        //获取用户id
        String token = request.getHeader("token");
        String userId = JwtUtil.getUserId(token);
        String username = JwtUtil.getUsername(token);

        //验证oldPassword是否真确
        User user = userServiceImpl.findByUsername(username);
        logger.info("获取的oldPassword: " + oldPassword);
        logger.info("获取的newPassword: " + newPassword);
        oldPassword = Md5Util.getMd5(oldPassword);
        if (user.getPassword().equals(oldPassword)){
            int i = userServiceImpl.updatePassword(newPassword, userId);
            if (i > 0){
                obj = ReturnObj.success();
                obj.setMsg("密码修改成功!!!");
            }else{
                obj = ReturnObj.fail();
                obj.setMsg("密码修改失败!!!");
            }
        }else{
            obj = ReturnObj.fail();
            obj.setMsg("密码错误!!!");
        }
        return obj;
    }

    /**
     * 修改用户的基础信息
     * @param user
     * @param request
     * @return
     */
    @RequestMapping("/updateUser")
    public ReturnObj updateUser(User user, HttpServletRequest request){
        ReturnObj obj;
        String token = request.getHeader("token");
        String userId = JwtUtil.getUserId(token);
        User one = userServiceImpl.updateUser(user, userId);
        if (one != null){
            obj = ReturnObj.success();
            obj.setMsg("修改用户信息成功!!!");
            obj.add("user",one);
        }else{
            obj = ReturnObj.fail();
        }
        return obj;
    }

    /**
     * 根据用户名查询用户信息
     * @param username
     * @return
     */
    @RequestMapping("/findUser")
    public ReturnObj findUser(String username){
        ReturnObj obj = null;

        logger.info("用户传过来的需要查询的朋友名: " + username);
        User user = userServiceImpl.findByUsername(username);
        //判空
        if (user == null){
            obj = ReturnObj.fail();
            obj.setMsg("用户不存在");
        }else{
            obj = ReturnObj.success();
            obj.add("user",user);
        }
        return obj;
    }

    /**
     * 根据用户id请求添加好友
     * @param friendName
     */
    @RequestMapping("/requestFriend")
    public ReturnObj requestFriend(String friendName, String msg, HttpServletRequest request){
        ReturnObj obj;

        String token = request.getHeader("token");
        String username = JwtUtil.getUsername(token);
        String userId = JwtUtil.getUserId(token);
        if(userServiceImpl.exists(friendName)){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "inform");
            jsonObject.put("msg",msg);
            jsonObject.put("toName", friendName);
            jsonObject.put("fromName", username);
            jsonObject.put("fromId", userId);
            //发送WebSocket
            UserWebSocket.sendMsg(friendName,jsonObject);

            obj = ReturnObj.success();
        }else{
            obj = ReturnObj.fail();
            obj.setMsg("用户不存在!!!");
        }
        return obj;
    }

    /**
     * 根据用户名添加好友
     * @param fiendName
     * @param request
     * @return
     */
    @RequestMapping("/addFriend")
    public ReturnObj addFriend(String fiendName, HttpServletRequest request){
        ReturnObj obj = null;
        logger.info("朋友姓名: " + fiendName);
        //判断用户是否存在
        if(!userServiceImpl.exists(fiendName)){
            obj = ReturnObj.fail();
            obj.setMsg("用户不存在!!!");
            return obj;
        }else{
            //获取token以及用户id
            String token = request.getHeader("token");
            String userId = JwtUtil.getUserId(token);
            //获取好友id
            User friend = userServiceImpl.findByUsername(fiendName);
            //查询到的用户id
            logger.info("查询到的用户id: " + friend.getId());
            Integer friendId = friend.getId();
            //插入数据
            int i = friendServiceImpl.addFriend(userId, String.valueOf(friendId));
            //判断是否成功
            if (i <= 0){
                obj = ReturnObj.fail();
                obj.setMsg("新增好友失败!!!");

                return obj;
            }else{
                obj = ReturnObj.success();
                obj.setMsg("新增好友成功!!!");
                List<User> users = userServiceImpl.findFriend(userId);
                obj.add("friends",users);

                return obj;
            }
        }
    }

    /**
     * 查询用户好友
     * @param request
     * @return
     */
    @RequestMapping("/findFriends")
    public ReturnObj findFriends(HttpServletRequest request){
        String token = request.getHeader("token");
        //获取用户id
        String userId = JwtUtil.getUserId(token);
        ReturnObj obj;
        //查找好友集
        List<User> friends = userServiceImpl.findFriend(userId);
        if(friends != null){
            obj = ReturnObj.success();
            obj.setMsg("查找好友成功!!!");
            obj.add("friends", friends);
        }else{
            obj = ReturnObj.fail();
            obj.setMsg("查找好友失败!!!");
            obj.add("friends", null);
        }
        return obj;
    }

    /**
     * 已经淘汰的Mapping
     * findFriends请求已经包含了好友的状态信息
     * @param request
     * @return
     */
    @Deprecated
    @RequestMapping("/findFriendState")
    public ReturnObj findFriendState(HttpServletRequest request){
        ReturnObj obj;
        String token = request.getHeader("token");
        String userId = JwtUtil.getUserId(token);
        List<State> friendState = stateServiceImpl.findFriendState(userId);
        if (friendState != null){
            obj = ReturnObj.success();
            obj.add("friendState",friendState);
        }else{
            obj = ReturnObj.fail();
        }
        return obj;
    }

    /**
     * 根据好友名删除好友
     * @param request
     * @param friendName
     * @return
     */
    @RequestMapping("/deleteFriend")
    public  ReturnObj deleteFriend(HttpServletRequest request, String friendName){
        ReturnObj obj;
        //获取用户id
        String token = request.getHeader("token");
        String userId = JwtUtil.getUserId(token);

        //删除好友
        int i = friendServiceImpl.deleteFriend(userId, friendName);
        if (i > 0){
            obj = ReturnObj.success();
            obj.setMsg("成功删除好友!!!");
            List<User> users = userServiceImpl.findFriend(userId);
            obj.add("friends",users);

            return obj;
        }else{
            obj = ReturnObj.fail();
            obj.setMsg("删除好友失败!!!");
            return obj;
        }
    }

    /**
     * 向前端发送文件
     * @param file
     * @param toName
     * @param request
     */
    @RequestMapping("/sendFile")
    public void sendFile(@RequestParam("file") MultipartFile file, String toName, HttpServletRequest request){
        String token = request.getHeader("token");
        String username = JwtUtil.getUsername(token);
        String originalFilename = file.getOriginalFilename();
        //获取后缀名
        String suffix  = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();
        //创建文件对象
        File dest = new File("C:/emm/test/"+uuid+suffix);
        //将文件保存到硬盘
        try {
            //文件信息保存至数据库
            Temp tempFile = new Temp();
            //设置file类
            tempFile.setFilePath(dest.toString());
            tempFile.setReceive(toName);
            tempFile.setSender(username);
            Temp temp = tempServiceImpl.saveFile(tempFile);
            logger.info("文件: " + dest);
            file.transferTo(dest);
            //WebSocket发送信息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type","file");
            jsonObject.put("msg","/receiveFile/" + temp.getId());
            jsonObject.put("from",username);
            jsonObject.put("to",toName);
            logger.info("发送的json: " + jsonObject);
            UserWebSocket.sendMsg(toName,jsonObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/receiveFile/{fileId}")
    public void receiveFile(@PathVariable("fileId") String fileId, HttpServletResponse response){
        logger.info("接收到的fileId: " + fileId);
        String filePath = tempServiceImpl.findFilePath(fileId);
        File file = new File(filePath);
        logger.info("创建的file: " + file);
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
     * 更改用户状态
     * @param state
     * @param request
     */
    public void updateState(String state, HttpServletRequest request){
        String token = request.getHeader("token");
        String username = JwtUtil.getUsername(token);
        String userId = JwtUtil.getUserId(token);
        //获取好友列表
        List<User> users = userServiceImpl.findFriend(username);
        //好友名字集合
        List<String> friends = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            friends.add(users.get(i).getUsername());
        }
        //发送状态信息
        UserWebSocket.sendState(friends, state, username, userId);
    }
}
