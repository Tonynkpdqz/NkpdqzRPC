package com.nkpdqz.protocol;

public class RpcResponse {

    private String responseID;
    private String error;
    private Object result;

    public RpcResponse(String responseID, String error, Object result) {
        this.responseID = responseID;
        this.error = error;
        this.result = result;
    }

    @Override
    public String toString() {
        return "RpcResponse{" +
                "responseID='" + responseID + '\'' +
                ", error='" + error + '\'' +
                ", result=" + result +
                '}';
    }

    public RpcResponse() {
    }

    public String getResponseID() {
        return responseID;
    }

    public void setResponseID(String responseID) {
        this.responseID = responseID;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
