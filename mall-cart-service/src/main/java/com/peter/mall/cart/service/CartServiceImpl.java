package com.peter.mall.cart.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.peter.mall.beans.OmsCartItem;
import com.peter.mall.cart.mapper.OmsCartItemMapper;
import com.peter.mall.service.CartService;
import com.peter.mall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    OmsCartItemMapper omsCartItemMapper;
    @Autowired
    RedisUtil redisUtil;

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
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        List<OmsCartItem> omsCartItems = omsCartItemMapper.select(omsCartItem);

        Map<String, String> cartMap = new HashMap<>();
        for (OmsCartItem cartItem : omsCartItems) {
            cartMap.put(cartItem.getProductSkuId(), JSON.toJSONString(cartItem));
        }

        Jedis jedis = redisUtil.getJedis();
        jedis.del("user:" + memberId + ":cart");
        jedis.hmset("user:" + memberId + ":cart", cartMap);
        jedis.close();
    }

    @Override
    public List<OmsCartItem> getOmsCartItemByMemberId(String memberId) {
        //该处代码和商品详情页面中获取Sku业务逻辑一样，此处简写
        Jedis jedis = redisUtil.getJedis();

        List<OmsCartItem> omsCartItems = new ArrayList<>();

        List<String> hvals = jedis.hvals("user:" + memberId + ":cart");
        for (String hval : hvals) {
            OmsCartItem omsCartItem = JSON.parseObject(hval, OmsCartItem.class);
            omsCartItems.add(omsCartItem);
        }
        jedis.close();
        return omsCartItems;
    }

    @Override
    public void checkCart(OmsCartItem omsCartItem) {
        Example example = new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("memberId", omsCartItem.getMemberId()).andEqualTo("productSkuId", omsCartItem.getProductSkuId());
        omsCartItemMapper.updateByExampleSelective(omsCartItem, example);

        //同步缓存
        flushCartCache(omsCartItem.getMemberId());
    }
}
