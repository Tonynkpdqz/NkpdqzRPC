package com.nkpdqz.client.future;

import com.nkpdqz.protocol.RpcResponse;

public class DefaultFuture {

    private RpcResponse rpcResponse;
    private volatile boolean isSucceed = false;
    private final Object object = new Object();

    public RpcResponse getRpcResponse(int timeout){
        synchronized (object) {
            while (!isSucceed) {
                try {
                    object.wait(timeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return rpcResponse;
        }
    }

    public void setRpcResponse(RpcResponse response){
        //System.out.println(response.toString());
        if (isSucceed)
            return;
        synchronized (object){
            this.rpcResponse = response;
            this.isSucceed = true;
            object.notify();
        }
    }
}
