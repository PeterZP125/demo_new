package com.peter.mall.cart;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.peter.mall.beans.OmsCartItem;
import com.peter.mall.beans.PmsSkuInfo;
import com.peter.mall.service.CartService;
import com.peter.mall.service.SkuService;
import com.peter.mall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@CrossOrigin
public class CartController {

    @Reference
    SkuService skuService;
    @Reference
    CartService cartService;

    @RequestMapping("addToCart")
    public String addToCart(String skuId, Integer quantity, HttpServletRequest request, HttpServletResponse response) {
        PmsSkuInfo pmsSkuInfo = skuService.getPmsSkuInfoById(skuId);
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setDeleteStatus(0);
        omsCartItem.setPrice(pmsSkuInfo.getPrice());
        omsCartItem.setProductCategoryId(pmsSkuInfo.getCatalog3Id());
        omsCartItem.setProductPic(pmsSkuInfo.getSkuDefaultImg());
        omsCartItem.setProductName(pmsSkuInfo.getSkuName());
        omsCartItem.setQuantity(quantity);
        omsCartItem.setProductId(pmsSkuInfo.getProductId());
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setModifyDate(new Date());

        //放入Cookie中的购物车
        List<OmsCartItem> omsCartItemList = new ArrayList<>();
        //查看用户是否登录
        String memberId = "1";
        if (StringUtils.isNotBlank(memberId)) {
            //查看用户是否添加过该商品
            OmsCartItem cartItemFromDb = cartService.ifExistByMember(memberId, skuId);
            if(cartItemFromDb != null){
                //如果用户没有添加过该商品
                omsCartItem.setMemberId(memberId);
                cartService.addToCart(omsCartItem);
            }else{
                cartItemFromDb.setQuantity(cartItemFromDb.getQuantity()+omsCartItem.getQuantity());
                cartService.updateCart(cartItemFromDb);
            }

            cartService.flushCartCache(memberId);
        } else {
            //用户没有登录，将购物车添加到Cookie中
            //检查Cookie中是否已有购物车数据
            String cookieValue = CookieUtil.getCookieValue(request, "cartItemCookie", true);
            if (StringUtils.isNotBlank(cookieValue)) {
                //购物车已有数据
                omsCartItemList = JSON.parseArray(cookieValue, OmsCartItem.class);
                System.out.println("cartSize"+omsCartItemList.size());
                if (isExistInCart(omsCartItemList, omsCartItem)) {
                    for (OmsCartItem cartItem : omsCartItemList) {
                        if(cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())){
                            cartItem.setQuantity(cartItem.getQuantity() + omsCartItem.getQuantity());
                            cartItem.setPrice(cartItem.getPrice().add(omsCartItem.getPrice()));
                        }
                    }
                }else{
                    omsCartItemList.add(omsCartItem);
                }
            } else {
                omsCartItemList.add(omsCartItem);
            }
            String cartJson = JSON.toJSONString(omsCartItemList);
            CookieUtil.setCookie(request, response, "cartItemCookie", cartJson, 60 * 60 * 72, true);
        }

        return "redirect:/success.html";
    }

    private boolean isExistInCart(List<OmsCartItem> cartItemList, OmsCartItem omsCartItem) {
        boolean bool = false;

        for (OmsCartItem cartItem : cartItemList) {
            if (cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())) {
                bool = true;
                break;
            }
        }
        return bool;
    }
}
