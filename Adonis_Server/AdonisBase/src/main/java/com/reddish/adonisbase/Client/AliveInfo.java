package com.reddish.adonisbase.Client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class AliveInfo implements Serializable {
    @Value("${spring.application.name}")
    private String applicationName;

    public String getInfo() {
        return applicationName + " is alive.";
    }
}
