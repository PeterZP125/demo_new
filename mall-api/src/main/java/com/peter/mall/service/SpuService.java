package com.peter.mall.service;

import com.peter.mall.beans.PmsProductInfo;

import java.util.List;

public interface SpuService {
    List<PmsProductInfo> getSpuList(String catalog3Id);

    String saveSpuInfo(PmsProductInfo pmsProductInfo);
}
