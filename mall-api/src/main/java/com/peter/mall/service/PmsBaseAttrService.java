package com.peter.mall.service;

import com.peter.mall.beans.PmsBaseAttrInfo;
import com.peter.mall.beans.PmsBaseAttrValue;

import java.util.List;

public interface PmsBaseAttrService {
    List<PmsBaseAttrInfo> getAttrInfoList(String catalog3Id);

    String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);

    List<PmsBaseAttrValue> getAttrValueList(String attrId);
}
