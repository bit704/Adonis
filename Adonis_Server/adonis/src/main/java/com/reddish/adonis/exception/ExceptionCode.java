package com.reddish.adonis.exception;

public enum ExceptionCode {
    _100("不合法的消息",100),
    _101("不存在的消息类型", 101),
    _102("消息类型与消息内容不符", 102),
    _200("不存在的用户消息类型",200),
    _201("用户ID已存在，无法注册", 201),
    _202("用户ID不存在，无法线上操作", 202),
    _203("密码不正确",203),
    _204("消息内用户ID与session拥有用户ID不一致",204),
    _205("消息中未传入新昵称，无法更新", 205),
    _206("消息中未传入新密码，无法更新", 206),
    _207("注册时未传入完整信息",207),
    _208("用户不在线",208)
    ;

    private final String desc;
    private final int codeId;

    ExceptionCode(String desc, int codeId) {
        this.desc = desc;
        this.codeId = codeId;
    }

    public String getDesc() {
        return desc;
    }

    public int getCodeId() {
        return codeId;
    }
}
