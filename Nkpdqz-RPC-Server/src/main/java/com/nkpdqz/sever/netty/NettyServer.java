package com.nkpdqz.sever.netty;

import com.nkpdqz.protocol.RPCDecoder;
import com.nkpdqz.protocol.RpcEncoder;
import com.nkpdqz.protocol.RpcRequest;
import com.nkpdqz.protocol.RpcResponse;
import com.nkpdqz.protocol.serializer.JSONSerializer;
import com.nkpdqz.sever.handler.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class NettyServer implements InitializingBean {

    private EventLoopGroup boss = null;
    private EventLoopGroup worker = null;
    @Autowired
    private ServerHandler serverHandler;


    @Override
    public void afterPropertiesSet() throws Exception {
        //加注册中心
    }

    public void start() {
        //负责处理客户端连接
        boss = new NioEventLoopGroup();
        //负责处理io事件
        worker = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new RpcEncoder(RpcResponse.class, new JSONSerializer()));
                        pipeline.addLast(new RPCDecoder(RpcRequest.class, new JSONSerializer()));
                        pipeline.addLast(serverHandler);
                    }
                });
        bind(serverBootstrap, 8888);
    }

    public void bind(ServerBootstrap serverBootstrap,int port){
        serverBootstrap.bind(port).addListener(future -> {
            if (future.isSuccess()){
                System.out.println("端口绑定成功");
            } else {
                System.out.println("端口绑定失败");
                bind(serverBootstrap,port+1);
            }
        });
    }

    @PreDestroy
    public void destory() throws InterruptedException {
        boss.shutdownGracefully().sync();
        worker.shutdownGracefully().sync();
        System.out.println("关闭服务器");
    }
}
