package com.reddish.adoniswebsocket.DTO;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Dialogue {

    private String senderId;

    private String receiverId;

    private String content;

    private long occurredTime;

    private long lastedTime;
}