package com.reddish.adonisbase.Client;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AliveClient {

    final AliveInfo aliveInfo;

    public AliveClient(AliveInfo aliveInfo) {
        this.aliveInfo = aliveInfo;
    }

    @GetMapping("/alive")
    public AliveInfo alive(){
        return aliveInfo;
    }
}
