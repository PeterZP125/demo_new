package com.peter.mall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.peter.mall.annotations.RequiredLoginAnno;
import com.peter.mall.beans.OmsCartItem;
import com.peter.mall.beans.OmsOrder;
import com.peter.mall.beans.OmsOrderItem;
import com.peter.mall.beans.UmsMemberReceiveAddress;
import com.peter.mall.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
public class OrderController {

    @Reference
    CartService cartService;
    @Reference
    UmsMemberReceiveAddressService umsMemberReceiveAddressService;
    @Reference
    OrderService orderService;
    @Reference
    SkuService skuService;


    @RequestMapping("submitOrder")
    @RequiredLoginAnno(loginSuccess = true)
    public ModelAndView submitOrder(HttpServletRequest request, String receiveAddressId, BigDecimal totalAmount, String tradeCode) {
        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");
        String checkRet = orderService.checkTradeCode(memberId, tradeCode);

        //外部订单号
        String outTradeNo = "mall";
        outTradeNo = outTradeNo + System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYMMDDHHmmss");
        outTradeNo = outTradeNo + simpleDateFormat.format(new Date());

        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setAutoConfirmDay(3);
        omsOrder.setCreateTime(new Date());
        omsOrder.setMemberId(memberId);
        omsOrder.setMemberUsername(nickname);
        omsOrder.setOrderSn(outTradeNo);
        omsOrder.setOrderType(0);
        omsOrder.setTotalAmount(totalAmount);
        omsOrder.setStatus(0);
        omsOrder.setSourceType(0);

        UmsMemberReceiveAddress umsMemberReceiveAddress = umsMemberReceiveAddressService.getReceiveAddressById(receiveAddressId);

        omsOrder.setReceiverCity(umsMemberReceiveAddress.getCity());
        omsOrder.setReceiverDetailAddress(umsMemberReceiveAddress.getDetailAddress());
        omsOrder.setReceiverName(umsMemberReceiveAddress.getName());
        omsOrder.setReceiverPhone(umsMemberReceiveAddress.getPhoneNumber());
        omsOrder.setReceiverPostCode(umsMemberReceiveAddress.getPostCode());
        omsOrder.setReceiverProvince(umsMemberReceiveAddress.getProvince());
        omsOrder.setReceiverRegion(umsMemberReceiveAddress.getRegion());

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 1);
        Date time = c.getTime();
        omsOrder.setReceiveTime(time);


        List<OmsOrderItem> omsOrderItemList = new ArrayList<>();

        if ("success".equals(checkRet)) {
            //获取用户需要购买的商品
            List<OmsCartItem> omsCartItems = cartService.getOmsCartItemByMemberId(memberId);
            for (OmsCartItem omsCartItem : omsCartItems) {
                if (omsCartItem.getIsChecked().equals("1")) {
                    OmsOrderItem omsOrderItem = new OmsOrderItem();
                    //验价
                    boolean ifEqual = skuService.checkPrice(omsCartItem.getProductSkuId(), omsCartItem.getPrice());
                    //验库存(远程调用库存系统)
                    if (!ifEqual) {
                        ModelAndView mv = new ModelAndView("tradeFail");
                        return mv;
                    }
                    omsOrderItem.setProductName(omsCartItem.getProductName());
                    omsOrderItem.setProductPic(omsCartItem.getProductPic());
                    omsOrderItem.setProductCategoryId(omsCartItem.getProductCategoryId());
                    omsOrderItem.setProductPrice(omsCartItem.getPrice());
                    omsOrderItem.setProductQuantity(omsCartItem.getQuantity());
                    omsOrderItem.setProductId(omsCartItem.getProductId());
                    omsOrderItem.setProductSn("商品在仓库中的编号");
                    omsOrderItem.setOrderSn(outTradeNo);

                    //外部订单号
                    omsOrderItem.setProductSn(outTradeNo);
                    omsOrderItem.setRealAmount(null);
                    omsOrderItemList.add(omsOrderItem);
                }
            }
            omsOrder.setOmsOrderItemList(omsOrderItemList);
            orderService.saveOrder(omsOrder);
            ModelAndView mv = new ModelAndView("redirect:http://payment.mall.com:8080/index");
            mv.addObject("outTradeNo",outTradeNo);
            mv.addObject("totalAmount",totalAmount);
            return mv;
        } else {
            ModelAndView mv = new ModelAndView("tradeFail");
            return mv;
        }
    }


    @RequestMapping("toTrade")
    @RequiredLoginAnno(loginSuccess = true)
    public String toTrade(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
        String memberId = (String) request.getAttribute("memberId");
        String nickname = (String) request.getAttribute("nickname");
        List<UmsMemberReceiveAddress> umsMemberReceiveAddresses = umsMemberReceiveAddressService.getUmsMemberReceiveAddressByMemberId(memberId);

        List<OmsCartItem> omsCartItems = cartService.getOmsCartItemByMemberId(memberId);
        List<OmsOrderItem> omsOrderItems = new ArrayList<>();
        for (OmsCartItem omsCartItem : omsCartItems) {
            if (omsCartItem.getIsChecked().equals("1")) {
                OmsOrderItem omsOrderItem = new OmsOrderItem();
                omsOrderItem.setProductName(omsCartItem.getProductName());
                omsOrderItem.setProductPic(omsCartItem.getProductPic());
                omsOrderItem.setProductQuantity(omsCartItem.getQuantity());
                omsOrderItems.add(omsOrderItem);
            }
        }

        String tradeCode = orderService.generateTradeCode(memberId);

        modelMap.put("tradeCode", tradeCode);
        modelMap.put("userAddressList", umsMemberReceiveAddresses);
        modelMap.put("omsOrderItems", omsOrderItems);
        modelMap.put("totalAmount", getTotalAmount(omsCartItems));
        return "trade";
    }

    private BigDecimal getTotalAmount(List<OmsCartItem> omsCartItemList) {
        BigDecimal bigDecimal = new BigDecimal("0");

        for (OmsCartItem omsCartItem : omsCartItemList) {
            if (omsCartItem.getIsChecked().equals("1")) {
                bigDecimal = bigDecimal.add(omsCartItem.getPrice().multiply(omsCartItem.getQuantity()));
            }
        }
        return bigDecimal;
    }
}
