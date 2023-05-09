package com.reddish.adonis.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.reddish.adonis.exception.DialogueInfoException;
import com.reddish.adonis.exception.ExceptionCode;
import com.reddish.adonis.exception.MessageException;
import com.reddish.adonis.DAO.DialogueMapper;
import com.reddish.adonis.DAO.FriendMapper;
import com.reddish.adonis.DAO.UserMapper;
import com.reddish.adonis.DO.Dialogue;
import com.reddish.adonis.DO.Friend;
import com.reddish.adonis.DO.User;
import com.reddish.adonis.AO.DialogueMessage;
import com.reddish.adonis.AO.Message;
import com.reddish.adonis.websocket.Dispatcher;
import org.springframework.stereotype.Service;

import javax.websocket.Session;

@Service
public class DialogueService {

    private final UserMapper userMapper;
    private final FriendMapper friendMapper;
    private final DialogueMapper dialogueMapper;

    public DialogueService(UserMapper userMapper, FriendMapper friendMapper, DialogueMapper dialogueMapper) {
        this.userMapper = userMapper;
        this.friendMapper = friendMapper;
        this.dialogueMapper = dialogueMapper;
    }

    private void sendDialogueInfoMessage(DialogueMessage dialogueMessage, String receiverId) {
        Message message = new Message(dialogueMessage);
        Session session = Dispatcher.userId2SessionMap.get(receiverId);
        Dispatcher.sendMessage(session, message);
    }

    public void handle(DialogueMessage dialogueMessage, Session session) throws MessageException, DialogueInfoException {

        String senderId = dialogueMessage.getSenderId();
        String receiverId = dialogueMessage.getReceiverId();
        User user1 = userMapper.selectById(senderId);
        User user2 = userMapper.selectById(receiverId);

        if (user1 == null || user2 == null) {
            throw new DialogueInfoException(ExceptionCode.SHADOW_MAN);
        }

        // 可以自己给自己发消息，user1和user2可以相同
        // cccurredTime是消息到达服务端的时间，由服务端填写
        dialogueMessage.setOccurredTime(System.currentTimeMillis());

        // 判断还是不是双向好友
        QueryWrapper<Friend> queryWrapper_sr = new QueryWrapper<>();
        queryWrapper_sr.eq("subjectId", senderId).eq("objectId", receiverId);
        Friend friend_so = friendMapper.selectOne(queryWrapper_sr);
        QueryWrapper<Friend> queryWrapper_rs = new QueryWrapper<>();
        queryWrapper_rs.eq("subjectId", senderId).eq("objectId", receiverId);
        Friend friend_os = friendMapper.selectOne(queryWrapper_rs);
        if (friend_so.getStatus() != 1 || friend_os.getStatus() != 1) {
            throw new DialogueInfoException(ExceptionCode.STRANGER);
        }

        // 对方刚好在线,直接转发
        if (Dispatcher.isOnline(receiverId)) {
            sendDialogueInfoMessage(dialogueMessage, receiverId);
        }

        // 否则先存在数据库里
        else {
            dialogueMapper.insert(new Dialogue(senderId, receiverId,
                    dialogueMessage.getContent(),
                    dialogueMessage.getLastedTime(),
                    dialogueMessage.getOccurredTime()));
        }
    }

}
