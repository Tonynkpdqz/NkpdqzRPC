package com.nkpdqz.protocol.serializer;

import com.alibaba.fastjson.JSON;

import java.io.IOException;

public class JSONSerializer implements Serializer {
    @Override
    public byte[] serializer(Object o) {
        return JSON.toJSONBytes(o);
    }

    @Override
    public <T> T deserializer(Class<T> className, byte[] bytes) throws IOException {
        return JSON.parseObject(bytes,className);
    }
}
