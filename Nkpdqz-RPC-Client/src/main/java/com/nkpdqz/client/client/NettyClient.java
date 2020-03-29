package com.nkpdqz.client.client;

import com.nkpdqz.protocol.RPCDecoder;
import com.nkpdqz.protocol.RpcEncoder;
import com.nkpdqz.protocol.RpcRequest;
import com.nkpdqz.protocol.RpcResponse;
import com.nkpdqz.protocol.serializer.JSONSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

public class NettyClient {

    private EventLoopGroup eventLoopGroup;
    private Channel channel;
    private ClientHandler clientHandler;
    private String host;
    private Integer port;
    private static final int MAX_RETRY = 5;

    public NettyClient(String host,Integer port){
        this.host = host;
        this.port = port;
    }

    public void connect() {
        clientHandler = new ClientHandler();
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .option(ChannelOption.TCP_NODELAY,true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(65535,0,4));
                        pipeline.addLast(new RpcEncoder(RpcRequest.class,new JSONSerializer()));
                        pipeline.addLast(new RPCDecoder(RpcResponse.class,new JSONSerializer()));
                        pipeline.addLast(clientHandler);
                    }
                });
        connect(bootstrap,host,port,MAX_RETRY);
    }

    private void connect(Bootstrap bootstrap, String host, Integer port, int retry) {
        ChannelFuture myFuture = bootstrap.connect(host, port);
        myFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if (future.isSuccess()){
                    System.out.println("连接服务器成功");
                }else if (retry == 0) {
                    System.out.println("重试多次仍失败，放弃连接");
                } else {
                    //第order次重连
                    int order = (MAX_RETRY - retry) + 1;
                    int delay = 1 << order;
                    System.out.printf("重连失败，第%d次重连", order);
                    bootstrap.config().group().schedule(() -> connect(bootstrap, host, port, retry - 1), delay, TimeUnit.SECONDS);
                }
            }
        });
        while (!myFuture.isSuccess()) {
        }
        channel = myFuture.channel();
        /*ChannelFuture channelFuture = bootstrap.connect(host, port).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("连接服务器成功");
                } else if (retry == 0) {
                    System.out.println("重试多次仍失败，放弃连接");
                } else {
                    //第order次重连
                    int order = (MAX_RETRY - retry) + 1;
                    int delay = 1 << order;
                    System.out.printf("重连失败，第%d次重连", order);
                    bootstrap.config().group().schedule(() -> connect(bootstrap, host, port, retry - 1), delay, TimeUnit.SECONDS);
                }
            }
        });
        channel = channelFuture.channel();*/
    }

    public RpcResponse send(final RpcRequest request){
        try {
            channel.writeAndFlush(request).await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return clientHandler.getRpcResponse(request.getRequestId());
    }

    @PreDestroy
    public void close(){
        eventLoopGroup.shutdownGracefully();
        channel.closeFuture().syncUninterruptibly();
    }
}
