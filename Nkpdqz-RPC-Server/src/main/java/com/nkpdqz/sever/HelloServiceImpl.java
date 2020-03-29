package com.nkpdqz.sever;

import com.nkpdqz.service.HelloService;
import org.springframework.stereotype.Service;

@Service
public class HelloServiceImpl implements HelloService {
    @Override
    public String Hello(String name) {
        return name+",你好";
    }
}
