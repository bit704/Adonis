package com.reddish.adonis.Service;

import com.reddish.adonis.AO.*;
import com.reddish.adonis.DO.Dialogue;
import com.reddish.adonis.DO.Friend;
import com.reddish.adonis.DO.User;
import com.reddish.adonis.Manager.DAOManager;
import com.reddish.adonis.Manager.Exception.MessageException;
import com.reddish.adonis.Manager.Exception.UserInfoException;
import com.reddish.adonis.Manager.SendManager;
import com.reddish.adonis.Websocket.Dispatcher;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;

import static com.reddish.adonis.AO.MessageCode.*;
import static com.reddish.adonis.Manager.Exception.ExceptionCode.*;

@Service
public class UserService {
    private final DAOManager daoManager;

    private final SendManager sendManager;

    public UserService(DAOManager daoManager, SendManager sendManager) {
        this.daoManager = daoManager;
        this.sendManager = sendManager;
    }

    public void handle(UserOpMessage userOpMessage, Session session) throws MessageException, UserInfoException {

        String userId = userOpMessage.getId();
        // 查看表中是否有该user
        User user = daoManager.selectUserById(userId);

        int code = userOpMessage.getCode();
        MessageCode messageCode = MessageCode.getCodeById(code);
        if (messageCode == null) {
            throw new MessageException(ILLEGAL_CODE);
        }

        MessageCode uif;

        // 这里先用if而不是直接用switch是因为：有部分公有代码为部分情况所有
        if (UOP_SIGN_IN.equals(messageCode)) {
            // 登录
            if (user == null) {
                uif = UIF_NOT_EXIST;
            } else if (!user.getPassword().equals(userOpMessage.getPassword())) {
                uif = UIF_WORRY_PASSWORD;
            } else {
                // 标记为在线
                Dispatcher.setOnline(userId);
                // 存userId和Session的对应关系
                Dispatcher.userId2SessionMap.put(userId, session);
                Dispatcher.session2UserIdMap.put(session, userId);
                uif = UIF_OP_SUCCESS;
            }

        } else if (UOP_SIGN_UP.equals(messageCode)) {
            // 已存在，不可注册
            if (user != null) {
                uif = UIF_ALREADY_EXIST;
            } else if (userId == null || userOpMessage.getNickname() == null || userOpMessage.getPassword() == null) {
                uif = UIF_INCOMPLETE_INFO;
            } else {
                daoManager.createNewUser(userId, userOpMessage.getNickname(), userOpMessage.getPassword());
                // 默认自己是自己的好友
                daoManager.createNewFriendship(userId, userId);
                uif = UIF_OP_SUCCESS;
            }
            // 校验消息内用户ID与session拥有用户ID是否一致
        } else if (!userId.equals(Dispatcher.session2UserIdMap.get(session))) {
            throw new UserInfoException(HACK);
        }
        // 校验用户是否在线
        else if (!Dispatcher.isOnline(userId)) {
            uif = UIF_OFFLINE;
        } else {
            switch (messageCode) {
                case UOP_SIGN_OUT -> {
                    // 标记为离线
                    Dispatcher.setOffline(userId);
                    uif = UIF_OP_SUCCESS;
                }
                case UOP_DELETE -> {
                    // TODO 还应该删除与其相关的好友信息和对话信息
                    daoManager.deleteUser(userId);
                    uif = UIF_OP_SUCCESS;
                }
                case UOP_CHANGE_NICKNAME -> {
                    String nicknameNew = userOpMessage.getNickname();
                    if (nicknameNew == null) {
                        uif = UIF_NO_NICKNAME;
                    } else {
                        daoManager.updateUserNickname(userId, nicknameNew);
                        uif = UIF_OP_SUCCESS;
                    }
                }
                case UOP_CHANGE_PASSWORD -> {
                    String passwordNew = userOpMessage.getPassword();
                    if (passwordNew == null) {
                        uif = UIF_NO_PASSWORD;
                    } else {
                        daoManager.updateUserPassword(userId, passwordNew);
                        uif = UIF_OP_SUCCESS;
                    }
                }
                case UOP_REQUEST_ONLINE_MESSAGE -> {
                    // 请求了再发送
                    uif = UIF_REPLY_ONLINE_MESSAGE;
                }
                default -> throw new MessageException(ILLEGAL_UOP_CODE);
            }
        }
        sendManager.sendUserInfoForUserOp(uif, session);
        if (UIF_REPLY_ONLINE_MESSAGE.equals(uif)) {
            sendUserOnlineMessage(session, userId);
        }
    }

    public void sendUserOnlineMessage(Session session, String userId) {

        // 向登录用户X发送上线消息
        List<FriendInfoMessage> friendInfoMessageList = new ArrayList<>();
        List<DialogueMessage> dialogueMessageList = new ArrayList<>();

        // 首先查询X作为客体的好友关系
        List<Friend> friendList = daoManager.queryFriendsOfObject(userId);

        for (Friend friend_o : friendList) {
            FriendInfoMessage friendInfoMessage = new FriendInfoMessage();
            // 此处的好友是好友关系中的主体
            String friendId = friend_o.getSubjectId();
            friendInfoMessage.setId(friendId);
            User friendUser = daoManager.selectUserById(friendId);
            // 好友还在，没有注销
            if (friendUser != null) {
                friendInfoMessage.setNickname(friendUser.getNickname());
                friendInfoMessage.setMemo(friend_o.getMemo());

                // 查询X为主体此好友为客体的关系
                Friend friend_so = daoManager.queryTheFriend(userId, friendId);

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
                List<Dialogue> dialogueList = daoManager.queryDialogues(friendId, userId);
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
        friendList = daoManager.queryFriendsOfSubject(userId);
        for (Friend friend_s : friendList) {
            // 此处的好友是好友关系中的客体
            String friendId = friend_s.getObjectId();

            // 查询X为客体此好友为主体的关系
            Friend friend_os = daoManager.queryTheFriend(friendId, userId);

            // friend_os为空的才没有在之前查询过，需要补充
            if (friend_os == null) {
                FriendInfoMessage friendInfoMessage = new FriendInfoMessage();
                // 填写好友id
                friendInfoMessage.setId(friend_s.getObjectId());
                User friendUser = daoManager.selectUserById(friend_s.getObjectId());
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
