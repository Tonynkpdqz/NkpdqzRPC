package com.nkpdqz.client.client;

import com.nkpdqz.protocol.RpcRequest;
import com.nkpdqz.protocol.RpcResponse;
import com.nkpdqz.client.future.DefaultFuture;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHandler extends ChannelDuplexHandler {

    private final Map<String, DefaultFuture> futureMap = new ConcurrentHashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RpcResponse){
            System.out.println("reading");
            RpcResponse response = (RpcResponse) msg;
            DefaultFuture defaultFuture = futureMap.get(response.getResponseID());
            defaultFuture.setRpcResponse(response);
        }
        super.channelRead(ctx,msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof RpcRequest){
            System.out.println("writing");
            RpcRequest request = (RpcRequest) msg;
            futureMap.putIfAbsent(request.getRequestId(),new DefaultFuture());
        }
        super.write(ctx, msg, promise);
    }

    public RpcResponse getRpcResponse(String requestId) {
        try {
            DefaultFuture defaultFuture = futureMap.get(requestId);
            System.out.println(defaultFuture.toString());
            return defaultFuture.getRpcResponse(10);
        } finally {
            futureMap.remove(requestId);
        }
    }
}
