package com.peter.mall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.peter.mall.annotations.RequiredLoginAnno;
import com.peter.mall.beans.OmsCartItem;
import com.peter.mall.beans.OmsOrderItem;
import com.peter.mall.beans.UmsMemberReceiveAddress;
import com.peter.mall.service.CartService;
import com.peter.mall.service.UmsMemberReceiveAddressService;
import com.peter.mall.service.UmsMemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderController {

    @Reference
    CartService cartService;
    @Reference
    UmsMemberReceiveAddressService umsMemberReceiveAddressService;

    @RequestMapping("submitOrder")
    @RequiredLoginAnno(loginSuccess = true)
    public String submitOrder(String receiveAddressId, BigDecimal totalAmount){

        return "";
    }

    @RequestMapping("toTrade")
    @RequiredLoginAnno(loginSuccess = true)
    public String toTrade(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
        String memberId = (String)request.getAttribute("memberId");
        String nickname = (String)request.getAttribute("nickname");
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressService.getUmsMemberReceiveAddressByMemberId(memberId);

        List<OmsCartItem> omsCartItems = cartService.getOmsCartItemByMemberId(memberId);
        List<OmsOrderItem> omsOrderItems = new ArrayList<>();
        for (OmsCartItem omsCartItem : omsCartItems) {
            if(omsCartItem.getIsChecked().equals("1")){
                OmsOrderItem omsOrderItem = new OmsOrderItem();
                omsOrderItem.setProductName(omsCartItem.getProductName());
                omsOrderItem.setProductPic(omsCartItem.getProductPic());
                omsOrderItem.setProductQuantity(omsCartItem.getQuantity().intValue());
                omsOrderItems.add(omsOrderItem);
            }
        }

        modelMap.put("userAddressList",umsMemberReceiveAddresses);
        modelMap.put("omsOrderItems",omsOrderItems);
        modelMap.put("totalAmount",getTotalAmount(omsCartItems));
        return "trade";
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
}
