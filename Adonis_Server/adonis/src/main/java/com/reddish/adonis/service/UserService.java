package com.reddish.adonis.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.reddish.adonis.AO.*;
import com.reddish.adonis.exception.MessageException;
import com.reddish.adonis.exception.UserInfoException;
import com.reddish.adonis.DAO.DialogueMapper;
import com.reddish.adonis.DAO.FriendMapper;
import com.reddish.adonis.DAO.UserMapper;
import com.reddish.adonis.DO.Dialogue;
import com.reddish.adonis.DO.Friend;
import com.reddish.adonis.DO.User;
import com.reddish.adonis.websocket.Dispatcher;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;

import static com.reddish.adonis.exception.ExceptionCode.*;
import static com.reddish.adonis.AO.MessageCode.*;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final FriendMapper friendMapper;
    private final DialogueMapper dialogueMapper;

    public UserService(UserMapper userMapper, FriendMapper friendMapper, DialogueMapper dialogueMapper) {
        this.userMapper = userMapper;
        this.friendMapper = friendMapper;
        this.dialogueMapper = dialogueMapper;
    }

    private void sendInfoForOp(MessageCode messageCode, Session session) {
        Dispatcher.sendMessage(session, new Message(new UserInfoMessage(messageCode.getId())));
    }

    public void handle(UserOpMessage userOpMessage, Session session) throws MessageException, UserInfoException {

        String userId = userOpMessage.getId();
        // 查看表中是否有该user
        User user = userMapper.selectById(userId);

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
                userMapper.insert(new User(userId, userOpMessage.getNickname(), userOpMessage.getPassword()));
                // 默认自己是自己的好友
                friendMapper.insert(new Friend(userId, userId, 1, null, null));
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
                    userMapper.deleteById(userId);
                    uif = UIF_OP_SUCCESS;
                }
                case UOP_CHANGE_NICKNAME -> {
                    UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
                    String nickname_new = userOpMessage.getNickname();
                    if (nickname_new == null) {
                        uif = UIF_NO_NICKNAME;
                    } else {
                        updateWrapper.eq("id", userId).set("nickname", nickname_new);
                        userMapper.update(null, updateWrapper);
                        uif = UIF_OP_SUCCESS;
                    }
                }
                case UOP_CHANGE_PASSWORD -> {
                    UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
                    String password_new = userOpMessage.getPassword();
                    if (password_new == null) {
                        uif = UIF_NO_PASSWORD;
                    } else {
                        updateWrapper.eq("id", userId).set("password", password_new);
                        userMapper.update(null, updateWrapper);
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
        sendInfoForOp(uif, session);
        if (UIF_REPLY_ONLINE_MESSAGE.equals(uif)) {
            sendUserOnlineMessage(session, userId);
        }
    }

    public void sendUserOnlineMessage(Session session, String userId) {

        List<FriendInfoMessage> friendInfoMessageList = new ArrayList<>();
        List<DialogueMessage> dialogueMessageList = new ArrayList<>();

        QueryWrapper<Friend> friendQueryWrapper_o = new QueryWrapper<>();
        friendQueryWrapper_o.eq("objectId", userId);

        List<Friend> friendList = new ArrayList<>(friendMapper.selectList(friendQueryWrapper_o));

        for (Friend friend_o : friendList) {
            FriendInfoMessage friendInfoMessage = new FriendInfoMessage();
            // 填写好友id
            friendInfoMessage.setId(friend_o.getSubjectId());
            User friendUser = userMapper.selectById(friend_o.getSubjectId());
            // 此用户还在，没有被注销
            if (friendUser != null) {

                friendInfoMessage.setNickname(friendUser.getNickname());
                friendInfoMessage.setMemo(friend_o.getMemo());

                // 查询自己对好友的关系
                String friendId = friendUser.getId();
                QueryWrapper<Friend> friendQueryWrapper_so = new QueryWrapper<>();
                friendQueryWrapper_so
                        .eq("subjectId", userId)
                        .eq("objectId", friendId);
                Friend friend_so = friendMapper.selectOne(friendQueryWrapper_so);

                switch (friend_o.getStatus()) {
                    case 0 -> friendInfoMessage.setCode(FIF_ADD.getId());
                    case 1 -> {
                        friendInfoMessage.setCode(FIF_ALREADY_ADD.getId());
                        if (friend_so != null) {
                            friendInfoMessage.setCustomNickname(friend_so.getCustomNickname());
                        }

                    }
                    case 2 -> friendInfoMessage.setCode(FIF_BLOCK.getId());
                    case 3 -> friendInfoMessage.setCode(FIF_REJECT.getId());
                }

                // 收集离线聊天记录
                QueryWrapper<Dialogue> dialogueQueryWrapper = new QueryWrapper<>();
                dialogueQueryWrapper
                        .eq("senderId", friendId)
                        .eq("receiverId", userId);
                List<Dialogue> dialogueList = dialogueMapper.selectList(dialogueQueryWrapper);
                for (Dialogue dialogue : dialogueList) {
                    dialogueMessageList.add(new DialogueMessage(
                            dialogue.getSenderId(),
                            dialogue.getReceiverId(),
                            dialogue.getContent(),
                            dialogue.getOccurredTime(),
                            dialogue.getLastedTime()));
                }
                // 聊天记录发完之后即删除
                dialogueMapper.delete(dialogueQueryWrapper);
            } else {
                friendInfoMessage.setCode(FIF_NOT_EXIST.getId());
            }
            friendInfoMessageList.add(friendInfoMessage);
        }

        QueryWrapper<Friend> friendQueryWrapper_s = new QueryWrapper<>();
        friendQueryWrapper_s
                .eq("subjectId", userId);
        friendList = new ArrayList<>(friendMapper.selectList(friendQueryWrapper_s));
        for (Friend friend_s : friendList) {
            QueryWrapper<Friend> friendQueryWrapper_os = new QueryWrapper<>();
            friendQueryWrapper_os
                    .eq("subjectId", friend_s.getObjectId())
                    .eq("objectId", userId);
            Friend friend_os = friendMapper.selectOne(friendQueryWrapper_os);
            if (friend_os == null) {
                FriendInfoMessage friendInfoMessage = new FriendInfoMessage();
                // 填写好友id
                friendInfoMessage.setId(friend_s.getObjectId());
                User friendUser = userMapper.selectById(friend_s.getObjectId());
                if (friendUser != null) {
                    friendInfoMessage.setNickname(friendUser.getNickname());
                }
                friendInfoMessage.setMemo(friend_s.getMemo());

                switch (friend_s.getStatus()) {
                    case 0 -> friendInfoMessage.setCode(FIF_REPEAT_ADD.getId());
                    case 1, 2, 3 -> friendInfoMessage.setCode(FIF_YOUR_SINGLE.getId());
                }

                friendInfoMessageList.add(friendInfoMessage);
            }
        }


        // 发送
        Dispatcher.sendMessage(session, new Message(new UserOnlineMessage(friendInfoMessageList, dialogueMessageList)));

    }

}
