package com.reddish.adonis.Message;

public enum MessageCode {
    /**
     * UserOpMessage
     */
    UOP_SIGN_IN(100, "登录"),
    UOP_SIGN_OUT(101, "登出"),
    UOP_SIGN_UP(102, "注册"),
    UOP_DELETE(103, "注销"),
    UOP_CHANGE_NICKNAME(104, "修改昵称"),
    UOP_CHANGE_PASSWORD(105, "修改密码"),
    UOP_REQUEST_ONLINE_MESSAGE(106, "要求服务端发送UserOnlineMessage。发这个消息的话，其它字段都不用填。"),

    /**
     * UserInfoMessage
     */
    UIF_OP_SUCCESS(200, "用户操作成功"),
    UIF_ALREADY_EXIST(201, "用户ID已存在，无法注册"),
    UIF_NOT_EXIST(202, "用户ID不存在，无法线上操作"),
    UIF_WORRY_PASSWORD(203, "密码不正确"),
    UIF_NO_NICKNAME(204, "消息中未传入新昵称，无法更新"),
    UIF_NO_PASSWORD(205, "消息中未传入新密码，无法更新"),
    UIF_INCOMPLETE_INFO(206, "注册时未传入完整信息"),
    UIF_OFFLINE(207, "用户不在线，已经断连，需要重新登录"),
    UIF_REPLY_ONLINE_MESSAGE(208, "回复要求服务端发送UserOnlineMessage"),

    /**
     * FriendOpMessage
     * subjectId是用户自己，例如在fop_consent的情况下，表示subjectId同意objectId发给自己的好友申请
     */
    FOP_ADD(300, "添加好友申请（可以选择是否添加申请备注，memo字段）"),
    FOP_CONSENT(301, "同意好友申请"),
    FOP_REJECT(302, "拒绝好友申请"),
    FOP_DELETE(303, "删除好友（只能单向删除，自己还在对方好友列表中，但对方发给自己的消息会被服务器拦截）"),
    FOP_QUERY_EXIST(304, "查询好友是否存在"),
    FOP_QUERY_ONLINE(305, "查询好友是否在线"),
    FOP_BLOCK(306, "拉黑好友"),
    FOP_CUSTOM_NICKNAME(307, "自定义对好友的备注名（customNickname字段）"),
    FOP_QUERY_FRIENDSHIP(308, "查询好友关系是否正常"),

    /**
     * FriendInfoMessage
     * 以下的“此用户”指FriendInfoMessage的id对应的用户
     */
    FIF_OP_SUCCESS(400, "您对此用户的好友操作成功"),
    FIF_ADD_YOU(401, "此用户正申请将您加入好友列表"),
    FIF_ADD_TO(402, "您正申请将此用户加入好友列表"),
    FIF_ADD_CONSENT(403, "此用户已同意将您加入好友列表"),
    FIF_BLOCK(404, "此用户已将您拉黑"),
    FIF_REJECT(405, "此用户拒绝您的好友申请"),
    FIF_EXIST(406, "此用户存在"),
    FIF_NOT_EXIST(407, "此用户不存在,已注销或从未注册过"),
    FIF_ONLINE(408, "此用户在线"),
    FIF_OFFLINE(409, "此用户不在线"),
    FIF_SINGLE_FOR_YOU(410, "此用户是您的单向好友（您列表有它，它列表没您）"),
    FIF_SINGLE_ON_YOU(411, "此用户是您的单向好友（您列表没它，它列表有您）"),
    FIF_TWO_WAY(412, "此用户和您是双向好友"),
    FIF_FREE(413, "此用户和您没有任何好友关系");

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
