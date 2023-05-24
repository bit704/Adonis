package com.reddish.adonisbase.Client;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AliveClient {

    final AliveInfo aliveInfo;

    public AliveClient(AliveInfo aliveInfo) {
        this.aliveInfo = aliveInfo;
    }

    @PostMapping("/alive")
    public AliveInfo alive(){
        return aliveInfo;
    }
}
