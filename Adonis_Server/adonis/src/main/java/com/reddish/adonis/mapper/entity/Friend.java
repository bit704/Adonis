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
     */
    @TableField("status")
    private int status;

    /**
     * 备注内容
     */
    @TableField("memo")
    private String memo;

}
