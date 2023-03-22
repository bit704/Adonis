package com.example.adonis.entity;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DialogueInfoMessage {
    /**
     * 发送方账号
     */
    private String senderId;
    /**
     * 接收方账号
     */
    private String receiverId;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 消息存在时间
     */
    private double lastedTime;
}
