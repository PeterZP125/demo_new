package com.peter.mall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.peter.mall.beans.OmsOrder;
import com.peter.mall.beans.OmsOrderItem;
import com.peter.mall.order.mapper.OmsOrderItemMapper;
import com.peter.mall.order.mapper.OmsOrderMapper;
import com.peter.mall.service.CartService;
import com.peter.mall.service.OrderService;
import com.peter.mall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OmsOrderMapper omsOrderMapper;
    @Autowired
    OmsOrderItemMapper omsOrderItemMapper;
    @Autowired
    RedisUtil redisUtil;
    @Reference
    CartService cartService;

    @Override
    public String generateTradeCode(String memberId) {
        Jedis jedis = redisUtil.getJedis();
        String tradeCode = UUID.randomUUID().toString();
        jedis.setex("user:" + memberId + ":tradeCode", 60 * 15, tradeCode);
        jedis.close();
        return tradeCode;
    }

    @Override
    public String checkTradeCode(String memberId, String tradeCode) {
        Jedis jedis = redisUtil.getJedis();

        try {
            String tradeCodeFromCache = jedis.get("user:" + memberId + ":tradeCode");
            //对比防重删令牌
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Long eval = (Long) jedis.eval(script, Collections.singletonList("user:" + memberId + ":tradeCode"),
                    Collections.singletonList(tradeCode));
            if (eval != null && eval != 0) {
                return "success";
            } else {
                return "fail";
            }
        } finally {
            jedis.close();
        }
    }

    @Override
    public void saveOrder(OmsOrder omsOrder) {
        omsOrderMapper.insertSelective(omsOrder);
        List<OmsOrderItem> omsOrderItemList = omsOrder.getOmsOrderItemList();
        for (OmsOrderItem omsOrderItem : omsOrderItemList) {
            omsOrderItem.setOrderId(omsOrder.getId());
            omsOrderItemMapper.insertSelective(omsOrderItem);
            //删除购物车数据
//            cartService.deleteCartItem();
        }
    }

    @Override
    public OmsOrder getOrderByOutTradeNo(String outTradeNo) {
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setOrderSn(outTradeNo);
        omsOrder = omsOrderMapper.selectOne(omsOrder);
        return omsOrder;
    }

    @Override
    public void updateByOutTradeNo(String out_trade_no) {
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setOrderSn(out_trade_no);
        omsOrder.setStatus(1);

        Example example = new Example(OmsOrder.class);
        example.createCriteria().andEqualTo("orderSn", omsOrder.getOrderSn());

        omsOrderMapper.updateByExampleSelective(omsOrder, example);
    }

}
