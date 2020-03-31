package com.peter.mall.manage;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MallManageWebApplicationTests {

    @Test
    void contextLoads() {
        String[] retMsg = new String[]{"aaa", "bbb"};
        StringBuilder sb = new StringBuilder(80);
        for (int i = 0; i < retMsg.length; i++) {
            sb.append("/");
            sb.append(retMsg[i]);
        }
        String url = "192.168.125.100:22122";
        url += sb.toString();
        System.out.println(url);
    }

}
