package com.reddish.adoniswebsocket;

import com.reddish.adoniswebsocket.FeignClient.AliveClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Alive {

    @Autowired
    private AliveClient aliveClient;
    @RequestMapping("/alive")
    String alive() {
        return  aliveClient.alive() + " too";
    };
}
