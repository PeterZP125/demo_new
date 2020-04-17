package com.peter.mall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.peter.mall.annotations.RequiredLoginAnno;
import com.peter.mall.beans.OmsCartItem;
import com.peter.mall.beans.PmsSkuInfo;
import com.peter.mall.service.CartService;
import com.peter.mall.service.SkuService;
import com.peter.mall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
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

    @RequestMapping("checkCart")
    @RequiredLoginAnno(loginSuccess = false)
    public String checkCart(String skuId, String isChecked, HttpServletRequest request, ModelMap modelMap){
        //登陆用户
        String memberId = (String)request.getAttribute("memberId");
        String nickName = (String)request.getAttribute("nickname");

        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setIsChecked(isChecked);
        omsCartItem.setMemberId(memberId);
        cartService.checkCart(omsCartItem);

        List<OmsCartItem> omsCartItemList = cartService.getOmsCartItemByMemberId(memberId);
        BigDecimal totalAmount = getTotalAmount(omsCartItemList);
        for (OmsCartItem cartItem : omsCartItemList) {
            cartItem.setTotalPrice(cartItem.getQuantity().multiply(cartItem.getPrice()));
        }
        modelMap.put("cartList",omsCartItemList);
        modelMap.put("totalAmount", totalAmount);
        return "cartListInner";
    }

    private BigDecimal getTotalAmount(List<OmsCartItem> omsCartItemList) {
        BigDecimal bigDecimal =new BigDecimal("0");

        for (OmsCartItem omsCartItem : omsCartItemList) {
            if(omsCartItem.getIsChecked().equals("1")){
                bigDecimal = bigDecimal.add(omsCartItem.getPrice().multiply(omsCartItem.getQuantity()));
            }
        }
        return bigDecimal;
    }

    @RequestMapping("cartList")
    @RequiredLoginAnno(loginSuccess = false)
    public String cartList(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
        List<OmsCartItem> cartItemList = new ArrayList<>();

        //查看用户是否登录
        String memberId = (String)request.getAttribute("memberId");
        String nickName = (String)request.getAttribute("nickname");
        if (StringUtils.isNotBlank(memberId)) {
            cartItemList = cartService.getOmsCartItemByMemberId(memberId);
        } else {
            //用户没有登录，查询Cookie中的购物车
            String cartCookie = CookieUtil.getCookieValue(request, "cartItemCookie", true);
            if (StringUtils.isNotBlank(cartCookie)) {
                cartItemList = JSON.parseArray(cartCookie, OmsCartItem.class);
            }
        }

        for (OmsCartItem omsCartItem : cartItemList) {
            omsCartItem.setTotalPrice(omsCartItem.getQuantity().multiply(omsCartItem.getPrice()));
        }
        BigDecimal totalAmount = getTotalAmount(cartItemList);

        modelMap.put("totalAmount", totalAmount);
        modelMap.put("cartList", cartItemList);
        return "cartList";
    }

    @RequestMapping("addToCart")
    @RequiredLoginAnno(loginSuccess = false)
    public String addToCart(String skuId, Integer quantity, HttpServletRequest request, HttpServletResponse response) {
        PmsSkuInfo pmsSkuInfo = skuService.getPmsSkuInfoById(skuId);
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setDeleteStatus(0);
        omsCartItem.setPrice(pmsSkuInfo.getPrice());
        omsCartItem.setProductCategoryId(pmsSkuInfo.getCatalog3Id());
        omsCartItem.setProductPic(pmsSkuInfo.getSkuDefaultImg());
        omsCartItem.setProductName(pmsSkuInfo.getSkuName());
        omsCartItem.setQuantity(new BigDecimal(quantity));
        omsCartItem.setProductId(pmsSkuInfo.getProductId());
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setModifyDate(new Date());
        omsCartItem.setIsChecked("1");

        //放入Cookie中的购物车
        List<OmsCartItem> omsCartItemList = new ArrayList<>();
        //查看用户是否登录
        String memberId = (String)request.getAttribute("memberId");
        String nickName = (String)request.getAttribute("nickname");
        if (StringUtils.isNotBlank(memberId)) {
            //查看用户是否添加过该商品
            OmsCartItem cartItemFromDb = cartService.ifExistByMember(memberId, skuId);
            if (cartItemFromDb == null) {
                //如果用户没有添加过该商品
                omsCartItem.setMemberId(memberId);
                cartService.addToCart(omsCartItem);
            } else {
                cartItemFromDb.setQuantity(cartItemFromDb.getQuantity().add(omsCartItem.getQuantity()));
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
                if (isExistInCart(omsCartItemList, omsCartItem)) {
                    for (OmsCartItem cartItem : omsCartItemList) {
                        if (cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())) {
                            cartItem.setQuantity(cartItem.getQuantity().add(omsCartItem.getQuantity()));
                        }
                    }
                } else {
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
