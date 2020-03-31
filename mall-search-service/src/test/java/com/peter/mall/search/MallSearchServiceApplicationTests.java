package com.peter.mall.search;


import com.alibaba.dubbo.config.annotation.Reference;
import com.peter.mall.beans.PmsSkuAttrValue;
import com.peter.mall.beans.PmsSkuInfo;
import com.peter.mall.service.SkuService;
import io.searchbox.client.JestClient;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
class MallSearchServiceApplicationTests {

    @Reference
    SkuService skuService;


    @Autowired
    JestClient jestClient;

    @Test
    void contextLoads() {
        System.out.println(skuService);
        List<PmsSkuInfo> pmsSkuInfos = skuService.getAllPmsSkuInfo();
        System.out.println(pmsSkuInfos);
    }

}
