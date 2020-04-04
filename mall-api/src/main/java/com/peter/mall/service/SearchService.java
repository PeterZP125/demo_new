package com.peter.mall.service;

import com.peter.mall.beans.PmsSearchSkuInfo;
import com.peter.mall.query.SkuInfoQueryParam;

import java.util.List;

public interface SearchService {
    List<PmsSearchSkuInfo> getPmsSearchSkuInfoByParam(SkuInfoQueryParam skuInfoQueryParam);
}
