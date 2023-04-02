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
        // 用户1注册
        UserInfoMessage userInfoMessage = new UserInfoMessage("sign_up", "8569", "乌有之乡", "56897z");
        Message message = new Message(UUID.randomUUID().toString(), "UserInfoMessage", null, userInfoMessage,null,null);
        String messageString = JSON.toJSONString(message);
        System.out.println("用户1注册");
        System.out.println(messageString);
        System.out.println();
    }
    {
        // 用户1登录
        UserInfoMessage userInfoMessage = new UserInfoMessage("sign_in", "8569", null, "56897z");
        Message message = new Message(UUID.randomUUID().toString(), "UserInfoMessage", null, userInfoMessage,null,null);
        String messageString = JSON.toJSONString(message);
        System.out.println("用户1登录");
        System.out.println(messageString);
        System.out.println();
    }
    {
        // 用户2注册
        UserInfoMessage userInfoMessage = new UserInfoMessage("sign_up", "kkk110", "三国杀", "110256");
        Message message = new Message(UUID.randomUUID().toString(), "UserInfoMessage", null, userInfoMessage,null,null);
        String messageString = JSON.toJSONString(message);
        System.out.println("用户2注册");
        System.out.println(messageString);
        System.out.println();
    }
    {
        // 用户2登录
        UserInfoMessage userInfoMessage = new UserInfoMessage("sign_in", "kkk110", null, "110256");
        Message message = new Message(UUID.randomUUID().toString(), "UserInfoMessage", null, userInfoMessage,null,null);
        String messageString = JSON.toJSONString(message);
        System.out.println("用户2登录");
        System.out.println(messageString);
        System.out.println();
    }
    {
        // 用户3注册
        UserInfoMessage userInfoMessage = new UserInfoMessage("sign_up", "peek", "清风吹拂", "19990607");
        Message message = new Message(UUID.randomUUID().toString(), "UserInfoMessage", null, userInfoMessage,null,null);
        String messageString = JSON.toJSONString(message);
        System.out.println("用户3注册");
        System.out.println(messageString);
        System.out.println();
    }
    {
        // 用户3登录
        UserInfoMessage userInfoMessage = new UserInfoMessage("sign_in", "peek", null, "19990607");
        Message message = new Message(UUID.randomUUID().toString(), "UserInfoMessage", null, userInfoMessage,null,null);
        String messageString = JSON.toJSONString(message);
        System.out.println("用户3登录");
        System.out.println(messageString);
        System.out.println();
    }
    {
        // 用户3修改昵称
        UserInfoMessage userInfoMessage = new UserInfoMessage("change_nickname", "peek", "浪迹天涯", null);
        Message message = new Message(UUID.randomUUID().toString(), "UserInfoMessage", null, userInfoMessage,null,null);
        String messageString = JSON.toJSONString(message);
        System.out.println("用户3修改昵称");
        System.out.println(messageString);
        System.out.println();
    }
    {
        // 用户3修改密码
        UserInfoMessage userInfoMessage = new UserInfoMessage("change_password", "peek", null, "19990607cs");
        Message message = new Message(UUID.randomUUID().toString(), "UserInfoMessage", null, userInfoMessage,null,null);
        String messageString = JSON.toJSONString(message);
        System.out.println("用户3修改密码");
        System.out.println(messageString);
        System.out.println();
    }
    {
        // 用户3登出
        UserInfoMessage userInfoMessage = new UserInfoMessage("sign_out", "peek", null, null);
        Message message = new Message(UUID.randomUUID().toString(), "UserInfoMessage", null, userInfoMessage,null,null);
        String messageString = JSON.toJSONString(message);
        System.out.println("用户3登出");
        System.out.println(messageString);
        System.out.println();
    }
    {
        // 用户3注销
        UserInfoMessage userInfoMessage = new UserInfoMessage("delete", "peek", null, null);
        Message message = new Message(UUID.randomUUID().toString(), "UserInfoMessage", null, userInfoMessage,null,null);
        String messageString = JSON.toJSONString(message);
        System.out.println("用户3注销");
        System.out.println(messageString);
        System.out.println();
    }



    }
}
