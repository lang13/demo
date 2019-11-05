package com.eem.demo.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eem.demo.pojo.ReturnObj;
import com.eem.demo.util.JwtUtil;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 拦截器实体类
 * @author Administrator
 */
public class InterceptorConfig implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Logger logger = Logger.getLogger(InterceptorConfig.class);

        //解决后台获取前端数据中文乱码问题
        response.setCharacterEncoding("utf-8");
        //从请求头中拿取token
        //前端需要设置token的唯一标识
        String token = request.getHeader("token");

        //放行的请求
        //登陆放行,在WebMvcConfig里面配置

        //验证token是否合法
        if(token != null){
            boolean isSuccess = JwtUtil.verify(token);
            if(isSuccess){
                return true;
            }
        }

        //放行的请求
        if ("/photo/downloadPhoto".equals(request.getRequestURI())){
            return true;
        }

        //给前端返回信息
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        ReturnObj obj = ReturnObj.fail();
        obj.setMsg("请求被拦截");
        writer.write(JSON.toJSONString(obj));


        logger.info("请求" + request.getRequestURI() + "被拦截...");
        return false;
        //测试,调为true
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
