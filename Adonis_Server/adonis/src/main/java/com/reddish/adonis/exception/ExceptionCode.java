package com.reddish.adonis.exception;

public enum ExceptionCode {
    _100("不合法的消息", 100),
    _101("不存在的消息类型", 101),
    _102("消息类型与消息内容不符", 102),

    _200("不存在的用户消息类型", 200),
    _201("用户ID已存在，无法注册", 201),
    _202("用户ID不存在，无法线上操作", 202),
    _203("密码不正确", 203),
    _204("消息内用户ID与session拥有用户ID不一致", 204),
    _205("消息中未传入新昵称，无法更新", 205),
    _206("消息中未传入新密码，无法更新", 206),
    _207("注册时未传入完整信息", 207),
    _208("用户不在线", 208),

    _300("不存在的好友消息类型", 300),
    _301("好友操作有一方不存在，可能已注销", 301),
    _302("好友双方不能相同", 302),
    _303("已经发送过同样的好友申请", 303),
    _304("已在好友列表中，无需申请", 304),

    _400("不存在的对话消息类型", 400),
    _401("消息发送与接收有一方不存在，可能已注销", 401),
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

    /**
     * 由codeId获取Code
     *
     * @param codeId
     * @return
     */
    public static ExceptionCode getCodeByCodeId(int codeId) {
        for (ExceptionCode exceptionCode : ExceptionCode.values()) {
            if (exceptionCode.getCodeId() == codeId) {
                return exceptionCode;
            }
        }
        return null;
    }
}
