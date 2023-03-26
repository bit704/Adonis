# 1 概述

所有消息均为`Message`类的对象，使用[alibaba/fastjson2](https://github.com/alibaba/fastjson2)序列化为字符串发送。

只需要填充所需字段，其它字段设为`null`。

# 2 消息结构

## 2.1 消息类

消息类位于`Adonis\Adonis_Server\adonis\src\main\java\com\reddish\adonis\service\entity`。

## 2.2 服务端异常代码

异常类位于`Adonis\Adonis_Server\adonis\src\main\java\com\reddish\adonis\exception`。

# 示例

以下是消息序列化后的字符串。可直接发送给服务端进行测试。

```json
注册A
{"id":"e6279dbd-a9a1-4207-b9da-0e411193f947","type":"UserInfoMessage","userInfoMessage":{"id":"8569","nickname":"乌有之乡","password":"56897z","type":"sign_up"}}

登录A
{"id":"58a3fc56-063e-43b9-bb05-fba2abbad6d9","type":"UserInfoMessage","userInfoMessage":{"id":"8569","password":"56897z","type":"sign_in"}}

注册B
{"id":"39565db9-e82f-4e50-929f-22fba9ba8330","type":"UserInfoMessage","userInfoMessage":{"id":"kkk110","nickname":"三国杀","password":"110256","type":"sign_up"}}

登录B
{"id":"a8708c4d-4278-4756-892b-d687d6da61d2","type":"UserInfoMessage","userInfoMessage":{"id":"kkk110","password":"110256","type":"sign_in"}}

```

