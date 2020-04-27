package com.peter.mall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.peter.mall.beans.PmsSkuAttrValue;
import com.peter.mall.beans.PmsSkuImage;
import com.peter.mall.beans.PmsSkuInfo;
import com.peter.mall.beans.PmsSkuSaleAttrValue;
import com.peter.mall.manage.mapper.PmsSkuAttrValueMapper;
import com.peter.mall.manage.mapper.PmsSkuImageMapper;
import com.peter.mall.manage.mapper.PmsSkuInfoMapper;
import com.peter.mall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.peter.mall.service.SkuService;
import com.peter.mall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    PmsSkuAttrValueMapper pmsSkuAttrValueMapper;

    @Autowired
    PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

    @Autowired
    PmsSkuImageMapper pmsSkuImageMapper;

    @Autowired
    RedisUtil redisUtil;

    public String saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
        pmsSkuInfoMapper.insert(pmsSkuInfo);

        for (PmsSkuAttrValue pmsSkuAttrValue : pmsSkuInfo.getSkuAttrValueList()) {
            pmsSkuAttrValue.setSkuId(pmsSkuInfo.getId());
            pmsSkuAttrValueMapper.insert(pmsSkuAttrValue);
        }

        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : pmsSkuInfo.getSkuSaleAttrValueList()) {
            pmsSkuSaleAttrValue.setSkuId(pmsSkuInfo.getId());
            pmsSkuSaleAttrValueMapper.insert(pmsSkuSaleAttrValue);
        }

        for (PmsSkuImage pmsSkuImage : pmsSkuInfo.getSkuImageList()) {
            pmsSkuImage.setSkuId(pmsSkuInfo.getId());
            pmsSkuImageMapper.insert(pmsSkuImage);
        }
        return "success";
    }

    @Override
    public PmsSkuInfo getPmsSkuInfoById(String skuId) {
        Jedis jedis = redisUtil.getJedis();
        String key = "sku:" + skuId + ":info";
        String jsonString = jedis.get(key);
        PmsSkuInfo pmsSkuInfo = null;
        if (StringUtils.isNotBlank(jsonString)) {
            //从Redis中获取PmsSkuInfo对象
            pmsSkuInfo = JSON.parseObject(jsonString, PmsSkuInfo.class);
        } else {
            String token = UUID.randomUUID().toString();
            //设置redis锁，防止缓存穿透
            String OK = jedis.set("sku:" + skuId + ":lock", token, "nx", "px", 10);
            //设置锁成功
            if (StringUtils.isNotBlank(OK) && "OK".equals(OK)) {
                //Redis中没有，则从数据库中查询
                pmsSkuInfo = getPmsSkuInfoByIdFromDB(skuId);
                if (pmsSkuInfo != null) {
                    //再把数据放入Redis中
                    jedis.set(key, JSON.toJSONString(pmsSkuInfo));
                } else {
                    //防止缓存穿透
                    jedis.setex(key, 60 * 3, JSON.toJSONString(""));
                }
                //释放锁
                String lockVal = jedis.get("sku:" + skuId + ":lock");
                if (StringUtils.isNotBlank(lockVal) && lockVal.equals(token)) {
//                    jedis.del("sku"+skuId+"lock");
//                    lua脚本解决分布式高并发删锁的问题
                    String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                    jedis.eval(script, Collections.singletonList("lock"), Collections.singletonList(token));
                }
            } else {
                //设置锁失败,自旋
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return getPmsSkuInfoById(skuId);
            }
        }
        return pmsSkuInfo;
    }

    private PmsSkuInfo getPmsSkuInfoByIdFromDB(String skuId) {
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);
        PmsSkuInfo pmsSkuInfo1 = pmsSkuInfoMapper.selectOne(pmsSkuInfo);
        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        List<PmsSkuImage> pmsSkuImageList = pmsSkuImageMapper.select(pmsSkuImage);
        pmsSkuInfo1.setSkuImageList(pmsSkuImageList);
        return pmsSkuInfo1;
    }

    @Override
    public List<PmsSkuInfo> getProductSaleAttrValueTableBySpuId(String productId) {
        return pmsSkuInfoMapper.selectProductSaleAttrValueTableBySpuId(productId);
    }

    @Override
    public List<PmsSkuInfo> getAllPmsSkuInfo() {
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.selectAll();

        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
            PmsSkuAttrValue pmsSkuAttrValue = new PmsSkuAttrValue();
            pmsSkuAttrValue.setSkuId(pmsSkuInfo.getId());
            List<PmsSkuAttrValue> pmsSkuAttrValues = pmsSkuAttrValueMapper.select(pmsSkuAttrValue);
            pmsSkuInfo.setSkuAttrValueList(pmsSkuAttrValues);
        }
        return pmsSkuInfos;
    }

    @Override
    public boolean checkPrice(String productSkuId, BigDecimal productPrice) {
        boolean ret = false;

        PmsSkuInfo skuInfo = new PmsSkuInfo();
        skuInfo.setId(productSkuId);
        skuInfo = pmsSkuInfoMapper.selectOne(skuInfo);
        BigDecimal price = skuInfo.getPrice();
        if(price.compareTo(productPrice) == 0){
            ret = true;
        }
        return ret;
    }
}
