package com.peter.mall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.google.gson.annotations.JsonAdapter;
import com.peter.mall.beans.UmsMember;
import com.peter.mall.service.UmsMemberService;
import com.peter.mall.user.mapper.UmsMemberMapper;
import com.peter.mall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class UmsMemberServiceImpl implements UmsMemberService {

    @Autowired
    UmsMemberMapper umsMemberMapper;
    @Autowired
    RedisUtil redisUtil;

    @Override
    public List<UmsMember> getUmsMemberById(String id) {
        UmsMember umsMember = new UmsMember();
        umsMember.setId(id);
        List<UmsMember> umsMembers = umsMemberMapper.select(umsMember);
        return umsMembers;
    }

    @Override
    public UmsMember login(UmsMember umsMember) {
        Jedis jedis = null;

        try {
            jedis = redisUtil.getJedis();
            if (jedis != null) {
                String userInfo = jedis.get("user:" + umsMember.getUsername() + umsMember.getPassword() + ":info");
                if (StringUtils.isNotBlank(userInfo)) {
                    UmsMember umsMemberFromCache = JSON.parseObject(userInfo, UmsMember.class);
                    return umsMemberFromCache;
                }
            }
            UmsMember umsMemberFromDB = loginFromDB(umsMember);
            if (umsMemberFromDB != null) {
                jedis.setex("user:" + umsMember.getUsername() + umsMember.getPassword() + ":info", 60 * 60 * 24, JSON.toJSONString(umsMemberFromDB));
            }
            return umsMemberFromDB;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return null;
    }

    @Override
    public void addTokenToCache(UmsMember umsMember, String token) {
        Jedis jedis = redisUtil.getJedis();
        jedis.setex("user:" + umsMember.getId() + ":token", 60 * 60 * 2, token);
        jedis.close();
    }

    private UmsMember loginFromDB(UmsMember umsMember) {
        UmsMember member = umsMemberMapper.selectOne(umsMember);
        return member;
    }
}
