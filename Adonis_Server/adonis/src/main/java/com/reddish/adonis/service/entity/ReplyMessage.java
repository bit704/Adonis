package com.reddish.adonis.service.entity;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ReplyMessage {

    /**
     * 此回复消息对应的消息的messageId
     */
    private String messageToReplyId;

    /**
     * 回复代码
     * 回复已发送消息的执行情况
     */
    private int replyCode;
}
