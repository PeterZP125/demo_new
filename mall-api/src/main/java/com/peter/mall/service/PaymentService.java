package com.peter.mall.service;

import com.peter.mall.beans.PaymentInfo;

public interface PaymentService {
    void save(PaymentInfo paymentInfo);

    void update(PaymentInfo paymentInfo);
}
