package org.coinalert.coinalertappserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
public class CoinAlertAppServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoinAlertAppServerApplication.class, args);
    }

}
