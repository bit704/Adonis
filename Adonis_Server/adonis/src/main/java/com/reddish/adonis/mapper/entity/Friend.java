package com.reddish.adonis.mapper.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@TableName("friend")
public class Friend {
    @TableField("subjectId")
    private String subjectId;

    @TableField("objectId")
    private String objectId;

    /**
     * 0: s申请将o加入好友列表
     * 1: s已经将o加入好友列表
     * 2: s已经将o拉黑
     * 3: s发给o的好友申请被拒绝
     */
    @TableField("status")
    private int status;

    /**
     * s对o的自定义备注名
     */
    @TableField("customname")
    private String customname;

    /**
     * 申请备注
     */
    @TableField("memo")
    private String memo;

}
