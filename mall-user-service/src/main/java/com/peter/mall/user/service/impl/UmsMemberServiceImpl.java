package com.peter.mall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.peter.mall.beans.UmsMember;
import com.peter.mall.service.UmsMemberService;
import com.peter.mall.user.mapper.UmsMemberMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class UmsMemberServiceImpl implements UmsMemberService {

    @Autowired
    UmsMemberMapper umsMemberMapper;

    @Override
    public List<UmsMember> getUmsMemberById(String id) {
        UmsMember umsMember = new UmsMember();
        umsMember.setId(id);
        List<UmsMember> umsMembers = umsMemberMapper.select(umsMember);
        return umsMembers;
    }
}
