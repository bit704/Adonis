# Adonis_通信协议

## 1 概述

所有消息均为`Message`类的对象，使用[alibaba/fastjson2](https://github.com/alibaba/fastjson2)序列化为字符串发送。

只需要填充所需字段，其它字段设为`null`。

## 2 消息结构

### 2.1 消息类

消息类位于`Adonis_Server/adonis/src/main/java/com/reddish/adonis/Message`。

全部消息均为**Message**类的对象，其**包含且仅包含一个**具体消息类的对象。Message类的对象将id作为唯一标识符，其用UUID生成。

一共7个具体消息类：

1. **ReplyMessage**用于服务端向客户端一一回复**所有**客户端向服务端发送过的任何消息（除了ReplyMessage）的查收情况，携带要回复的消息的id和回复代码。回复代码为0表示成功执行，其他代码均为**异常代码**。客户端也要向服务端发这个。

2. **UserOpMessage**用于客户端向服务端发送用户操作消息。

3. **UserInfoMessage**用于服务端向客户端发送用户信息消息，用来回应用户操作消息。

4. **FriendOpMessage**用于客户端向服务端发送好友操作消息。

5. **FriendInfoMessage**用于客户端向服务端发送好友信息消息，用来回应与相关好友操作相关的所有用户。

6. **DialogueInfoMessage**用于客户端向服务端发送和接收对话消息。

7. **UserOnlineMessage**用于服务端在客户端登录上线时，给其发送**离线期间**的消息，包括FriendInfoMessage和DialogueInfoMessage。用户**请求后**再发送。

### 2.2 消息代码

消息代码位于`Adonis_Server/adonis/src/main/java/com/reddish/adonis/AO/MessageCode.java`。

消息代码由具体消息对象携带，用枚举类的枚举对象表示，含有一个数字用作标识符，一个字符串用作解释性说明

### 2.3 异常代码

异常代码位于`Adonis_Server/adonis/src/main/java/com/reddish/adonis/exception/ExceptionCode.java`。

异常代码由ReplyMessage对象携带，异常表示开发过程中可能出现的错误，以及应由客户端预防而不应在服务端出现的错误，实际中并无用途。

## 3 详细说明

### 3.1 UserOpMessage和UserInfoMessage说明

客户端向服务端发送UserOpMessage，服务端向客户端发送UserInfoMessage。

**其中消息代码所有可能的情况如下**

UIF_OP_SUCCESS用来回复**所有**已成功处理的UserOpMessage。下面是其它情况。

发送UOP_SIGN_IN请求登录，可能收到UIF_NOT_EXIST、UIF_WORRY_PASSWORD。

发送UOP_SIGN_UP请求注册，可能收到UIF_ALREADY_EXIST、UIF_INCOMPLETE_INFO。

下面情况均需要操作放在线，不在线会发送UIF_OFFLINE。

发送UOP_SIGN_OUT下线。

发送UOP_DELETE请求注销。

发送UOP_CHANGE_NICKNAME修改昵称，可能收到UIF_NO_NICKNAME。

发送UOP_CHANGE_PASSWORD修改密码，可能收到UIF_NO_PASSWORD。

发送UOP_REQUEST_ONLINE_MESSAGE请求在线消息（同步自己不在线时的消息），必然收到UIF_REPLY_ONLINE_MESSAGE，然后服务端会发送UserOnlineMessage给请求方。

### 3.2 FriendOpMessage和FriendInfoMessage说明

客户端向服务端发送FriendOpMessage，服务端向客户端发送FriendInfoMessage（不仅向操作发送方发送，也可能向操作涉及方发送）。

> 以下称FriendOpMessage中subjectId对应的用户为s，objectId对应的用户为o。

FriendInfoMessage用于告知好友操作相关方（s、o）额外信息。

**其中消息代码所有可能的情况如下**

FIF_OP_SUCCESS用来回复**所有**已成功处理的FriendOpMessage。下面是其它情况。

s发送FOP_ADD请求添加o为好友，s可能收到FIF_BLOCK、FIF_ADD_CONSENT，o可能收到FIF_ADD_YOU。

s发送FOP_CONSENT同意添加o为好友，o可能收到FIF_ADD_CONSENT。

s发送FOP_REJECT拒绝添加o为好友，o可能收到FIF_REJECT。

s发送FOP_DELETE删除好友o。

s发送FOP_QUERY_EXIST查询好友o是否存在，s可能收到FIF_EXIST、FIF_NOT_EXIST。

s发送FOP_QUERY_ONLINE查询好友o是否在线，s可能收到FIF_ONLINE、FIF_OFFLINE。

s发送FOP_BLOCK拉黑好友o。

s发送FOP_CUSTOM_NICKNAME修改对好友o的备注昵称。

s发送FOP_QUERY_FRIENDSHIP查询自己与o的好友关系，可能收到FIF_TWO_WAY、FIF_SINGLE_FOR_YOU、FIF_SINGLE_ON_YOU、FIF_FREE。

### 3.3 DialogueMessage说明

其中**lastedTime**字段表示消息存活时间T，由客户端填写。当消息查收方查看该消息后经过时间T，该消息会自动销毁。若T=0，表示该消息存活时间为无限。用长整型表示，单位为毫秒，意思是T的大小。

客户端不需要填写**occurredTime**字段，服务端会填写，以服务端中转时间表示消息发送时间。用长整型表示，单位为毫秒，意义是自1970年1月1日 00:00:00 GMT到消息发送时刻经过的毫秒数，

### 3.4 UserOnlineMessage说明

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

### 3.5 ReplyMessage说明

只要收到消息，就会发送异常代码为0的此消息表示收到。

ReplyMessage包含的其它异常代码应只用于开发调试。

## 4 设计思路

不把这多个Message类继承一个父类，而是组合起来，是因为继承还得判断一次要序列化为哪个子类。

Message类的id用UUID生成以确保唯一性，作用有二：

1. 让ReplyMessage标记回复的是哪条消息。
2. 同一条消息可能被重复发送（TCP的超时重传机制），去重。

