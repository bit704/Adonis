package com.reddish.adonis.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.reddish.adonis.exception.MessageException;
import com.reddish.adonis.exception.UserInfoException;
import com.reddish.adonis.mapper.DialogueMapper;
import com.reddish.adonis.mapper.FriendMapper;
import com.reddish.adonis.mapper.UserMapper;
import com.reddish.adonis.mapper.entity.Dialogue;
import com.reddish.adonis.mapper.entity.Friend;
import com.reddish.adonis.mapper.entity.User;
import com.reddish.adonis.service.entity.*;
import com.reddish.adonis.websocket.Dispatcher;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;

import static com.reddish.adonis.exception.ExceptionCode.*;
import static com.reddish.adonis.service.entity.MessageCode.*;

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
            throw new MessageException(illegal_code);
        }

        if (uop_sign_in.equals(messageCode)) {
            // 登录
            if (user == null) {
                sendInfoForOp(uif_not_exist, session);
                return;
            }

            if (!user.getPassword().equals(userOpMessage.getPassword())) {
                sendInfoForOp(uif_worry_password, session);
                return;
            }
            // 标记为在线
            Dispatcher.setOnline(userId);
            // 存userId和Session的对应关系
            Dispatcher.userId2SessionMap.put(userId, session);
            Dispatcher.session2UserIdMap.put(session, userId);

        } else if (uop_sign_up.equals(messageCode)) {
            // 已存在，不可注册
            if (user != null) {
                sendInfoForOp(uif_already_exist, session);
                return;
            }
            if (userId == null || userOpMessage.getNickname() == null || userOpMessage.getPassword() == null) {
                sendInfoForOp(uif_incomplete_info, session);
                return;
            }
            userMapper.insert(new User(userId, userOpMessage.getNickname(), userOpMessage.getPassword()));
            // 默认自己是自己的好友
            friendMapper.insert(new Friend(userId, userId, 1, null, null));
        } else {
            // 校验消息内用户ID与session拥有用户ID是否一致
            if (!Dispatcher.session2UserIdMap.get(session).equals(userId)) {
                throw new UserInfoException(hack);
            }
            // 校验用户是否在线
            if (!Dispatcher.isOnline(userId)) {
                sendInfoForOp(uif_offline, session);
                return;
            }

            switch (messageCode) {
                case uop_sign_out -> {
                    // 标记为离线
                    Dispatcher.setOffline(userId);
                }
                case uop_delete -> {
                    userMapper.deleteById(userId);
                }
                case uop_change_nickname -> {
                    UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
                    String nickname_new = userOpMessage.getNickname();
                    if (nickname_new == null) {
                        sendInfoForOp(uif_no_nickname, session);
                        return;
                    }
                    updateWrapper.eq("id", userId).set("nickname", nickname_new);
                    userMapper.update(null, updateWrapper);
                }
                case uop_change_password -> {
                    UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
                    String password_new = userOpMessage.getPassword();
                    if (password_new == null) {
                        sendInfoForOp(uif_no_password, session);
                        return;
                    }
                    updateWrapper.eq("id", userId).set("password", password_new);
                    userMapper.update(null, updateWrapper);
                }
                case uop_request_online_message -> {
                    // 请求了再发送
                    sendUserOnlineMessage(session, userId);
                }
                default -> throw new MessageException(illegal_uop_code);
            }
        }
    }

    public void sendUserOnlineMessage(Session session, String userId) {

        List<FriendInfoMessage> friendInfoMessageList = new ArrayList<>();
        List<DialogueInfoMessage> dialogueInfoMessageList = new ArrayList<>();

        QueryWrapper<Friend> friendQueryWrapper = new QueryWrapper<>();
        friendQueryWrapper.eq("subjectId", userId);
        List<Friend> friendList = friendMapper.selectList(friendQueryWrapper);

        for (Friend friend : friendList) {
            FriendInfoMessage friendInfoMessage = new FriendInfoMessage();
            // 填写好友id
            friendInfoMessage.setId(friend.getObjectId());
            User friendUser = userMapper.selectById(friend.getObjectId());
            // 此用户还在，没有被注销
            if (friendUser != null) {
                friendInfoMessage.setNickname(friendUser.getNickname());
                friendInfoMessage.setCustomNickname(friend.getCustomNickname());
                friendInfoMessage.setMemo(friend.getMemo());

                // 查询好友对自己的关系，填写status
                String friendId = friendUser.getId();
                QueryWrapper<Friend> friendQueryWrapper_os = new QueryWrapper<>();
                friendQueryWrapper_os
                        .eq("subjectId", friendId)
                        .eq("objectId", userId);
                Friend friend_os = friendMapper.selectOne(friendQueryWrapper_os);
                if (friend_os != null) {
                    // 填写好友状态
                    switch (friend_os.getStatus()) {
                        case 0 -> {
                            friendInfoMessage.setCode(fif_add.getId());
                        }
                        case 1 -> {
                            friendInfoMessage.setCode(fif_already_add.getId());
                        }
                        case 2 -> {
                            friendInfoMessage.setCode(fif_block.getId());
                        }
                        case 3 -> {
                            friendInfoMessage.setCode(fif_reject.getId());
                        }
                    }
                } else {
                    friendInfoMessage.setCode(fif_your_single.getId());
                }

                // 收集离线聊天记录
                QueryWrapper<Dialogue> dialogueQueryWrapper = new QueryWrapper<>();
                dialogueQueryWrapper
                        .eq("senderId", friendId)
                        .eq("receiverId", userId);
                List<Dialogue> dialogueList = dialogueMapper.selectList(dialogueQueryWrapper);
                for (Dialogue dialogue : dialogueList) {
                    dialogueInfoMessageList.add(new DialogueInfoMessage(
                            dialogue.getSenderId(),
                            dialogue.getReceiverId(),
                            dialogue.getContent(),
                            dialogue.getOccurredTime(),
                            dialogue.getLastedTime()));
                }
            } else {
                friendInfoMessage.setCode(fif_not_exist.getId());
            }
            friendInfoMessageList.add(friendInfoMessage);
        }
        // 发送
        Dispatcher.sendMessage(session, new Message(new UserOnlineMessage(friendInfoMessageList, dialogueInfoMessageList)));

    }

}
