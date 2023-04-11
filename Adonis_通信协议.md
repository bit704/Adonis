# Adonis_通信协议

## 1 概述

所有消息均为`Message`类的对象，使用[alibaba/fastjson2](https://github.com/alibaba/fastjson2)序列化为字符串发送。

只需要填充所需字段，其它字段设为`null`。

## 2 消息结构

### 2.1 消息类

消息类位于`Adonis\Adonis_Server\adonis\src\main\java\com\reddish\adonis\service\entity`。

### 2.2 消息代码

消息代码位于`Adonis_Server/adonis/src/main/java/com/reddish/adonis/service/entity/MessageCode.java`。

### 2.3 异常代码

异常类位于`Adonis\Adonis_Server\adonis\src\main\java\com\reddish\adonis\exception`。

异常表示开发过程中可能出现的错误，以及应由客户端预防而不应在服务端出现的错误，实际中并无用途。

## 3 协议说明

全部消息均为**Message**类的对象，其id为唯一标识符，用UUID生成，其包含其它消息类的对象。

1. **ReplyMessage**用于服务端向客户端一一回复**所有**客户端向服务端发送过的任何消息（除了ReplyMessage）的查收情况，携带要回复的消息的id和回复代码。回复代码为0表示成功执行，其他代码均为**异常代码**。客户端也要向服务端发这个。

2. **UserOpMessage**用于客户端向服务端发送用户操作消息。如有必要，客户端会收到服务端回复的**UserInfoMessage**告知操作情况。

3. **FriendOpMessage**用于客户端向服务端发送好友操作消息。如果涉及此操作（如申请添加好友）的其它用户（包括发送方自己，如查询其它好友在线状态，结果会发回自己）在线，其它用户会**立刻**收到服务端填写的**FriendInfoMessage**。

4. **DialogueInfoMessage**用于客户端向服务端发送对话消息。如果涉及此操作的其它用户在线，其它用户会**立刻**收到服务端**转发**的相同消息。

   > 注意，客户端不需要填写DialogueInfoMessage的occurredTime字段，服务端会填写，以服务端中转时间表示消息发送时间。

5. **UserOnlineMessage**用于服务端在客户端登录上线时，给其发送其好友关系和离线期间的消息，包括FriendInfoMessage和DialogueInfoMessage。用户**请求后**再发送。

## 4 示例

以下是消息序列化后的字符串。测试连接用。

```json
暂无
```

## 5 设计思路

不把这多个Message类继承一个父类，而是组合起来，是因为继承还得判断一次要序列化为哪个子类。

Message类的id用UUID生成以确保唯一性，作用有二：

1. 让ReplyMessage标记回复的是哪条消息。
2. 同一条消息可能被重复发送（TCP的超时重传机制），去重。

