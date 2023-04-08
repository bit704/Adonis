package com.reddish.adonis;

import com.alibaba.fastjson2.JSON;
import com.reddish.adonis.service.entity.Message;
import com.reddish.adonis.service.entity.UserOpMessage;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class ServiceTests extends AdonisApplicationTests {

    @Test
    void message2String() {
    {
        // 用户1注册
        UserOpMessage userOpMessage = new UserOpMessage("sign_up", "8569", "乌有之乡", "56897z");
        Message message = new Message(UUID.randomUUID().toString(), "UserOpMessage", null, userOpMessage,null,null,null);
        String messageString = JSON.toJSONString(message);
        System.out.println("用户1注册");
        System.out.println(messageString);
        System.out.println();
    }
    {
        // 用户1登录
        UserOpMessage userOpMessage = new UserOpMessage("sign_in", "8569", null, "56897z");
        Message message = new Message(UUID.randomUUID().toString(), "UserOpMessage", null, userOpMessage,null,null,null);
        String messageString = JSON.toJSONString(message);
        System.out.println("用户1登录");
        System.out.println(messageString);
        System.out.println();
    }
    {
        // 用户2注册
        UserOpMessage userOpMessage = new UserOpMessage("sign_up", "kkk110", "三国杀", "110256");
        Message message = new Message(UUID.randomUUID().toString(), "UserOpMessage", null, userOpMessage,null,null,null);
        String messageString = JSON.toJSONString(message);
        System.out.println("用户2注册");
        System.out.println(messageString);
        System.out.println();
    }
    {
        // 用户2登录
        UserOpMessage userOpMessage = new UserOpMessage("sign_in", "kkk110", null, "110256");
        Message message = new Message(UUID.randomUUID().toString(), "UserOpMessage", null, userOpMessage,null,null,null);
        String messageString = JSON.toJSONString(message);
        System.out.println("用户2登录");
        System.out.println(messageString);
        System.out.println();
    }
    {
        // 用户3注册
        UserOpMessage userOpMessage = new UserOpMessage("sign_up", "peek", "清风吹拂", "19990607");
        Message message = new Message(UUID.randomUUID().toString(), "UserOpMessage", null, userOpMessage,null,null,null);
        String messageString = JSON.toJSONString(message);
        System.out.println("用户3注册");
        System.out.println(messageString);
        System.out.println();
    }
    {
        // 用户3登录
        UserOpMessage userOpMessage = new UserOpMessage("sign_in", "peek", null, "19990607");
        Message message = new Message(UUID.randomUUID().toString(), "UserOpMessage", null, userOpMessage,null,null,null);
        String messageString = JSON.toJSONString(message);
        System.out.println("用户3登录");
        System.out.println(messageString);
        System.out.println();
    }
    {
        // 用户3修改昵称
        UserOpMessage userOpMessage = new UserOpMessage("change_nickname", "peek", "浪迹天涯", null);
        Message message = new Message(UUID.randomUUID().toString(), "UserOpMessage", null, userOpMessage,null,null,null);
        String messageString = JSON.toJSONString(message);
        System.out.println("用户3修改昵称");
        System.out.println(messageString);
        System.out.println();
    }
    {
        // 用户3修改密码
        UserOpMessage userOpMessage = new UserOpMessage("change_password", "peek", null, "19990607cs");
        Message message = new Message(UUID.randomUUID().toString(), "UserOpMessage", null, userOpMessage,null,null,null);
        String messageString = JSON.toJSONString(message);
        System.out.println("用户3修改密码");
        System.out.println(messageString);
        System.out.println();
    }
    {
        // 用户3登出
        UserOpMessage userOpMessage = new UserOpMessage("sign_out", "peek", null, null);
        Message message = new Message(UUID.randomUUID().toString(), "UserOpMessage", null, userOpMessage,null,null,null);
        String messageString = JSON.toJSONString(message);
        System.out.println("用户3登出");
        System.out.println(messageString);
        System.out.println();
    }
    {
        // 用户3注销
        UserOpMessage userOpMessage = new UserOpMessage("delete", "peek", null, null);
        Message message = new Message(UUID.randomUUID().toString(), "UserOpMessage", null, userOpMessage,null,null,null);
        String messageString = JSON.toJSONString(message);
        System.out.println("用户3注销");
        System.out.println(messageString);
        System.out.println();
    }



    }
}
