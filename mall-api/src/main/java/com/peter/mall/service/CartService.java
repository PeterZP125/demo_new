package com.peter.mall.service;

import com.peter.mall.beans.OmsCartItem;

import java.util.List;

public interface CartService {
    OmsCartItem ifExistByMember(String memberId, String skuId);

    void addToCart(OmsCartItem omsCartItem);

    void updateCart(OmsCartItem cartItemFromDb);

    void flushCartCache(String memberId);

    List<OmsCartItem> getOmsCartItemByMemberId(String memberId);

    void checkCart(OmsCartItem omsCartItem);
}
