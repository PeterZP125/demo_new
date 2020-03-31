package com.peter.mall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.peter.mall.beans.PmsBaseSaleAttr;
import com.peter.mall.beans.PmsProductImage;
import com.peter.mall.beans.PmsProductInfo;
import com.peter.mall.beans.PmsProductSaleAttr;
import com.peter.mall.manage.util.FileUploadUtil;
import com.peter.mall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin
public class ProductController {

    @Reference
    SpuService spuService;

    @RequestMapping("fileUpload")
    public String fileUpload(@RequestParam("file") MultipartFile multipartFile) {
        String url = FileUploadUtil.fileUpload(multipartFile);
        return url;
    }

    @RequestMapping("spuList")
    public List<PmsProductInfo> spuList(String catalog3Id) {
        return spuService.getSpuList(catalog3Id);
    }

    @RequestMapping("saveSpuInfo")
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo) {
        return spuService.saveSpuInfo(pmsProductInfo);
    }

    @RequestMapping("spuSaleAttrList")
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId) {
        return spuService.getPmsProductSaleAttrBySpuId(spuId);
    }

    @RequestMapping("spuImageList")
    public List<PmsProductImage> spuImageList(String spuId) {
        return spuService.getPmsProductImageBySpuId(spuId);
    }
}
