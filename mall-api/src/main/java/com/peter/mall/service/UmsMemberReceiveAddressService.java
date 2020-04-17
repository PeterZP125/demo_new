package com.peter.mall.service;

import com.peter.mall.beans.UmsMemberReceiveAddress;

import java.util.List;

public interface UmsMemberReceiveAddressService {
    List<UmsMemberReceiveAddress> getUmsMemberReceiveAddressByMemberId(String memberId);
}
