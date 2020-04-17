package com.peter.mall.interceptor;

import com.alibaba.fastjson.JSON;
import com.peter.mall.HttpclientUtil;
import com.peter.mall.annotations.RequiredLoginAnno;
import com.peter.mall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HandlerMethod hm = (HandlerMethod) handler;
        RequiredLoginAnno anno = hm.getMethodAnnotation(RequiredLoginAnno.class);
        if (anno == null) {
            return true;
        }
        System.out.println(request.getRequestURL());
        String token = "";
        String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);
        String newToken = (String) request.getParameter("token");
        if (StringUtils.isNotBlank(oldToken)) {
            token = oldToken;
        }
        if (StringUtils.isNotBlank(newToken)) {
            token = newToken;
        }

        //调用认证中心认证
        Map<String, String> successMap = new HashMap<>();
        String success = "fail";
        if (StringUtils.isNotBlank(token)) {
            String ip = request.getHeader("x-forwarded-for");//获取nginx代理的请求
            if (StringUtils.isBlank(ip)) {
                ip = request.getRemoteAddr();
                if (StringUtils.isBlank(ip)) {
                    ip = "127.0.0.1";
                }
            }
            String successJson = HttpclientUtil.doGet("http://passport.mall.com:8070/verify?token=" + token + "&currentIp=" + ip);
            successMap = JSON.parseObject(successJson, Map.class);
            success = successMap.get("status");
        }

        boolean loginSuccess = anno.loginSuccess();
        if (loginSuccess) {
            //必须登录才能使用
            if (!success.equals("success")) {
                //重定向回passport登录
                StringBuffer url = request.getRequestURL();
                response.sendRedirect("http://passport.mall.com:8070/index?ReturnedUrl=" + url);
                return false;
            }
            //验证通过，覆盖cookie中的token
            if (StringUtils.isNotBlank(token)) {
                CookieUtil.setCookie(request, response, "oldToken", token, 60 * 60 * 2, true);
            }
            //需要将token携带的用户信息写入
            request.setAttribute("memberId", successMap.get("memberId"));
            request.setAttribute("nickname", successMap.get("nickname"));
        } else {
            //没有登录也能用，但是必须验证
            if (success.equals("success")) {
                //需要将token携带的用户信息写入
                request.setAttribute("memberId", successMap.get("memberId"));
                request.setAttribute("nickname", successMap.get("nickname"));
                if (StringUtils.isNotBlank(token)) {
                    CookieUtil.setCookie(request, response, "oldToken", token, 60 * 60 * 2, true);
                }
            }
        }
        return true;
    }
}