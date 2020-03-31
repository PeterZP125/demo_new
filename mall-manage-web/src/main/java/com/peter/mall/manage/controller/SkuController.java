package com.peter.mall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.peter.mall.beans.PmsBaseCatalog1;
import com.peter.mall.beans.PmsBaseCatalog2;
import com.peter.mall.beans.PmsBaseCatalog3;
import com.peter.mall.beans.PmsSkuInfo;
import com.peter.mall.service.CatalogService;
import com.peter.mall.service.SkuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class SkuController {

    @Reference
    SkuService skuService;

    @RequestMapping("saveSkuInfo")
    public String saveSkuInfo(@RequestBody PmsSkuInfo pmsSkuInfo) {
        pmsSkuInfo.setProductId(pmsSkuInfo.getSpuId());
        if(StringUtils.isBlank(pmsSkuInfo.getSkuDefaultImg())){
            pmsSkuInfo.setSkuDefaultImg(pmsSkuInfo.getSkuImageList().get(0).getImgUrl());
        }
        return skuService.saveSkuInfo(pmsSkuInfo);
    }
}
