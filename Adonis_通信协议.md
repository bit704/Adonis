# 1 概述

所有消息均为`Message`类的对象，使用[alibaba/fastjson2](https://github.com/alibaba/fastjson2)序列化为字符串发送。

`Message`类包含`UserInfoMessage`、`FriendInfoMessage`、`DialogueInfoMessage`三个类的对象。

只需要填充所需字段，其它字段设为`null`。

# 2 消息结构

## 2.1 Message

```java
public class Message {
    /**
     * 表示此消息是不是对已发送消息的回复
     * 如果是，messageId表示此消息对应的已发送消息的id,除了isReply、replyCode、id的其它字段都应为null
     */
    private boolean isReply;
    /**
     * 回复代码
     * 回复已发送消息的执行情况
     */
    private int replyCode;

    /**
     * 唯一id
     * 使用UUID实现
     */
    private String messageId;

    /**
     * 消息类型
     * userMessage 用户操作相关消息
     * friendInfoMessage 好友操作相关消息
     * dialogueMessage 对话相关消息
     */
    private String type;
    
    private UserInfoMessage userInfoMessage;
    private FriendInfoMessage friendInfoMessage;
    private DialogueInfoMessage dialogueInfoMessage;
}
```

## 2.2 UserInfoMessage

```java
public class UserInfoMessage {
    /**
     * sign_in: 登录
     * sign_out: 登出
     * sign_up: 注册
     * delete: 注销
     * change_nickname: 修改昵称
     * change_password: 修改密码
     */
    private String type;
    /**
     * 账号
     * 长度20以内
     */
    private String id;
    /**
     * 昵称
     * 长度20以内
     */
    private String nickname;
    /**
     * 密码
     * 长度20以内
     */
    private String password;
}
```

## 2.3 FriendInfoMessage

```java
public class FriendInfoMessage {
    /**
     * 请求方账号
     */
    private String subjectId;
    /**
     * 被请求方账号
     */
    private String objectId;
    /**
     * 备注内容
     */
    private String memo;
}
```

## 2.4 DialogueInfoMessage

```java
public class DialogueInfoMessage {
    /**
     * 发送方账号
     */
    private String senderId;
    /**
     * 接收方账号
     */
    private String receiverId;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 消息存在时间
     */
    private double lastedTime;
}
```

## 2.5 回复代码

| 代码 | 含义                                  |
| ---- | ------------------------------------- |
| 0    | 成功执行                              |
| 100  | 不合法的消息                          |
| 101  | 不存在的消息类型                      |
| 102  | 消息类型与消息内容不符                |
| 200  | 不存在的用户消息类型                  |
| 201  | 用户ID已存在，无法注册                |
| 202  | 用户ID不存在，无法线上操作            |
| 203  | 密码不正确                            |
| 204  | 消息内用户ID与session拥有用户ID不一致 |
| 205  | 消息中未传入新昵称，无法更新          |
| 206  | 消息中未传入新密码，无法更新          |
| 207  | 注册时未传入完整信息                  |
| 208  | 用户不在线                            |

# 示例

仅有已经开发好的功能的示例。

以下是消息序列化后的字符串。

```json
//注册 
{"messageId":"d393badd-9ed5-444a-8d8a-3cdf19bddb7c","reply":false,"replyCode":0,"type":"userMessage","userId":"8569","userInfoMessage":{"id":"8569","nickname":"乌有之乡","password":"56897z","type":"sign_up"}}

//登录
{"messageId":"e4b9e610-461b-45a2-8e5b-3a306792d587","reply":false,"replyCode":0,"type":"userMessage","userInfoMessage":{"id":"8569","password":"56897z","type":"sign_in"}}
```

