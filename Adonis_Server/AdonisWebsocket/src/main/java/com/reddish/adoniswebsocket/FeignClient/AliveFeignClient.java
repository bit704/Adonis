package com.reddish.adoniswebsocket.FeignClient;

import com.reddish.adoniswebsocket.Client.AliveInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "Adonis-Base", contextId = "alive")
public interface AliveFeignClient {
    @PostMapping("/alive")
    AliveInfo alive();
}
