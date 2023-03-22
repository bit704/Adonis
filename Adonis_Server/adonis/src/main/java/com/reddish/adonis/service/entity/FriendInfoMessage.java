package com.reddish.adonis.service.entity;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FriendInfoMessage {
    /**
     * add: 添加好友申请
     * consent: 同意好友申请
     * refuse: 拒绝好友申请
     * delete: 删除好友
     */
    private String type;
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
