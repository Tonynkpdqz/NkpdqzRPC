package com.nkpdqz.client.proxy;

import com.nkpdqz.client.client.NettyClient;
import com.nkpdqz.protocol.RpcRequest;
import com.nkpdqz.protocol.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.UUID;

public class RpcClientDynamicProxy<T> implements InvocationHandler {

    private Class<T> clazz;

    public RpcClientDynamicProxy(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        String requestID = UUID.randomUUID().toString();
        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        request.setClassName(className);
        request.setMethodName(methodName);
        request.setParameterTypes(parameterTypes);
        request.setRequestId(requestID);
        request.setParameters(args);
        System.out.println("请求内容: "+request.toString());
        NettyClient client = new NettyClient("127.0.0.1",8888);
        System.out.println("开始连接服务端: "+new Date());
        client.connect();
        //Thread.sleep(10000);
        RpcResponse response = client.send(request);
        System.out.println("返回结果： "+response.getResult().toString());
        return response.getResult();
    }
}
