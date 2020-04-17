package com.peter.mall.service;

import com.peter.mall.beans.UmsMember;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface UmsMemberService {
    List<UmsMember> getUmsMemberById(String id);

    UmsMember login(UmsMember umsMember);

    void addTokenToCache(UmsMember umsMember, String token);
}
