package com.example.adonis.entity;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FriendInfoMessage {
    /**
     * 请求方账号
     */
    private String subjectId;
    /**
     * 被请求方账号
     */
    private String objectId;
    /**
     * 备注内容
     */
    private String memo;
}
