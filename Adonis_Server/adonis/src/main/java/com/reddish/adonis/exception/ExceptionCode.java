package com.reddish.adonis.exception;

public enum ExceptionCode {
    /**
     * 编码0表示成功，回复的消息不存在异常
     * 异常表示开发过程中可能出现的错误，以及应由客户端预防而不应在服务端出现的错误
     */
    _100(100, "不存在的消息类型"),
    _101(101, "消息类型与消息内容不符"),
    _102(102, "不存在的子消息MessageCode"),

    _200(200, "不正确的用户操作消息MessageCode"),
    _201(201, "消息内用户ID与session拥有用户ID不一致,可能有破坏者伪装成他人发送消息"),

    _300(300, "不正确的好友操作消息MessageCode"),
    _301(301, "发出好友操作的用户自己已经注销，这是一种极端错误"),
    _302(302, "好友操作涉及双方不能相同"),

    _400(400, "不正确的对话操作消息MessageCode"),
    _401(401, "消息发送与接收有一方不存在，可能已注销"),
    _402(402, "消息接收方和自己不是双向好友"),
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
