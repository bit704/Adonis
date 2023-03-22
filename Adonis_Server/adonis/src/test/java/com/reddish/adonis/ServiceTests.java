package com.reddish.adonis;

import com.alibaba.fastjson2.JSON;
import com.reddish.adonis.service.UserInfoService;
import com.reddish.adonis.service.entity.Message;
import com.reddish.adonis.service.entity.UserInfoMessage;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class ServiceTests extends AdonisApplicationTests {

    @Test
    void message2String() {
    {
        // 注册A
        UserInfoMessage userInfoMessage = new UserInfoMessage("sign_up", "8569", "乌有之乡", "56897z");
        Message message = new Message(UUID.randomUUID().toString(), "UserInfoMessage", null, userInfoMessage,null,null);
        String messageString = JSON.toJSONString(message);
        System.out.println("注册A");
        System.out.println(messageString);
        System.out.println();
    }
    {
        // 登录A
        UserInfoMessage userInfoMessage = new UserInfoMessage("sign_in", "8569", null, "56897z");
        Message message = new Message(UUID.randomUUID().toString(), "UserInfoMessage", null, userInfoMessage,null,null);
        String messageString = JSON.toJSONString(message);
        System.out.println("登录A");
        System.out.println(messageString);
        System.out.println();
    }
    {
        // 注册B
        UserInfoMessage userInfoMessage = new UserInfoMessage("sign_up", "kkk110", "三国杀", "110256");
        Message message = new Message(UUID.randomUUID().toString(), "UserInfoMessage", null, userInfoMessage,null,null);
        String messageString = JSON.toJSONString(message);
        System.out.println("注册B");
        System.out.println(messageString);
        System.out.println();
    }
    {
        // 登录B
        UserInfoMessage userInfoMessage = new UserInfoMessage("sign_in", "kkk110", null, "110256");
        Message message = new Message(UUID.randomUUID().toString(), "UserInfoMessage", null, userInfoMessage,null,null);
        String messageString = JSON.toJSONString(message);
        System.out.println("登录B");
        System.out.println(messageString);
        System.out.println();
    }

    }
}
