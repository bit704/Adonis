package com.reddish.adoniswebsocket.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "Adonis-Base")
public interface AliveClient {
    @RequestMapping("/alive")
    String alive();
}
