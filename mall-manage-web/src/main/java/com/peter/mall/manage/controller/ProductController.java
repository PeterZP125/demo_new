package com.peter.mall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.peter.mall.beans.PmsBaseSaleAttr;
import com.peter.mall.beans.PmsProductInfo;
import com.peter.mall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class ProductController {

    @Reference
    SpuService spuService;

    @RequestMapping("spuList")
    public List<PmsProductInfo> spuList(String catalog3Id){
        return spuService.getSpuList(catalog3Id);
    }

    @RequestMapping("saveSpuInfo")
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo){
       return spuService.saveSpuInfo(pmsProductInfo);
    }

}
