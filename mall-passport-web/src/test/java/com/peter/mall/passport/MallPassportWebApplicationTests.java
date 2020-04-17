package com.peter.mall.passport;

import com.alibaba.fastjson.JSON;
import com.peter.mall.HttpclientUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class MallPassportWebApplicationTests {

    @Test
    void testOAuth() {
        //code=7c5ab1cd96fd3ce5bd885c8f0c0e85f8
//        HttpclientUtil.doGet("https://api.weibo.com/oauth2/authorize?client_id=2843589023&response_type=code&redirect_uri=http://passport.mall.com:8050/weibologin");
//        Map<String, String> param = new HashMap<>();
//        param.put("client_id","2843589023");
//        param.put("client_secret","ba37fbdb7c9ca94ca13964a38f92135b");
//        param.put("grant_type","authorization_code");
//        param.put("redirect_uri","http://passport.mall.com:8050/weibologin");
//        param.put("code","7c5ab1cd96fd3ce5bd885c8f0c0e85f8");
//        String s2 = HttpclientUtil.doPost("https://api.weibo.com/oauth2/access_token?",param);
//
//        Map<String,String> map = JSON.parseObject(s2, Map.class);
//        System.out.println(map);
        String userInfo = HttpclientUtil.doGet("https://api.weibo.com/2/users/show.json?access_token=2.0049b4ICF668GD59c6f32a6c0yZhFSPB&uid=1956101883&uid=1956101883");
        Map<String, String> map = JSON.parseObject(userInfo, Map.class);
        System.out.println(map);
    }

}
