# Adonis_通信协议

## 1 概述

所有消息均为`Message`类的对象，使用[alibaba/fastjson2](https://github.com/alibaba/fastjson2)序列化为字符串发送。

只需要填充所需字段，其它字段设为`null`。

## 2 消息结构

### 2.1 消息类

消息类位于`Adonis_Server/adonis/src/main/java/com/reddish/adonis/AO`。

全部消息均为**Message**类的对象，其**包含且仅包含一个**具体消息类的对象。Message类的对象将id作为唯一标识符，其用UUID生成。

一共7个具体消息类：

1. **ReplyMessage**用于服务端向客户端一一回复**所有**客户端向服务端发送过的任何消息（除了ReplyMessage）的查收情况，携带要回复的消息的id和回复代码。回复代码为0表示成功执行，其他代码均为**异常代码**。客户端也要向服务端发这个。

2. **UserOpMessage**用于客户端向服务端发送用户操作消息。

3. **UserInfoMessage**用于服务端向客户端发送用户信息消息，用来回应用户操作消息。

4. **FriendOpMessage**用于客户端向服务端发送好友操作消息。

5. **FriendInfoMessage**用于客户端向服务端发送好友信息消息，用来回应与相关好友操作相关的所有用户。

6. **DialogueInfoMessage**用于客户端向服务端发送和接收对话消息。

   > 注意，客户端不需要填写DialogueInfoMessage的occurredTime字段，服务端会填写，以服务端中转时间表示消息发送时间。

7. **UserOnlineMessage**用于服务端在客户端登录上线时，给其发送**离线期间**的消息，包括FriendInfoMessage和DialogueInfoMessage。用户**请求后**再发送。

### 2.2 消息代码

消息代码位于`Adonis_Server/adonis/src/main/java/com/reddish/adonis/AO/MessageCode.java`。

消息代码由枚举类的枚举对象表示，含有一个数字用作标识符，一个字符串用作解释性说明

### 2.3 异常代码

异常类位于`Adonis_Server/adonis/src/main/java/com/reddish/adonis/exception/ExceptionCode.java`。

异常表示开发过程中可能出现的错误，以及应由客户端预防而不应在服务端出现的错误，实际中并无用途。

## 3 协议说明

### 3.1 UserOnlineMessage说明

dialogueMessageList列表内含用户离线期间收到的所有DialogueMessage，按时间先后排序（以occurredTime作为排序字段）。

friendInfoMessageList列表内含好友相关的所有FriendInfoMessage。

这里的FriendInfoMessage会有的消息代码如下：

FIF_ADD_YOU 此用户正申请将您加入好友列表

FIF_ADD_TO 您正申请将此用户加入好友列表

FIF_TWO_WAY 此用户和您是双向好友

FIF_SINGLE_ON_YOU 此用户是您的单向好友（您列表没它，它列表有您）

FIF_SINGLE_FOR_YOU 此用户是您的单向好友（您列表没它，它列表有您）

FIF_BLOCK 此用户已将您拉黑

FIF_REJECT 此用户拒绝您的好友申请

FIF_NOT_EXIST 此用户已经注销，不存在了

### 3.2 FriendOpMessage和FriendInfoMessage关系说明

> 以下称FriendOpMessage中subjectId对应的用户为s，objectId对应的用户为o。

对于每一个FriendOpMessage，如果操作成功，s**必然**收到一个消息代码为FIF_OP_SUCCESS的FriendInfoMessage。

除此之外，FriendInfoMessage用于告知好友操作相关方（s、o）额外信息。

s发送FOP_ADD请求添加o为好友，s可能收到FIF_BLOCK、FIF_ADD_CONSENT，o可能收到FIF_ADD_YOU。

s发送FOP_CONSENT同意添加o为好友，o可能收到FIF_ADD_CONSENT。

s发送FOP_REJECT拒绝添加o为好友，o可能收到FIF_REJECT。

s发送FOP_DELETE删除好友o。

s发送FOP_QUERY_EXIST查询好友o是否存在，s可能收到FIF_EXIST、FIF_NOT_EXIST。

s发送FOP_QUERY_ONLINE查询好友o是否在线，s可能收到FIF_ONLINE、FIF_OFFLINE。

s发送FOP_BLOCK拉黑好友o。

s发送FOP_CUSTOM_NICKNAME修改对好友o的备注昵称。

s发送FOP_QUERY_FRIENDSHIP查询自己与o的好友关系，可能收到FIF_TWO_WAY、FIF_SINGLE_FOR_YOU、FIF_SINGLE_ON_YOU、FIF_FREE。

## 4 设计思路

不把这多个Message类继承一个父类，而是组合起来，是因为继承还得判断一次要序列化为哪个子类。

Message类的id用UUID生成以确保唯一性，作用有二：

1. 让ReplyMessage标记回复的是哪条消息。
2. 同一条消息可能被重复发送（TCP的超时重传机制），去重。

