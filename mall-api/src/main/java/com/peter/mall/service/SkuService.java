package com.peter.mall.service;

import com.peter.mall.beans.PmsSkuInfo;

import java.math.BigDecimal;
import java.util.List;

public interface SkuService {
    String saveSkuInfo(PmsSkuInfo pmsSkuInfo);

    PmsSkuInfo getPmsSkuInfoById(String skuId);

    List<PmsSkuInfo> getProductSaleAttrValueTableBySpuId(String productId);

    List<PmsSkuInfo> getAllPmsSkuInfo();

    boolean checkPrice(String productSkuId, BigDecimal productPrice);
}
