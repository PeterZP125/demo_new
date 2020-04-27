package com.peter.mall.payment.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.peter.mall.beans.PaymentInfo;
import com.peter.mall.payment.mapper.PaymentInfoMapper;
import com.peter.mall.service.PaymentService;
import com.peter.mall.util.ActiveMQUtil;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentInfoMapper paymentInfoMapper;
    @Autowired
    ActiveMQUtil activeMQUtil;

    @Override
    public void save(PaymentInfo paymentInfo) {
        paymentInfoMapper.insertSelective(paymentInfo);
    }

    @Override
    public void update(PaymentInfo paymentInfo) {
        Example e = new Example(PaymentInfo.class);
        e.createCriteria().andEqualTo("orderSn", paymentInfo.getOrderSn());

        ConnectionFactory connectionFactory = activeMQUtil.getConnectionFactory();
        Connection connection = null;
        Session session = null;
        //支付成功后，引起的系统服务-》订单服务的更新-》库存服务-》物流服务
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue queue = session.createQueue("PAYMENT_SUCCESS_QUEUE");
            MessageProducer producer = session.createProducer(queue);
            MapMessage mapMessage = new ActiveMQMapMessage();
            mapMessage.setString("out_trade_no", paymentInfo.getOrderSn());
            paymentInfoMapper.updateByExample(paymentInfo, e);
            producer.send(mapMessage);
        } catch (Exception ex) {
            try {
                session.rollback();
            } catch (JMSException ex1) {
                ex1.printStackTrace();
            }
        } finally {
            try {
                session.close();
                connection.close();
            } catch (JMSException ex) {
                ex.printStackTrace();
            }
        }
    }
}
