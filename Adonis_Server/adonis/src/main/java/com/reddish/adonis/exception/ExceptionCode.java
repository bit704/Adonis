package com.reddish.adonis.exception;

public enum ExceptionCode {
    /**
     * 代码0表示正常收到消息
     * 异常表示开发过程中可能出现的错误，以及应由客户端预防而不应在服务端出现的错误，因此0之外其它实际中并无用途
     */
    ILLEGAL_TYPE(100, "不存在的消息类型"),
    UNCONFORMITY(101, "消息类型与消息内容不符"),
    ILLEGAL_CODE(102, "不存在的子消息MessageCode"),

    ILLEGAL_UOP_CODE(200, "不正确的用户操作消息MessageCode"),
    HACK(201, "消息内用户ID与session拥有用户ID不一致,可能有破坏者伪装成他人发送消息"),

    ILLEGAL_FOP_CODE(300, "不正确的好友操作消息MessageCode"),
    EXTREME_ERROR(301, "发出好友操作的用户自己已经注销，这是一种极端错误"),
    NOT_SELF(302, "好友操作涉及双方不能相同"),

    SHADOW_MAN(401, "消息发送与接收有一方不存在，可能已注销"),
    STRANGER(402, "消息接收方和自己不是双向好友就发送了消息"),
    ;

    private final int id;
    private final String desc;

    ExceptionCode(int id, String desc) {
        this.id = id;
        this.desc = desc;
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
    public static ExceptionCode getCodeById(int id) {
        for (ExceptionCode exceptionCode : ExceptionCode.values()) {
            if (exceptionCode.getId() == id) {
                return exceptionCode;
            }
        }
        return null;
    }
}
