package com.peter.mall.service;

import com.peter.mall.beans.PmsProductImage;
import com.peter.mall.beans.PmsProductInfo;
import com.peter.mall.beans.PmsProductSaleAttr;

import java.util.List;

public interface SpuService {
    List<PmsProductInfo> getSpuList(String catalog3Id);

    String saveSpuInfo(PmsProductInfo pmsProductInfo);

    List<PmsProductSaleAttr> getPmsProductSaleAttrBySpuId(String spuId);

    List<PmsProductImage> getPmsProductImageBySpuId(String spuId);

    List<PmsProductSaleAttr> getSpuSaleAttrListCheckBySku(String productId, String skuId);
}
