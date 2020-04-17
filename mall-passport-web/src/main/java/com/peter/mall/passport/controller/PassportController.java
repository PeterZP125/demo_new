package com.peter.mall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.peter.mall.beans.UmsMember;
import com.peter.mall.service.UmsMemberService;
import com.peter.mall.util.CookieUtil;
import com.peter.mall.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@CrossOrigin
public class PassportController {

    @Reference
    UmsMemberService umsMemberService;

    @RequestMapping("verify")
    @ResponseBody
    public String verify(String token, String currentIp) {
        //通过jwt校验token真假
        Map<String, String> map = new HashMap<>();
        Map<String, Object> decode = JwtUtil.decode(token, "mall", currentIp);

        if (decode != null) {
            map.put("status", "success");
            map.put("memberId", (String) decode.get("memberId"));
            map.put("nickname", (String) decode.get("nickname"));
        } else {
            map.put("status", "fail");
        }
        return JSON.toJSONString(map);
    }

    @RequestMapping("login")
    @ResponseBody
    public String login(UmsMember umsMember, HttpServletRequest request) {
        String token = "";

        UmsMember umsMemberLogin = umsMemberService.login(umsMember);
        if (umsMemberLogin != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("memberId", umsMemberLogin.getId());
            map.put("nickname", umsMemberLogin.getNickname());

            String ip = request.getHeader("x-forwarded-for");//获取nginx代理的请求IP
            if (StringUtils.isBlank(ip)) {
                ip = request.getRemoteAddr();//从request中获取IP
                if (StringUtils.isBlank(ip)) {
                    ip = "127.0.0.1";
                }
            }
            token = JwtUtil.encode("mall", map, ip);
            umsMemberService.addTokenToCache(umsMember, token);//将Token加入到缓存中
        } else {
            token = "fail";
        }
        return token;
    }

    @RequestMapping("index")
    public String index(String ReturnedUrl, ModelMap modelMap) {
        modelMap.put("ReturnedUrl", ReturnedUrl);
        return "index";
    }
}
