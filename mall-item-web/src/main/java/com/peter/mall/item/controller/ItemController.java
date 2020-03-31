package com.peter.mall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.peter.mall.beans.PmsProductSaleAttr;
import com.peter.mall.beans.PmsSkuInfo;
import com.peter.mall.beans.PmsSkuSaleAttrValue;
import com.peter.mall.service.SkuService;
import com.peter.mall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@CrossOrigin
public class ItemController {

    @Reference
    SkuService skuService;
    @Reference
    SpuService spuService;

    @RequestMapping("{skuId}.html")
    public String item(@PathVariable String skuId, ModelMap modelMap) {
        PmsSkuInfo pmsSkuInfo = skuService.getPmsSkuInfoById(skuId);
        modelMap.put("skuInfo",pmsSkuInfo);
        List<PmsProductSaleAttr> pmsProductSaleAttrs = spuService.getSpuSaleAttrListCheckBySku(pmsSkuInfo.getProductId(),pmsSkuInfo.getId());
        modelMap.put("spuSaleAttrListCheckBySku",pmsProductSaleAttrs);
        List<PmsSkuInfo> pmsSkuInfos = skuService.getProductSaleAttrValueTableBySpuId(pmsSkuInfo.getProductId());
        Map<String, String> saleAttrValueMap = new HashMap<>();
        for (PmsSkuInfo skuInfo : pmsSkuInfos) {
            String value = skuInfo.getId();
            String key = "";
            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuInfo.getSkuSaleAttrValueList()) {
                key += pmsSkuSaleAttrValue.getSaleAttrValueId()+"|";
            }
            saleAttrValueMap.put(key,value);
        }
        //向前台返回JSON字符串
        String saleAttrValueJson = JSON.toJSONString(saleAttrValueMap);
        modelMap.put("saleAttrValueJson",saleAttrValueJson);
        return "item";
    }
}
