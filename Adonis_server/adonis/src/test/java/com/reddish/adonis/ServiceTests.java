package com.reddish.adonis;

import com.alibaba.fastjson2.JSON;
import com.reddish.adonis.service.UserInfoService;
import com.reddish.adonis.service.entity.Message;
import com.reddish.adonis.service.entity.UserInfoMessage;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class ServiceTests extends AdonisApplicationTests {

    @Test
    /*
    注册A
    {"messageId":"d393badd-9ed5-444a-8d8a-3cdf19bddb7c","reply":false,"replyCode":0,"type":"userMessage","userId":"8569","userInfoMessage":{"id":"8569","nickname":"乌有之乡","password":"56897z","type":"sign_up"}}

    登录A
    {"messageId":"e4b9e610-461b-45a2-8e5b-3a306792d587","reply":false,"replyCode":0,"type":"userMessage","userInfoMessage":{"id":"8569","password":"56897z","type":"sign_in"}}


    */
    void message2String() {
    {
        // 注册A
        UserInfoMessage userInfoMessage = new UserInfoMessage("sign_up", "8569", "乌有之乡", "56897z");
        Message message = new Message(false, 0, UUID.randomUUID().toString(), "userMessage", userInfoMessage, null,null);
        String messageString = JSON.toJSONString(message);
        System.out.println(messageString);
        System.out.println();
    }
    {
        // 登录A
        UserInfoMessage userInfoMessage = new UserInfoMessage("sign_in", "8569", null, "56897z");
        Message message = new Message(false, 0, UUID.randomUUID().toString(), "userMessage", userInfoMessage, null, null);
        String messageString = JSON.toJSONString(message);
        System.out.println(messageString);
        System.out.println();
    }
    {
        //
    }

    }
}
