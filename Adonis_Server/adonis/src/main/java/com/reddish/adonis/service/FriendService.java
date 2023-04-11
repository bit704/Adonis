package com.reddish.adonis.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.reddish.adonis.exception.FriendInfoException;
import com.reddish.adonis.exception.MessageException;
import com.reddish.adonis.mapper.FriendMapper;
import com.reddish.adonis.mapper.UserMapper;
import com.reddish.adonis.mapper.entity.Friend;
import com.reddish.adonis.mapper.entity.User;
import com.reddish.adonis.service.entity.FriendInfoMessage;
import com.reddish.adonis.service.entity.FriendOpMessage;
import com.reddish.adonis.service.entity.Message;
import com.reddish.adonis.service.entity.MessageCode;
import com.reddish.adonis.websocket.Dispatcher;
import org.springframework.stereotype.Service;

import javax.websocket.Session;

import static com.reddish.adonis.exception.ExceptionCode.*;
import static com.reddish.adonis.service.entity.MessageCode.*;


@Service
public class FriendService {

    private final UserMapper userMapper;
    private final FriendMapper friendMapper;

    public FriendService(UserMapper userMapper, FriendMapper friendMapper) {
        this.userMapper = userMapper;
        this.friendMapper = friendMapper;
    }


    private void sendInfoForOp(FriendOpMessage friendOpMessage, MessageCode messageCode, String infoId, String send2Id) {
        FriendInfoMessage friendInfoMessage = new FriendInfoMessage();
        friendInfoMessage.setCode(messageCode.getId());
        friendInfoMessage.setId(infoId);
        // 发出friendOpMessage的用户
        User user = userMapper.selectById(friendOpMessage.getSubjectId());
        friendInfoMessage.setNickname(user.getNickname());
        // 此字段在发送此类回复时其实没有用
        friendInfoMessage.setCustomNickname(friendOpMessage.getCustomNickname());
        friendInfoMessage.setMemo(friendOpMessage.getMemo());

        Message message = new Message(friendInfoMessage);
        Session session = Dispatcher.userId2SessionMap.get(send2Id);
        Dispatcher.sendMessage(session, message);
    }

    private void updateStatus(String subjectId, String objectId, int status) {
        UpdateWrapper<Friend> updateWrapper = new UpdateWrapper<>();
        updateWrapper
                .eq("subjectId", subjectId)
                .eq("objectId", objectId)
                .set("status", status);
        friendMapper.update(null, updateWrapper);
    }

    private void updateCustomNickname(String subjectId, String objectId, String customNickname) {
        UpdateWrapper<Friend> updateWrapper = new UpdateWrapper<>();
        updateWrapper
                .eq("subjectId", subjectId)
                .eq("objectId", objectId)
                .set("customNickname", customNickname);
        friendMapper.update(null, updateWrapper);
    }

    private Friend queryFriend(String subjectId, String objectId) {
        QueryWrapper<Friend> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("subjectId", subjectId)
                .eq("objectId", objectId);
        return friendMapper.selectOne(queryWrapper);
    }

    private void deleteFriend(String subjectId, String objectId) {
        QueryWrapper<Friend> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("subjectId", subjectId)
                .eq("objectId", objectId);
        friendMapper.delete(queryWrapper);
    }


    public void handle(FriendOpMessage friendOpMessage, Session session) throws MessageException, FriendInfoException {

        int code = friendOpMessage.getCode();
        MessageCode messageCode = MessageCode.getCodeById(code);
        if (messageCode == null) {
            throw new MessageException(illegal_code);
        }

        // 查看表中是否有添加好友双方user
        String subjectId = friendOpMessage.getSubjectId();
        String objectId = friendOpMessage.getObjectId();
        User user_s = userMapper.selectById(subjectId);
        User user_o = userMapper.selectById(objectId);

        // 不判断user_o是否存在，因为对方可能已经注销
        if (user_s == null) {
            throw new FriendInfoException(extreme_error);
        }
        if (user_s.equals(user_o)) {
            throw new FriendInfoException(not_self);
        }

        Friend friend_so = queryFriend(subjectId, objectId);
        Friend friend_os = queryFriend(objectId, subjectId);

        switch (messageCode) {
            case fop_add -> {
                if (friend_so != null) {
                    // s申请将o加入好友列表
                    if (friend_so.getStatus() == 0) {
                        sendInfoForOp(friendOpMessage, fif_repeat_add, objectId, subjectId);
                        return;
                    } else if (friend_so.getStatus() == 1) {
                        sendInfoForOp(friendOpMessage, fif_already_add, objectId, subjectId);
                        return;
                    } else if (friend_so.getStatus() == 2) {
                        // 拉黑了对方，又重新给对方发送好友申请，自动把拉黑状态删除
                        // 因此先删除好友关系
                        deleteFriend(subjectId, objectId);
                    }
                }
                // 被拉黑了
                if (friend_os != null && friend_os.getStatus() == 2) {
                    sendInfoForOp(friendOpMessage, fif_block, objectId, subjectId);
                    return;
                }
                // 对方刚好在线，直接发给o
                if (Dispatcher.isOnline(objectId)) {
                    sendInfoForOp(friendOpMessage, fif_add, subjectId, objectId);
                }
                // 否则先存在数据库里，状态为0
                else {
                    friendMapper.insert(new Friend(subjectId, objectId, 0, null, friendOpMessage.getMemo()));
                }
            }
            case fop_consent -> {
                // s申请将o加入好友列表，o同意
                // s将o加入好友列表，将申请状态更新为同意状态
                updateStatus(subjectId, objectId, 1);

                // o也将s加入好友列表
                if (friend_os == null) {
                    // o没给s发过好友申请，需要插入
                    friendMapper.insert(new Friend(objectId, subjectId, 1, null, friend_so.getMemo()));
                } else {
                    // 只需更新
                    updateStatus(objectId, subjectId, 1);
                }
                // 告知s，o已经同意
                if (Dispatcher.isOnline(subjectId)) {
                    sendInfoForOp(friendOpMessage, fif_already_add, subjectId, objectId);
                }
                // 不在线就不用告知了，待s登录将s的好友状态一并告诉s
            }
            case fop_reject -> {
                // s申请将o加入好友列表，o拒绝
                deleteFriend(subjectId, objectId);
                // 告知s，o拒绝
                if (Dispatcher.isOnline(subjectId)) {
                    sendInfoForOp(friendOpMessage, fif_reject, objectId, subjectId);
                } else {
                    // 存起来，等o上线后告知
                    updateStatus(subjectId, objectId, 3);
                }
            }
            case fop_delete -> {
                // 这个不用告知o，因为是单向删除，s还在o的好友列表中，只是不能发消息了
                // s申请将o从好友列表删除
                deleteFriend(subjectId, objectId);
            }
            case fop_query_exist -> {
                User user = userMapper.selectById(objectId);
                if (user != null) {
                    sendInfoForOp(friendOpMessage, fif_exist, objectId, subjectId);
                } else {
                    sendInfoForOp(friendOpMessage, fif_not_exist, objectId, subjectId);
                }
            }
            case fop_query_online -> {
                if (Dispatcher.isOnline(objectId)) {
                    sendInfoForOp(friendOpMessage, fif_online, objectId, subjectId);
                } else {
                    sendInfoForOp(friendOpMessage, fif_offline, objectId, subjectId);
                }
            }
            case fop_block -> {
                // 拉黑好友
                updateStatus(subjectId, objectId, 2);
            }
            case fop_custom_nickname -> {
                // 更新对好友的自定义备注
                updateCustomNickname(subjectId, objectId, friendOpMessage.getCustomNickname());
            }
            case fop_query_friendship -> {
                if (friend_so.getStatus() == 1 && friend_os.getStatus() == 1) {
                    sendInfoForOp(friendOpMessage, fif_two_way, objectId, subjectId);
                } else {
                    sendInfoForOp(friendOpMessage, fif_not_two_way, objectId, subjectId);
                }
            }
            default -> throw new MessageException(illegal_fop_code);
        }
    }

}
