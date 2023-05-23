package com.reddish.adonis;

import com.alibaba.fastjson2.JSON;
import com.reddish.adonis.Message.FriendOpMessage;
import com.reddish.adonis.Message.Message;
import org.junit.jupiter.api.Test;

public class ServiceTests extends AdonisApplicationTests {

    @Test
    void message2String() {
        {
            FriendOpMessage friendOpMessage = new FriendOpMessage(305, "xgg", "xjj", "", "");
            Message message = new Message(friendOpMessage);
            System.out.println(JSON.toJSONString(message));
        }
    }
}