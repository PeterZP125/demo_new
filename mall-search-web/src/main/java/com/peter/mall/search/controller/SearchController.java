package com.peter.mall.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.peter.mall.annotations.RequiredLoginAnno;
import com.peter.mall.beans.*;
import com.peter.mall.query.SkuInfoQueryParam;
import com.peter.mall.service.PmsBaseAttrService;
import com.peter.mall.service.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
@CrossOrigin
public class SearchController {

    @Reference
    SearchService searchService;
    @Reference
    PmsBaseAttrService pmsBaseAttrService;

    @RequestMapping("list.html")
    public String list(SkuInfoQueryParam skuInfoQueryParam, ModelMap modelMap) {
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = searchService.getPmsSearchSkuInfoByParam(skuInfoQueryParam);
        modelMap.put("skuLsInfoList", pmsSearchSkuInfos);

        //获取平台属性列表
        Set<String> valueIdSet = new HashSet<>();
        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
            for (PmsSkuAttrValue pmsSkuAttrValue : pmsSearchSkuInfo.getSkuAttrValueList()) {
                valueIdSet.add(pmsSkuAttrValue.getValueId());
            }
        }
        List<PmsBaseAttrInfo> pmsBaseAttrInfoList = pmsBaseAttrService.getPmsBaseAttrInfoByBaseAttrValueId(valueIdSet);
        modelMap.put("attrList", pmsBaseAttrInfoList);

        //将已选的属性值的属性列表删除
        String[] valueId1 = skuInfoQueryParam.getValueId();
        if (valueId1 != null) {
            Iterator<PmsBaseAttrInfo> iterator = pmsBaseAttrInfoList.iterator();
            while (iterator.hasNext()) {
                PmsBaseAttrInfo pmsBaseAttrInfo = iterator.next();
                for (PmsBaseAttrValue pmsBaseAttrValue : pmsBaseAttrInfo.getAttrValueList()) {
                    for (String s : valueId1) {
                        if (s.equals(pmsBaseAttrValue.getId())) {
                            iterator.remove();
                        }
                    }
                }
            }
        }
        String urlParam = getUrlParam(skuInfoQueryParam);
        modelMap.put("urlParam", urlParam);
        String keyword = skuInfoQueryParam.getKeyword();
        if (StringUtils.isNotBlank(keyword)) {
            modelMap.put("keyword", keyword);
        }
//
//        //面包屑功能
//        List<PmsSearchSkuCrumb> pmsSearchSkuCrumbList = new ArrayList<>();
//        if (valueId1 != null) {
//            for (String s : valueIdSet) {
//                PmsSearchSkuCrumb pmsSearchSkuCrumb = new PmsSearchSkuCrumb();
//                pmsSearchSkuCrumb.setValueId(s);
//                pmsSearchSkuCrumb.setValueId(s);
//                pmsSearchSkuCrumbList.add(pmsSearchSkuCrumb);
//            }
//        }
//        modelMap.put("attrValueSelectedList", pmsSearchSkuCrumbList);
        return "list";
    }

    private String getUrlParam(SkuInfoQueryParam skuInfoQueryParam) {
        String catalog3Id = skuInfoQueryParam.getCatalog3Id();
        String keyword = skuInfoQueryParam.getKeyword();
        String[] valueIds = skuInfoQueryParam.getValueId();

        String urlParam = "";

        if (StringUtils.isNotBlank(keyword)) {
            urlParam += "&keyword=" + keyword;
        }
        if (StringUtils.isNotBlank(catalog3Id)) {
            urlParam += "&catalog3Id=" + catalog3Id;
        }
        if (valueIds != null) {
            for (String valueId : valueIds) {
                urlParam += "&valueId=" + valueId;
            }
        }

        urlParam = urlParam.substring(1);
        System.out.println(urlParam);
        return urlParam;
    }

    @RequestMapping("index")
    @RequiredLoginAnno(loginSuccess = false)
    public String index() {
        return "index";
    }
}
