package com.peter.mall.manage.mapper;

import com.peter.mall.beans.PmsBaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PmsBaseAttrInfoMapper extends Mapper<PmsBaseAttrInfo> {
    List<PmsBaseAttrInfo> selectPmsBaseAttrInfoByBaseAttrValueId(@Param("valueIdStr") String valueIdStr);
}
