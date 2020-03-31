package com.peter.mall.manage.mapper;

import com.peter.mall.beans.PmsSkuInfo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PmsSkuInfoMapper extends Mapper<PmsSkuInfo> {
    List<PmsSkuInfo> selectProductSaleAttrValueTableBySpuId(String productId);
}
