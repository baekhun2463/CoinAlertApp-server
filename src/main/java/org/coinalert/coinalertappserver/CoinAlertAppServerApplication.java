package org.coinalert.coinalertappserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
//@EnableFeignClients
public class CoinAlertAppServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoinAlertAppServerApplication.class, args);
    }

}
