package com.eem.demo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
import com.eem.demo.util.PinYinUtil;
import com.eem.demo.websocket.UserWebSocket;
import org.apache.commons.logging.LogFactory;
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
     * showdoc
     * @catalog EMM考核项目/用户功能模块
     * @title 用户登陆
     * @description 用户登陆接口,成功登陆后返回用户信息,并且带上用户的token,登陆失败则返回错误代码200
     * @method get/post
     * @url http://2700v9g607.zicp.vip:18340/login
     * @param username 必选 string 用户名
     * @param password 必选 string 密码
     * @return {"code":100,"msg":"登陆成功!!!","extend":{"user":{"id":9,"username":"赵六","password":"fea4c3794c8cacf5c43c7277c2c6d51d","photo":null,"gender":null,"address":null,"signature":null,"state":null,"memoName":null,"pinYin":null},"token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJsb2dpbk5hbWUiOiLotbXlha0iLCJleHAiOjE1NzI1MjkyODIsInVzZXJJZCI6IjkifQ.fZ6KLTf2wTAhcHnHc9xJA-1zAps1Ctr5LqZiQmrIAww"}}
     * @return_param token string 用户的token,用于验证登陆
     * @remake
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

    /**
     * showdoc
     * @catalog EMM考核项目/用户功能模块
     * @title 用户注册
     * @description 用户注册接口
     * @method get/post
     * @url http://2700v9g607.zicp.vip:18340/register
     * @param username 必选 string 用户名
     * @param password 必选 string 密码
     * @return {"code":100,"msg":"处理成功！","extend":{"user":{"id":9,"username":"赵六","password":"d8bacf6f768bbd18d2a5642d83edf916","photo":null,"gender":null,"address":null,"signature":null,"state":null,"memoName":null,"pinYin":null}}}
     * @return_param id int 用户id,用户的唯一标识之一
     * @return_param username string 用户名
     * @return_param password string 用户密码,经过MD5进行加密
     */
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
     * showdoc
     * @catalog EMM考核项目/用户功能模块
     * @title 用户头像上传
     * @description 客户端上传用户头像文件到服务器
     * @method post
     * @url http://2700v9g607.zicp.vip:18340/photo/uploadPhoto
     * @param file 必选 file 用户头像照片
     * @return 类似于用户注册和登陆时成功时返回的界面
     * @return_param photo string 用户头像在服务器的位置
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

            //获取用户名
            String username = JwtUtil.getUsername(request.getHeader("token"));
            //上传数据时,先将原文件删除
            String oldFile = userServiceImpl.findByUsername(username).getPhoto();
            //判断老头像是否存在
            if (oldFile != null){
                userServiceImpl.deleteFile(oldFile);
            }

            logger.info("将头像信息写入数据库!!!");
            int i = userServiceImpl.updatePhoto(filename, username);
            if (i > 0){
                //返回上传的文件类
                ReturnObj obj = ReturnObj.success();
                obj.setMsg("保存头像成功!!!");
                obj.add("photo",dest);
                return obj;
            }else{
                ReturnObj obj = ReturnObj.fail();
                obj.setMsg("保存头像失败!!!");
                return obj;
            }
        } catch (IOException e) {
            e.printStackTrace();
            ReturnObj obj = ReturnObj.fail();
            obj.setMsg("文件上传失败!!!");

            return obj;
        }
    }

    /**
     * showdoc
     * @catalog EMM考核项目/用户功能模块
     * @title 加载用户头像
     * @description 加载用户头像的接口, 图像是以字节码文件发送给请求用户
     * @method post
     * @url http://2700v9g607.zicp.vip:18340/photo/downloadPhoto
     * @param photoPath 不是 string 如果要加载其他用户的头像,就将其他用户的头的路径传递过来
     * @remark 无参数也没有有用的返回值
     */
    @RequestMapping("/photo/downloadPhoto")
    public ReturnObj downloadPhoto(HttpServletRequest request, HttpServletResponse response,
                                   @RequestParam(value = "photoPath", required = false) String photoPath){
        response.setCharacterEncoding("utf-8");

        logger.info("传过来的photoPath: " + photoPath);

        if (photoPath == null || "".equals(photoPath)){
            //获取用户id
            String userId = JwtUtil.getUserId(request.getHeader("token"));
            //获取照片路径
            photoPath = userRepository.findOne(Integer.valueOf(userId)).getPhoto();
        }

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
            obj.setMsg("头像不存在!!!");
            return obj;
        }
    }

    /**
     * showdoc
     * @catalog EMM考核项目/用户功能模块
     * @title 修改用户的用户名
     * @description 用户名是账户的唯一标识,所以用户名不能和其他用户的重复,后台会进行查重
     * @method post/get
     * @url http://2700v9g607.zicp.vip:18340/updateUsername
     * @param username 必须 string 需要改成的用户名
     * @return {"code":200,"msg":"用户名已存在!!!","extend":{}}
     * @remark 如果成功,就返回修改后的用户名
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
     * showdoc
     * @catalog EMM考核项目/用户功能模块
     * @title 修改用户密码
     * @description 修改用户的密码,但需要传入旧密码以验证是否是本人操作
     * @method get/post
     * @url http://2700v9g607.zicp.vip:18340/updatePassword
     * @param newPassword 必须 string 用户的新密码
     * @param oldPassword 必须 string 用户的旧密码
     * @return {"code":100,"msg":"密码修改成功!!!","extend":{}}
     * @remark 如果修改密码失败或者密码错误,返回的msg里面会有说明
     */
    @RequestMapping("/updatePassword")
    public ReturnObj updatePassword(String oldPassword, String newPassword, HttpServletRequest request){
        ReturnObj obj = null;
        //获取用户id
        String token = request.getHeader("token");
        String userId = JwtUtil.getUserId(token);
        String username = JwtUtil.getUsername(token);

        //验证oldPassword是否真确
        User user = userServiceImpl.login(username, Md5Util.getMd5(oldPassword));
        logger.info("获取的oldPassword: " + oldPassword);
        logger.info("获取的newPassword: " + newPassword);
        if (user != null){
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
     * showdoc
     * @catalog EMM考核项目/用户功能模块
     * @title 修改用户的基本信息
     * @description 修改用户除用户名,头像,密码的用户信息
     * @method get/post
     * @url http://2700v9g607.zicp.vip:18340/updateUser
     * @param gender 不是 string 用户性别
     * @param address 不是 string 用户的所在地
     * @param signature 不是 string 用户的个性签名
     * @return {"code":100,"msg":"修改用户信息成功!!!","extend":{"user":{"id":1,"username":"张三","password":"d8bacf6f768bbd18d2a5642d83edf916","photo":"C:/emm/1875b54c-252f-431f-936a-3156f611e143.jpg","gender":"女","address":"广东惠州","signature":"浪","state":null,"memoName":null,"pinYin":null}}}
     * @return_param password string 这个并不是用户的密码,这是一个修改过的字符串
     * @remark 返回的是用户的全部信息,但状态(state),备注名(memoName),用户名的中文拼音(PinYin)是空的
     */
    @RequestMapping("/updateUser")
    public ReturnObj updateUser(User user, HttpServletRequest request){
        logger.info("传入的修改后的用户信息: " + user);

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
     * showdoc
     * @catalog EMM考核项目/用户好友模块
     * @title 查询用户信息
     * @description 根据用户的用户名查询用户
     * @method get/post
     * @url http://2700v9g607.zicp.vip:18340/findUser
     * @param username 必须 string 需要查找的用户名
     * @return {"code":100,"msg":"处理成功！","extend":{"user":{"id":2,"username":"李四","password":"8cc806a94d3a495ae44d68f6a164d208","photo":null,"gender":null,"address":null,"signature":null,"state":null,"memoName":null,"pinYin":null}}}
     * @return_param id string 用户id
     * @return_param username string 用户名
     * @return_parma password string 用户名,但是是假的
     * @return_param photo string 用户头像信息
     * @remark 其他的信息皆为空
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
     * showdoc
     * @catalog EMM考核项目/用户好友模块
     * @title 请求添加好友
     * @description 根据用户名,请求添加对方为好友,但需要对方的同意,请求时,会向对方发送websocket信息,通知对方有人要加好友,websocket的type为"requestFriend"
     * @method get/post
     * @url http://2700v9g607.zicp.vip:18340/requestFriend
     * @param friendName 必须 string 需要添加为好友的用户名
     * @param msg 不是 string 添加好友时,给对方发送的信息
     * @return {"code":100,"msg":"处理成功！","extend":{}}
     * @remark 发送请求前,会查询该用户是否存在,如果不存在会返回"用户不存在的信息"
     */
    @RequestMapping("/requestFriend")
    public ReturnObj requestFriend(String friendName, String msg, HttpServletRequest request){
        ReturnObj obj;

        String token = request.getHeader("token");
        String username = JwtUtil.getUsername(token);

        //获取自己的用户信息
        User user = userServiceImpl.findByUsername(username);
        user.setPinYin(PinYinUtil.toPinYin(user.getUsername()));

        if(userServiceImpl.exists(friendName)){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "requestFriend");
            jsonObject.put("fromName", user.getUsername());
            jsonObject.put("fromId", user.getId());
            jsonObject.put("fromPhoto", user.getPhoto());
            jsonObject.put("fromNamePinYin", PinYinUtil.toPinYin(user.getUsername()));
            jsonObject.put("msg",msg);
            jsonObject.put("toName", friendName);
            jsonObject.put("pinYin", PinYinUtil.toPinYin(friendName));
            jsonObject.put("memoName", "");
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
     * showdoc
     * @catalog EMM考核项目/用户好友模块
     * @title 添加好友
     * @description 请求添加好友时对方同意后调用的方法,调用后会将好友关系的数据写入数据库,并向对方发送"添加好好友成功"的信息(websocket发送,type类型为addFriend)
     * @method get/post
     * @url http://2700v9g607.zicp.vip:18340/addFriend
     * @param friendName 必须 string 对方的用户名(发送添加好友请求的用户名,requestFriend发送请求时会带上)
     * @remark 会发送websocket告知对方,同意添加好友时才调用的方法
     */
    @RequestMapping("/addFriend")
    public ReturnObj addFriend(String friendName, HttpServletRequest request){
        ReturnObj obj = null;
        logger.info("朋友姓名: " + friendName);

        //判断用户是否存在
        if(!userServiceImpl.exists(friendName)){
            obj = ReturnObj.fail();
            obj.setMsg("用户不存在!!!");
            return obj;
        }else{
            //获取token以及用户id
            String token = request.getHeader("token");
            String userId = JwtUtil.getUserId(token);
            logger.info("token中的id: " + userId);

            //获取好友id
            User friend = userServiceImpl.findByUsername(friendName);
            //查询到的用户id
            logger.info("查询到的用户id: " + friend.getId());
            Integer friendId = friend.getId();
            //插入数据
            User user = friendServiceImpl.addFriend(userId, String.valueOf(friendId));
            //判断是否成功
            if (user == null){
                obj = ReturnObj.fail();
                obj.setMsg("新增好友失败!!!");

                return obj;
            }else{
                obj = ReturnObj.success();
                obj.setMsg("新增好友成功!!!");
                obj.add("friend",user);

                //获取自己的用户信息
                String username = JwtUtil.getUsername(token);
                User oneself = userServiceImpl.findByUsername(username);
                oneself.setPinYin(PinYinUtil.toPinYin(oneself.getUsername()));

                //向请求者发送信息
                JSONObject object = new JSONObject();
                object.put("fromName", oneself.getUsername());
                object.put("fromId", oneself.getId());
                object.put("fromPhoto", oneself.getPhoto());
                object.put("fromNamePinYin", PinYinUtil.toPinYin(oneself.getUsername()));
                object.put("toName", friendName);
                object.put("type", "addFriend");
                object.put("msg", "添加好友" + username + "成功");
                object.put("memoName", "");
                //发送websocket
                UserWebSocket.sendMsg(friendName, object);
                return obj;
            }
        }
    }

    /**
     * showdoc
     * @catalog EMM考核项目/用户好友模块
     * @title 查询所有好友信息
     * @description 查询已添加的所有好友信息
     * @method get/post
     * @url http://2700v9g607.zicp.vip:18340/findFriends
     * @return {"code":100,"msg":"查找好友成功!!!","extend":{"friends":[{"id":2,"username":"李四","password":null,"photo":null,"gender":null,"address":null,"signature":null,"state":"离线","memoName":"小四","pinYin":"xiaosi"},{"id":3,"username":"GDUT","password":null,"photo":"C:/emm/0d33be3b-b3d7-4d48-9488-865ea3dc8b4f.png","gender":null,"address":null,"signature":null,"state":"离线","memoName":"小G","pinYin":"xiaoG"},{"id":5,"username":"qq","password":null,"photo":null,"gender":null,"address":null,"signature":null,"state":"离线","memoName":"小Q","pinYin":"xiaoQ"}]}}
     * @return_param state string 好友的状态信息
     * @return_param memoName string 好友的备注名
     * @return_param PinYin string 用户名或者备注名的中文拼音缩写
     * @remark 详细的好友信息需要调用/fiendFriend
     */
    @RequestMapping("/findFriends")
    public ReturnObj findFriends(HttpServletRequest request){
        String token = request.getHeader("token");
        //获取用户id
        String userId = JwtUtil.getUserId(token);
        ReturnObj obj;
        //查找好友集
        List<User> friends = friendServiceImpl.findFriends(userId);
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
     * showdoc
     * @catalog EMM考核项目/用户好友模块
     * @title 查询某个好友的详细信息
     * @description 根据好友id查询某个好友的详细信息(地址,个性签名,性别)
     * @method get/post
     * @url http://2700v9g607.zicp.vip:18340/fiendFriend
     * @param friendId 必须 string 好友的用户id
     * @return {"code":100,"msg":"处理成功！","extend":{"friend":{"id":3,"username":"GDUT","password":null,"photo":"C:/emm/0d33be3b-b3d7-4d48-9488-865ea3dc8b4f.png","gender":"男","address":"广东惠州","signature":"lang","state":"离线","memoName":"小G","pinYin":"xiaoG"}}}
     * @remark 如果不是好友,则会显示"处理失败"
     */
    @RequestMapping("/findFriend")
    public ReturnObj findFriend(String friendId, HttpServletRequest request){
        ReturnObj obj;
        String token = request.getHeader("token");
        String userId = JwtUtil.getUserId(token);
        User user = friendServiceImpl.findFriend(friendId, userId);
        if (user == null){
            obj = ReturnObj.fail();
        }else{
            obj = ReturnObj.success();
            obj.add("friend",user);
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
     * showdoc
     * @catalog EMM考核项目/用户好友模块
     * @title 删除好友
     * @description 解除好友关系,解除关系后,会给对方发送一个websocket信息,删除后,会返回自己的好友列表(刷新后的)
     * @method get/post
     * @url http://2700v9g607.zicp.vip:18340/deleteFriend
     * @param friendName 必须 string 需要删除的好友的用户名
     * @remark 接收到解除好友关系的websocket(type为"deleteFriend")后,要刷新用户的好友列表
     */
    @RequestMapping("/deleteFriend")
    public  ReturnObj deleteFriend(HttpServletRequest request, String friendName){
        ReturnObj obj;
        //获取用户id
        String token = request.getHeader("token");
        String userId = JwtUtil.getUserId(token);
        String username = JwtUtil.getUsername(token);

        //删除好友
        int i = friendServiceImpl.deleteFriend(userId, friendName);
        if (i > 0){
            obj = ReturnObj.success();
            obj.setMsg("成功删除好友!!!");

            //给对方好友发送websocket信息
            JSONObject object = new JSONObject();
            object.put("toName", friendName);
            object.put("fromName", username);
            object.put("type", "deleteFriend");
            object.put("msg", username + "和你解除好友关系!!!");
            UserWebSocket.sendMsg(friendName, object);

            //刷新好友列表
            List<User> users = friendServiceImpl.findFriends(userId);
            obj.add("friends",users);

            return obj;
        }else{
            obj = ReturnObj.fail();
            obj.setMsg("删除好友失败!!!");
            return obj;
        }
    }

    /**
     * showdoc
     * @catalog EMM考核项目/用户聊天模块
     * @title 单对单聊天发送文件
     * @description 单对单聊天发送文件时调动的url,调用这个接口后,会将文件保存着服务器,然后先对方发送一个websocket,里面含有文件的url地址
     * @method post
     * @url http://2700v9g607.zicp.vip:18340/sendFile
     * @param file 必须 file 需要发送的文件
     * @param toName 必须 string 接收者的用户名
     * @return 里面包含着文件的url
     * @remark websocket的type为"file"
     */
    @RequestMapping("/sendFile")
    public ReturnObj sendFile(@RequestParam("file") MultipartFile file, String toName, String toId, HttpServletRequest request){
        String token = request.getHeader("token");
        String username = JwtUtil.getUsername(token);

        String originalFilename = file.getOriginalFilename();
        //获取后缀名
        String suffix  = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();
        //创建文件对象
        File dest = new File("C:/emm/test/"+uuid+suffix);
        //将文件保存到硬盘
        Temp temp;
        try {
            //文件信息保存至数据库
            Temp tempFile = new Temp();
            //设置file类
            tempFile.setFilePath(dest.toString());
            tempFile.setReceive(toName);
            tempFile.setSender(username);
            temp = tempServiceImpl.saveFile(tempFile);
            logger.info("文件: " + dest);
            file.transferTo(dest);
//            //WebSocket发送信息
//
//            logger.info("给" + toName + "发送文件");
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("type","file");
//            jsonObject.put("msg","/receiveFile/" + temp.getId());
//            jsonObject.put("fromName",username);
//            jsonObject.put("fromId", userId);
//            jsonObject.put("toName",toName);
//            jsonObject.put("toId", toId);
//
//            logger.info("发送的json: " + jsonObject);
//            UserWebSocket.sendMsg(toName,jsonObject);
        } catch (IOException e) {
            e.printStackTrace();
            ReturnObj obj = ReturnObj.fail();
            obj.setMsg("发送文件失败!!!");
            return obj;
        }
        ReturnObj obj = ReturnObj.success();
        obj.add("url", "/receiveFile/" + temp.getId());
        return obj;
    }

    /**
     * showdoc
     * @catalog EMM考核项目/用户聊天模块
     * @title 单对单聊天接收文件
     * @description 单对单聊天接收文件的url
     * @method get/post
     * @url http://2700v9g607.zicp.vip:18340/websocket发过去的url
     * @remark 无返回值和参数
     */
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
     * showdoc
     * @catalog EMM考核项目/用户好友模块
     * @title 更改自己的用户转态(在线,隐身)
     * @description 更改自己的用户状态(在线,隐身等,更多的功能还未完善)比如离线后不能接收信息,忙碌是自动回复信息,看哪天心情好有时间再去弄
     * @method get/post
     * @url http://2700v9g607.zicp.vip:18340/updateState
     * @remark 无返回值
     */
    @RequestMapping("/updateState")
    public void updateState(String state, HttpServletRequest request){
        String token = request.getHeader("token");
        String username = JwtUtil.getUsername(token);
        String userId = JwtUtil.getUserId(token);
        //获取好友列表
        List<User> users = friendServiceImpl.findFriends(username);
        //好友名字集合
        List<String> friends = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            friends.add(users.get(i).getUsername());
        }
        //发送状态信息
        UserWebSocket.sendState(friends, state, username, userId);
    }

    /**
     * showdoc
     * @catalog EMM考核项目/用户好友模块
     * @title 修改用户的备注名
     * @description 修改用户备注名的url
     * @method get/post
     * @url http://2700v9g607.zicp.vip:18340/updateMemo
     * @param friendId 必须 string 需要修改的用户的用户ID
     * @param memoName 不是 string 用户的备注名
     * @return {"code":100,"msg":"处理成功！","extend":{"user":{"id":2,"username":"李四","password":null,"photo":null,"gender":null,"address":null,"signature":null,"state":"离线","memoName":"张三设置的小四","pinYin":"zhangsanshezhidexiaosi"}}}
     * @remark 如果想取消备注名,就传递个空字符串("")或者直接不传入值进来
     */
    @RequestMapping("/updateMemo")
    public ReturnObj updateMemo(String friendId,
                                @RequestParam(value = "memoName",required = false)String memoName,
                                HttpServletRequest request){
        ReturnObj obj;
        String token = request.getHeader("token");
        String userId = JwtUtil.getUserId(token);
        logger.info("传入的memo: " + memoName);
        //修改用户昵称
        User user = friendServiceImpl.updateMemo(friendId, userId, memoName);
        if (user != null){
            obj = ReturnObj.success();
            obj.add("user",user);
        }else{
            obj = ReturnObj.fail();
        }
        return obj;
    }

    /**
     * showdoc
     * @catalog EMM考核项目/用户聊天模块
     * @title 获取聊天记录
     * @description 群聊和单对单聊天的聊天记录都是从这里获取
     * @method get/post
     * @url http://2700v9g607.zicp.vip:18340/requestRecord
     * @param friendId 不是 string 如果想要获取单对单聊天的聊天记录,就将对方的用户id传递进来
     * @param roomId 不是 string 如果想要获取群聊的聊天记录,就将群聊房间的id传递进来
     * @return {"code":100,"msg":"处理成功！","extend":{"record":[{"toId":"2","msg":"你好啊李四","toName":"李四","fromName":"张三","type":"msg","fromId":"1"},{"toId":"2","msg":"你好啊李四","toName":"李四","fromName":"张三","type":"msg","fromId":"1"},{"toId":"2","msg":"你好啊李四","toName":"李四","fromName":"张三","type":"msg","fromId":"1"},{"toId":"2","msg":"你好啊李四","toName":"李四","fromName":"张三","type":"msg","fromId":"1"},{"toId":"2","msg":"你好啊李四","toName":"李四","fromName":"张三","type":"msg","fromId":"1"},{"toId":"2","msg":"你好啊李四","toName":"李四","fromName":"张三","type":"msg","fromId":"1"},{"toId":"2","msg":"你好啊李四","toName":"李四","fromName":"张三","type":"msg","fromId":"1"},{"toId":"2","msg":"你好啊李四","toName":"李四","fromName":"张三","type":"msg","fromId":"1"},{"toId":"2","msg":"你好啊李四","toName":"李四","fromName":"张三","type":"msg","fromId":"1"},{"toId":"2","msg":"你好啊李四","toName":"李四","fromName":"张三","type":"msg","fromId":"1"},{"toId":"2","msg":"你好啊李四","toName":"李四","fromName":"张三","type":"msg","fromId":"1"},{"toId":"2","msg":"你好啊李四","toName":"李四","fromName":"张三","type":"msg","fromId":"1"},{"toId":"2","msg":"你好啊李四","toName":"李四","fromName":"张三","type":"msg","fromId":"1"},{"toId":"2","msg":"你好啊李四","toName":"李四","fromName":"张三","type":"msg","fromId":"1"},{"toId":"2","msg":"你好啊李四","toName":"李四","fromName":"张三","type":"msg","fromId":"1"},{"toId":"2","msg":"你好啊李四","toName":"李四","fromName":"张三","type":"msg","fromId":"1"},{"toId":"2","msg":"你好啊李四","toName":"李四","fromName":"张三","type":"msg","fromId":"1"},{"toId":"2","msg":"你好啊李四","toName":"李四","fromName":"张三","type":"msg","fromId":"1"},{"toId":"2","msg":"你好啊李四","toName":"李四","fromName":"张三","type":"msg","fromId":"1"},{"toId":"2","msg":"你好啊李四","toName":"李四","fromName":"张三","type":"msg","fromId":"1"}]}}
     * @remark 返回的是所有聊天记录的json数据,聊天记录的格式由前端控制,但是fromName,fromId,toName,toId是不能缺失的
     */
    @RequestMapping("/requestRecord")
    public ReturnObj requestRecord(@RequestParam(value = "friendId", required = false) String friendId,
                                   @RequestParam(value = "roomId", required = false) String roomId, HttpServletRequest request){
        ReturnObj obj;

        logger.info("接收到的roomId: " + roomId);
        logger.info("接收的的friendId: " + friendId);

        String token = request.getHeader("token");
        String userId = JwtUtil.getUserId(token);
        //判空
        String fileName;
        String filePath;

        //一对一聊天记录
        if (friendId != null){
            //传递了两个值得情况
            if (roomId != null){
                obj = ReturnObj.fail();
                return  obj;
            }
            filePath = "c:/emm/record/user/";
            int u = Integer.parseInt(userId);
            int f = Integer.parseInt(friendId);
            if (u < f){
                fileName = userId + "and" + friendId + ".txt";
            }else{
                fileName = friendId + "and" + userId + ".txt";
            }
        }else{  //群聊聊天记录
            if (roomId != null){
                filePath = "c:/emm/record/room/";
                fileName = roomId + ".txt";
            }else{ //没有传递值的情况
                obj = ReturnObj.fail();
                return obj;
            }
        }
        logger.info("file: " + filePath + fileName);
        JSONArray objects = tempServiceImpl.requestRecord(filePath + fileName);
        obj = ReturnObj.success();
        obj.add("record", objects);

        return obj;
    }
}
