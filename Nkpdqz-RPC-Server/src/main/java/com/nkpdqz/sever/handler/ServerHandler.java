package com.nkpdqz.sever.handler;

import com.nkpdqz.protocol.RpcRequest;
import com.nkpdqz.protocol.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.BeansException;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

@Component
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<RpcRequest> implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest request) throws Exception {
        RpcResponse response = new RpcResponse();
        response.setResponseID(request.getRequestId());
        try{
            Object handler = handler(request);
            System.out.println(handler);
            response.setResult(handler);
        }catch (Throwable throwable){
            response.setError(throwable.toString());
            throwable.printStackTrace();
        }
        channelHandlerContext.writeAndFlush(response);
    }


    private Object handler(RpcRequest request) throws ClassNotFoundException, InvocationTargetException {
        Class<?> clazz = Class.forName(request.getClassName());
        Object serviceBean = applicationContext.getBean(clazz);
        System.out.println("serviceBean: "+ serviceBean);
        Class<?> serviceBeanClass = serviceBean.getClass();
        System.out.println("serverCLass: "+ serviceBeanClass);
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();
        //CGLIB动态代理
        FastClass fastClass = FastClass.create(serviceBeanClass);
        FastMethod method = fastClass.getMethod(methodName, parameterTypes);
        System.out.println("开始远程调用方法");
        return method.invoke(serviceBean,parameters);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
