package com.reddish.adonis.service.entity;

public enum MessageCode {
    /**
     * UserOpMessage
     */
    uop_100(100, "登录"),
    uop_101(101, "登出"),
    uop_102(102, "注册"),
    uop_103(103, "注销"),
    uop_104(104, "修改昵称"),
    uop_105(105, "修改密码"),
    uop_106(106, "要求服务端发送UserOnlineMessage。发这个消息的话，其它字段都不用填。"),
    /**
     * UserInfoMessage
     */
    uif_100(100,"用户ID已存在，无法注册"),
    uif_101(101,"用户ID不存在，无法线上操作"),
    uif_102(102,"密码不正确"),
    uif_103(103,"消息中未传入新昵称，无法更新"),
    uif_104(104,"消息中未传入新密码，无法更新"),
    uif_105(105,"注册时未传入完整信息"),
    uif_106(106,"用户不在线，已经断连，需要重新登录"),

    /**
     * FriendOpMessage
     */
    fop_100(100, "添加好友申请（可以选择是否添加申请备注，memo字段）"),
    fop_101(101, "同意好友申请"),
    fop_102(102, "拒绝好友申请"),
    fop_103(103, " 删除好友（只能单向删除，自己还在对方好友列表中，但对方发给自己的消息会被服务器拦截）"),
    fop_104(104, "查询好友是否存在"),
    fop_105(105, "查询好友是否在线"),
    fop_106(106, "拉黑好友"),
    fop_107(107, "自定义对好友的备注名（customNickname字段）"),
    fop_108(108,"查询好友关系是否正常"),

    /**
     * FriendInfoMessage
     * 以下的“此用户”指FriendInfoMessage的id对应的用户
     */
    fif_100(101, "此用户正申请将您加入好友列表"),
    fif_101(100, "此用户已将您加入好友列表"),
    fif_102(102, "此用户已将您拉黑"),
    fif_103(103, "此用户拒绝您的好友申请"),
    fif_104(104, "此用户存在"),
    fif_105(105, "此用户不存在"),
    fif_106(106, "此用户在线"),
    fif_107(107, "此用户不在线"),
    fif_108(108, "此用户是您的单向好友（您没删它，它删了您）"),
    fif_109(109, "此用户已注销"),
    fif_110(110, "已经对此用户发送过同样的好友申请"),
    fif_111(111, "此用户已在您好友列表中，无需申请"),
    fif_112(112, "无法向此用户发送好友申请，已被对方拉黑"),
    fif_113(113,"此用户和您是双向好友"),
    fif_114(114,"此用户和您不是双向好友")
    ;

    private final int id;
    private final String desc;

    MessageCode(int codeId, String desc) {
        this.desc = desc;
        this.id = codeId;
    }

    public String getDesc() {
        return desc;
    }

    public int getId() {
        return id;
    }

    /**
     * 由id获取Code
     */
    public static MessageCode getCodeById(int id) {
        for (MessageCode messageCode : MessageCode.values()) {
            if (messageCode.getId() == id) {
                return messageCode;
            }
        }
        return null;
    }
}
