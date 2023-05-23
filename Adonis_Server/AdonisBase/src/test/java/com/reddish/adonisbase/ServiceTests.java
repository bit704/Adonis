package com.reddish.adonisbase;

import com.alibaba.fastjson2.JSON;
import com.reddish.adonisbase.Message.FriendOpMessage;
import com.reddish.adonisbase.Message.Message;
import org.junit.jupiter.api.Test;

public class ServiceTests extends AdonisBaseApplicationTests {

    @Test
    void message2String() {
        {
            FriendOpMessage friendOpMessage = new FriendOpMessage(305, "xgg", "xjj", "", "");
            Message message = new Message(friendOpMessage);
            System.out.println(JSON.toJSONString(message));
        }
    }
}