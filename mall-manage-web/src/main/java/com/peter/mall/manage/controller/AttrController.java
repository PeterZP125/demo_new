package com.peter.mall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.peter.mall.beans.PmsBaseAttrInfo;
import com.peter.mall.beans.PmsBaseAttrValue;
import com.peter.mall.beans.PmsBaseSaleAttr;
import com.peter.mall.service.PmsBaseAttrService;
import com.peter.mall.service.PmsBaseSaleAttrService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class AttrController {

    @Reference
    PmsBaseAttrService pmsBaseAttrService;
    @Reference
    PmsBaseSaleAttrService pmsBaseSaleAttrService;

    @RequestMapping("attrInfoList")
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id){
        return pmsBaseAttrService.getAttrInfoList(catalog3Id);
    }

    @RequestMapping("saveAttrInfo")
    public String saveAttrInfo(@RequestBody PmsBaseAttrInfo pmsBaseAttrInfo){
        String ret = pmsBaseAttrService.saveAttrInfo(pmsBaseAttrInfo);
        return ret;
    }

    @RequestMapping("getAttrValueList")
    public List<PmsBaseAttrValue> getAttrValueList(String attrId){
        List<PmsBaseAttrValue> pmsBaseAttrValues = pmsBaseAttrService.getAttrValueList(attrId);
        return pmsBaseAttrValues;
    }

    @RequestMapping("baseSaleAttrList")
    public List<PmsBaseSaleAttr> baseSaleAttrList(){
        return pmsBaseSaleAttrService.getPmsBaseSaleAttr();
    }
}
