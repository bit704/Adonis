package com.reddish.adoniswebsocket.DTO;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Friend {
    private String subjectId;

    private String objectId;

    /**
     * 0: s申请将o加入好友列表
     * 1: s已经将o加入好友列表
     * 2: s已经将o拉黑
     * 3: s发给o的好友申请被拒绝
     */
    private int status;

    /**
     * s对o的自定义备注名
     */
    private String customNickname;

    /**
     * 申请备注
     */
    private String memo;

}
