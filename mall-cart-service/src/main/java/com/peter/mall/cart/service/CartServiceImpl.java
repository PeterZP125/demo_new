package com.peter.mall.cart.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.peter.mall.beans.OmsCartItem;
import com.peter.mall.cart.mapper.OmsCartItemMapper;
import com.peter.mall.service.CartService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    OmsCartItemMapper omsCartItemMapper;

    @Override
    public OmsCartItem ifExistByMember(String memberId, String skuId) {
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        omsCartItem.setProductSkuId(skuId);
        OmsCartItem omsCartItem1 = omsCartItemMapper.selectOne(omsCartItem);
        return omsCartItem1;
    }

    @Override
    public void addToCart(OmsCartItem omsCartItem) {
        String memberId = omsCartItem.getMemberId();
        if (StringUtils.isNotBlank(memberId)) {
            omsCartItemMapper.insertSelective(omsCartItem);
        }
    }

    @Override
    public void updateCart(OmsCartItem cartItemFromDb) {
        Example example = new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("id", cartItemFromDb.getId());
        omsCartItemMapper.updateByExampleSelective(cartItemFromDb, example);
    }

    @Override
    public void flushCartCache(String memberId) {

    }
}
