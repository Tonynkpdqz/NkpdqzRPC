package com.nkpdqz.client.proxy;

import java.lang.reflect.Proxy;

public class ProxyFactory {

    public static <T> T create(Class<T> interfaceClass){
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),new Class<?>[]{interfaceClass},new RpcClientDynamicProxy<>(interfaceClass));
    }
}
