package com.nkpdqz.protocol.serializer;

import java.io.IOException;

public interface Serializer {

    byte[] serializer(Object o) throws IOException;

    <T>T deserializer(Class<T> className,byte[] bytes) throws IOException;
}
