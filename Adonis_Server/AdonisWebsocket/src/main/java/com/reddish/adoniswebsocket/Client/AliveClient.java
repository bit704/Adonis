package com.reddish.adoniswebsocket.Client;

import com.reddish.adoniswebsocket.FeignClient.AliveFeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class AliveClient {
    private final AliveInfo aliveInfo;
    private final AliveFeignClient aliveFeignClient;

    public AliveClient(AliveInfo aliveInfo, AliveFeignClient aliveFeignClient) {
        this.aliveInfo = aliveInfo;
        this.aliveFeignClient = aliveFeignClient;
    }

    @RequestMapping("/alive")
    List<AliveInfo> alive() {
        return new ArrayList<>(Arrays.asList(aliveFeignClient.alive(), aliveInfo));
    }
}
