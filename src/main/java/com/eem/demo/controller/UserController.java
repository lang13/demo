package com.eem.demo.controller;

import com.eem.demo.entity.User;
import com.eem.demo.pojo.ReturnObj;
import com.eem.demo.repository.UserRepository;
import com.eem.demo.service.UserService;
import com.eem.demo.util.JwtUtil;
import org.apache.log4j.Logger;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
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

    /**
     * 用户登陆
     * @param user
     * @return
     */
    @RequestMapping("/login")
    public ReturnObj login(User user){
        ReturnObj obj = ReturnObj.fail();
        User login;
        if (userServiceImpl.isRegister(user.getUsername())){
            //将代码设为成功
            obj.setCode(100);
            obj.setMsg("登陆成功!!!");

            login = userServiceImpl.login(user.getUsername(), user.getPassword());
            obj.add("user",login);
            //token签名
            String token = JwtUtil.sign(login.getUsername(), String.valueOf(login.getId()));
            obj.add("token",token);
        }else{
            obj.setMsg("用户不存在!!!");
        }
        return obj;
    }

    @RequestMapping("/register")
    public ReturnObj register(User user){
        ReturnObj obj = ReturnObj.success();
        if (userServiceImpl.isRegister(user.getUsername())){
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
     * 上传用户头像文件
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
        if (dest.getParentFile() !=null && !dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            file.transferTo(dest);
            ReturnObj obj = ReturnObj.success();
            obj.setMsg("文件上传成功!!!");
            //数据库保存photo路径
            String username = JwtUtil.getUsername(request.getHeader("token"));
            userServiceImpl.updatePhoto(filePath,username);
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
     * 下载用户头像
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
     * 修改用户昵称
     * @param nickname
     * @param request
     * @return
     */
    @RequestMapping("/updateNickname")
    public ReturnObj updateNickname(String nickname, HttpServletRequest request){
        //获取用户token
        String token = request.getHeader("token");
        //获取token中的用户id
        String userId = JwtUtil.getUserId(token);
        //修改nickname
        int i = userServiceImpl.updateNickname(nickname, userId);
        if (i > 0){
            ReturnObj obj = ReturnObj.success();
            obj.setMsg("用户昵称修改成功!!!");
            return obj;
        }else{
            ReturnObj obj = ReturnObj.fail();
            obj.setMsg("用户昵称修改失败!!!");
            return obj;
        }
    }
}
