package com.peter.mall.service;

import com.peter.mall.beans.OmsOrder;

import java.math.BigDecimal;

public interface OrderService {
    String generateTradeCode(String memberId);

    String checkTradeCode(String memberId, String tradeCode);

    void saveOrder(OmsOrder omsOrder);

    OmsOrder getOrderByOutTradeNo(String outTradeNo);

    void updateByOutTradeNo(String out_trade_no);
}
