package com.reddish.adoniswebsocket.Manager;

import com.reddish.adoniswebsocket.FeignClient.DAOFeignClient;
import com.reddish.adoniswebsocket.Utils.DTO.Dialogue;
import com.reddish.adoniswebsocket.Utils.DTO.Friend;
import com.reddish.adoniswebsocket.Utils.DTO.User;
import com.reddish.adoniswebsocket.Utils.Message.*;
import com.reddish.adoniswebsocket.Websocket.Dispatcher;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;

import static com.reddish.adoniswebsocket.Utils.Message.MessageCode.*;

@Component
public class SendManager {

    final DAOFeignClient daoFeignClient;

    public SendManager(DAOFeignClient daoFeignClient) {
        this.daoFeignClient = daoFeignClient;
    }


    public void sendUserInfoForUserOp(MessageCode messageCode, Session session) {
        Dispatcher.sendMessage(session, new Message(new UserInfoMessage(messageCode.getId())));
    }

    public void sendFriendInfoForOp(FriendOpMessage friendOpMessage, MessageCode messageCode, String infoId, String send2Id) {
        FriendInfoMessage friendInfoMessage = new FriendInfoMessage();

        // 消息代码
        friendInfoMessage.setCode(messageCode.getId());

        // 此消息要说明的用户
        friendInfoMessage.setId(infoId);

        // friendOpMessage指定要操作的用户
        User user = daoFeignClient.selectUserById(friendOpMessage.getObjectId());
        if (user != null) {
            // 好友昵称
            friendInfoMessage.setNickname(user.getNickname());
        }

        // 对好友的备注昵称
        friendInfoMessage.setCustomNickname(friendOpMessage.getCustomNickname());

        // 备注
        friendInfoMessage.setMemo(friendOpMessage.getMemo());

        Message message = new Message(friendInfoMessage);
        Session session = Dispatcher.userId2SessionMap.get(send2Id);
        Dispatcher.sendMessage(session, message);
    }


    public void sendDialogue(String senderId, String receiverId, String content, long occurredTime, long lastedTime) {
        // 对方刚好在线,直接转发
        if (Dispatcher.isOnline(receiverId)) {
            DialogueMessage dialogueMessage = new DialogueMessage(senderId, receiverId, content, occurredTime, lastedTime);
            Message message = new Message(dialogueMessage);
            Session session = Dispatcher.userId2SessionMap.get(receiverId);
            Dispatcher.sendMessage(session, message);
        }
        // 否则先存在数据库里
        else {
            daoFeignClient.saveDialogue(senderId, receiverId, content, occurredTime, lastedTime);
        }
    }


    public void sendUserOnlineMessage(Session session, String userId) {

        // 向登录用户X发送上线消息
        List<FriendInfoMessage> friendInfoMessageList = new ArrayList<>();
        List<DialogueMessage> dialogueMessageList = new ArrayList<>();

        // 首先查询X作为客体的好友关系
        List<Friend> friendList = daoFeignClient.queryFriendsOfObject(userId);

        for (Friend friend_o : friendList) {
            FriendInfoMessage friendInfoMessage = new FriendInfoMessage();
            // 此处的好友是好友关系中的主体
            String friendId = friend_o.getSubjectId();
            friendInfoMessage.setId(friendId);
            User friendUser = daoFeignClient.selectUserById(friendId);
            // 好友还在，没有注销
            if (friendUser != null) {
                friendInfoMessage.setNickname(friendUser.getNickname());
                friendInfoMessage.setMemo(friend_o.getMemo());

                // 查询X为主体此好友为客体的关系
                Friend friend_so = daoFeignClient.queryTheFriend(userId, friendId);

                switch (friend_o.getStatus()) {
                    case 0 -> friendInfoMessage.setCode(FIF_ADD_YOU.getId());
                    case 1 -> {
                        if (friend_so != null && friend_so.getStatus() == 1) {
                            // 填入备注昵称。这种情况下才可能已有备注昵称。
                            friendInfoMessage.setCustomNickname(friend_so.getCustomNickname());
                            friendInfoMessage.setCode(FIF_TWO_WAY.getId());
                        } else {
                            friendInfoMessage.setCode(FIF_SINGLE_ON_YOU.getId());
                        }
                    }
                    case 2 -> friendInfoMessage.setCode(FIF_BLOCK.getId());
                    case 3 -> friendInfoMessage.setCode(FIF_REJECT.getId());
                }

                // 收集离线聊天记录
                List<Dialogue> dialogueList = daoFeignClient.queryDialogues(friendId, userId);
                for (Dialogue dialogue : dialogueList) {
                    dialogueMessageList.add(new DialogueMessage(
                            dialogue.getSenderId(),
                            dialogue.getReceiverId(),
                            dialogue.getContent(),
                            dialogue.getOccurredTime(),
                            dialogue.getLastedTime()));
                }
            } else {
                friendInfoMessage.setCode(FIF_NOT_EXIST.getId());
            }
            friendInfoMessageList.add(friendInfoMessage);
        }

        //其次查询X作为主体的好友关系
        friendList = daoFeignClient.queryFriendsOfSubject(userId);
        for (Friend friend_s : friendList) {
            // 此处的好友是好友关系中的客体
            String friendId = friend_s.getObjectId();

            // 查询X为客体此好友为主体的关系
            Friend friend_os = daoFeignClient.queryTheFriend(friendId, userId);

            // friend_os为空的才没有在之前查询过，需要补充
            if (friend_os == null) {
                FriendInfoMessage friendInfoMessage = new FriendInfoMessage();
                // 填写好友id
                friendInfoMessage.setId(friend_s.getObjectId());
                User friendUser = daoFeignClient.selectUserById(friend_s.getObjectId());
                if (friendUser != null) {
                    friendInfoMessage.setNickname(friendUser.getNickname());
                    friendInfoMessage.setMemo(friend_s.getMemo());

                    if (friend_s.getStatus() == 0) {
                        friendInfoMessage.setCode(FIF_ADD_TO.getId());
                    } else {
                        friendInfoMessage.setCode(FIF_SINGLE_FOR_YOU.getId());
                    }
                } else {
                    friendInfoMessage.setCode(FIF_NOT_EXIST.getId());
                }
                friendInfoMessageList.add(friendInfoMessage);
            }
        }

        // 对话消息排序后再发出，旧消息在前新消息在后
        dialogueMessageList.sort((a, b) -> {
            long r = a.getOccurredTime() - b.getOccurredTime();
            return r > 0 ? 1 : (r == 0 ? 0 : -1);
        });

        // 发送
        Dispatcher.sendMessage(session, new Message(new UserOnlineMessage(friendInfoMessageList, dialogueMessageList)));

    }

}
