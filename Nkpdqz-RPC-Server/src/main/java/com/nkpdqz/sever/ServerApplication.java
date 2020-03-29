package com.nkpdqz.sever;

import com.nkpdqz.sever.netty.NettyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ServerApplication {


    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ServerApplication.class, args);
        NettyServer server = context.getBean(NettyServer.class);
        //NettyServer server = new NettyServer();
        server.start();
    }
}
