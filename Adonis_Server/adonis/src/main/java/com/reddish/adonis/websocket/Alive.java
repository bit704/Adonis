package com.reddish.adonis.websocket;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Alive {
    @RequestMapping("/alive")
    public String alive(){
        return "alive";
    }
}
