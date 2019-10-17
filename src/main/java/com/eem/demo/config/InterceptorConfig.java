package com.eem.demo.config;

import com.eem.demo.util.JwtUtil;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器实体类
 * @author Administrator
 */
public class InterceptorConfig implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //解决后台获取前端数据中文乱码问题
        response.setCharacterEncoding("utf-8");
        //从请求头中拿取token
        //前端需要设置token的唯一标识
        String token = request.getHeader("token");

        //放行的请求
        //登陆放行,在WebMvcConfig里面配置

        //验证token是否合法
        if( token != null){
            boolean isSuccess = JwtUtil.verify(token);
            if(isSuccess){
                return true;
            }
        }

        System.out.println("方法被拦截...");
        return true;
        //测试,调为true
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
