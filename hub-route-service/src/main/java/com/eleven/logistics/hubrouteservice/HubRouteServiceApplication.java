package com.eleven.logistics.hubrouteservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class HubRouteServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HubRouteServiceApplication.class, args);
    }

}
