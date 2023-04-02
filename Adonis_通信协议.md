# 1 概述

所有消息均为`Message`类的对象，使用[alibaba/fastjson2](https://github.com/alibaba/fastjson2)序列化为字符串发送。

只需要填充所需字段，其它字段设为`null`。

# 2 消息结构

## 2.1 消息类

消息类位于`Adonis\Adonis_Server\adonis\src\main\java\com\reddish\adonis\service\entity`。

## 2.2 服务端异常代码

异常类位于`Adonis\Adonis_Server\adonis\src\main\java\com\reddish\adonis\exception`。

# 示例

以下是消息序列化后的字符串。测试用。

```json
用户1注册
{"id":"b8f7c628-1b19-4052-85ec-7c281ad53185","type":"UserInfoMessage","userInfoMessage":{"id":"8569","nickname":"乌有之乡","password":"56897z","type":"sign_up"}}

用户1登录
{"id":"7d161356-ea7a-4c7b-baad-3730ce74db3d","type":"UserInfoMessage","userInfoMessage":{"id":"8569","password":"56897z","type":"sign_in"}}

用户2注册
{"id":"90b4eb43-b458-468e-9772-97c117a42131","type":"UserInfoMessage","userInfoMessage":{"id":"kkk110","nickname":"三国杀","password":"110256","type":"sign_up"}}

用户2登录
{"id":"bbb597a1-582e-4168-a777-138c523a8519","type":"UserInfoMessage","userInfoMessage":{"id":"kkk110","password":"110256","type":"sign_in"}}

用户3注册
{"id":"a6529f3b-7596-4074-9e97-d96218b806a7","type":"UserInfoMessage","userInfoMessage":{"id":"peek","nickname":"清风吹拂","password":"19990607","type":"sign_up"}}

用户3登录
{"id":"5af1fa86-6575-48aa-b0e1-ba647f5d930a","type":"UserInfoMessage","userInfoMessage":{"id":"peek","password":"19990607","type":"sign_in"}}

用户3修改昵称
{"id":"12d9f44a-8cb0-4008-bed6-76702d0634fc","type":"UserInfoMessage","userInfoMessage":{"id":"peek","nickname":"浪迹天涯","type":"change_nickname"}}

用户3修改密码
{"id":"f61ef9b4-76e3-4aaa-9da8-44267a46c960","type":"UserInfoMessage","userInfoMessage":{"id":"peek","password":"19990607cs","type":"change_password"}}

用户3登出
{"id":"fde98e5d-6377-4a32-87d4-76163f7d0520","type":"UserInfoMessage","userInfoMessage":{"id":"peek","type":"sign_out"}}

用户3注销
{"id":"9fff2ddd-04e8-4dda-b112-b30e7ef178f3","type":"UserInfoMessage","userInfoMessage":{"id":"peek","type":"delete"}}

```

