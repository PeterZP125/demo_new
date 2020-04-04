package com.peter.mall.service;

import com.peter.mall.beans.PmsBaseAttrInfo;
import com.peter.mall.beans.PmsBaseAttrValue;

import java.util.List;
import java.util.Set;

public interface PmsBaseAttrService {
    List<PmsBaseAttrInfo> getAttrInfoList(String catalog3Id);

    String saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);

    List<PmsBaseAttrValue> getAttrValueList(String attrId);

    List<PmsBaseAttrInfo> getPmsBaseAttrInfoByBaseAttrValueId(Set<String> valueIdSet);
}
