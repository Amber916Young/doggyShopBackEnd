package com.doggy.config;

import com.doggy.entity.CustomerInfo;
import com.doggy.utils.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.*;
import java.io.PrintWriter;

/**
 * @ClassName:LoginInterceptor
 * @Auther: yyj
 * @Description:
 * @Date: 25/11/2022 22:39
 * @Version: v1.0
 */
public class LoginInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

    /*
     * 在请求处理之前进行调用(Controller方法调用之前)
     * 若返回true请求将会继续执行后面的操作
     * */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        String path = request.getSession().getServletContext().getContextPath();

        // 如果不是映射到方法不拦截 直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        RequestDispatcher redirect = request.getRequestDispatcher("/error/invalid");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        if (null == token || "".equals(token)) {
            redirect.forward(request, response);
            return false;
        }
        //验证token
        if (!TokenUtils.isTokenExpired(token)) {
            redirect.forward(request, response);
            return false;
        }
        //若token验证成功，把用户信息存储在ThreadLocal
        // add log表
//        CustomerInfo customer = JwtUtils.getUserByToken(token);
//        UserUtils.setLoginUser(user);

        return true;
    }

    /***
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("执行了拦截器的postHandle方法");
    }

    /***
     * 整个请求结束之后被调用，也就是在DispatchServlet渲染了对应的视图之后执行（主要用于进行资源清理工作）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清除线程变量
//        UserUtils.removeUser();
    }

}
