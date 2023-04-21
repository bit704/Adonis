package com.reddish.adonis.AO;

public enum MessageCode {
    /**
     * UserOpMessage
     */
    uop_sign_in(100, "登录"),
    uop_sign_out(101, "登出"),
    uop_sign_up(102, "注册"),
    uop_delete(103, "注销"),
    uop_change_nickname(104, "修改昵称"),
    uop_change_password(105, "修改密码"),
    uop_request_online_message(106, "要求服务端发送UserOnlineMessage。发这个消息的话，其它字段都不用填。"),

    /**
     * UserInfoMessage
     */
    uif_op_success(200, "用户操作成功"),
    uif_already_exist(201, "用户ID已存在，无法注册"),
    uif_not_exist(202, "用户ID不存在，无法线上操作"),
    uif_worry_password(203, "密码不正确"),
    uif_no_nickname(204, "消息中未传入新昵称，无法更新"),
    uif_no_password(205, "消息中未传入新密码，无法更新"),
    uif_incomplete_info(206, "注册时未传入完整信息"),
    uif_offline(207, "用户不在线，已经断连，需要重新登录"),
    uif_reply_online_message(208, "回复要求服务端发送UserOnlineMessage"),

    /**
     * FriendOpMessage
     * subjectId是用户自己，例如在fop_consent的情况下，表示subjectId同意objectId发给自己的好友申请
     */
    fop_add(300, "添加好友申请（可以选择是否添加申请备注，memo字段）"),
    fop_consent(301, "同意好友申请"),
    fop_reject(302, "拒绝好友申请"),
    fop_delete(303, "删除好友（只能单向删除，自己还在对方好友列表中，但对方发给自己的消息会被服务器拦截）"),
    fop_query_exist(304, "查询好友是否存在"),
    fop_query_online(305, "查询好友是否在线"),
    fop_block(306, "拉黑好友"),
    fop_custom_nickname(307, "自定义对好友的备注名（customNickname字段）"),
    fop_query_friendship(308, "查询好友关系是否正常"),

    /**
     * FriendInfoMessage
     * 以下的“此用户”指FriendInfoMessage的id对应的用户
     */
    fif_op_success(400, "您对此用户的好友操作成功"),
    fif_add(401, "此用户正申请将您加入好友列表"),
    fif_already_add(402, "此用户已将您加入好友列表"),
    fif_block(403, "此用户已将您拉黑"),
    fif_reject(404, "此用户拒绝您的好友申请"),
    fif_exist(405, "此用户存在"),
    fif_not_exist(406, "此用户不存在,已注销或从未注册过"),
    fif_online(407, "此用户在线"),
    fif_offline(408, "此用户不在线"),
    fif_your_single(409, "此用户是您的单向好友（它在您列表，您不在它列表）"),
    fif_repeat_add(410, "已经对此用户发送过好友申请"),
    fif_two_way(411, "此用户和您是双向好友"),
    fif_not_two_way(412, "此用户和您不是双向好友");

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
