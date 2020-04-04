package com.peter.mall.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan(basePackages = "com.peter.mall")
@SpringBootApplication
public class MallCartServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallCartServiceApplication.class, args);
    }

}
