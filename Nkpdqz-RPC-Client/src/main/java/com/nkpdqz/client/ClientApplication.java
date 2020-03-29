package com.nkpdqz.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.nkpdqz.client.proxy.ProxyFactory;
import com.nkpdqz.service.HelloService;

@SpringBootApplication
public class ClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class,args);
        HelloService service = ProxyFactory.create(HelloService.class);
        System.out.println("响应结果:"+ service.Hello(new String("nkpdqz")));
    }
}
