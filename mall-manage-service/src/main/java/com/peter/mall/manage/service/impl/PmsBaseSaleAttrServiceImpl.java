package com.peter.mall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.peter.mall.beans.PmsBaseSaleAttr;
import com.peter.mall.manage.mapper.PmsBaseSaleAttrMapper;
import com.peter.mall.service.PmsBaseSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class PmsBaseSaleAttrServiceImpl implements PmsBaseSaleAttrService {

    @Autowired
    PmsBaseSaleAttrMapper pmsBaseSaleAttrMapper;

    @Override
    public List<PmsBaseSaleAttr> getPmsBaseSaleAttr() {
        return pmsBaseSaleAttrMapper.selectAll();
    }
}
