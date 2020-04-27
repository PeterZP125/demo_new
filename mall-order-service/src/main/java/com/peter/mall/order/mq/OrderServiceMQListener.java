package com.peter.mall.order.mq;

import com.peter.mall.config.ActiveMQConfig;
import com.peter.mall.service.OrderService;
import com.peter.mall.util.ActiveMQUtil;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.*;

@Component
public class OrderServiceMQListener {

    @Autowired
    OrderService orderService;

    @Autowired
    ActiveMQUtil activeMQUtil;

    @JmsListener(destination="PAYMENT_SUCCESS_QUEUE",containerFactory = "jmsQueueListener")
    public void consumePaymentMessage(MapMessage mapMessage1) throws JMSException {
        String out_trade_no = mapMessage1.getString("out_trade_no");


        ConnectionFactory connectionFactory = activeMQUtil.getConnectionFactory();
        Connection connection = null;
        Session session = null;
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue queue = session.createQueue("ORDER_PAY_QUEUE");
            MessageProducer producer = session.createProducer(queue);
            MapMessage mapMessage = new ActiveMQMapMessage();
//            mapMessage.setString("out_trade_no", paymentInfo.getOrderSn());
            //更新订单状态
            orderService.updateByOutTradeNo(out_trade_no);
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
