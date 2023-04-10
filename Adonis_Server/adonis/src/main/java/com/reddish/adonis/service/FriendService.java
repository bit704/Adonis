package com.reddish.adonis.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.reddish.adonis.exception.ExceptionCode;
import com.reddish.adonis.exception.FriendInfoException;
import com.reddish.adonis.exception.MessageException;
import com.reddish.adonis.mapper.FriendMapper;
import com.reddish.adonis.mapper.UserMapper;
import com.reddish.adonis.mapper.entity.Friend;
import com.reddish.adonis.mapper.entity.User;
import com.reddish.adonis.service.entity.FriendInfoMessage;
import com.reddish.adonis.service.entity.FriendOpMessage;
import com.reddish.adonis.service.entity.Message;
import com.reddish.adonis.websocket.Dispatcher;
import org.springframework.stereotype.Service;

import javax.websocket.Session;


@Service
public class FriendService {

    private final UserMapper userMapper;
    private final FriendMapper friendMapper;

    public FriendService(UserMapper userMapper, FriendMapper friendMapper) {
        this.userMapper = userMapper;
        this.friendMapper = friendMapper;
    }

    // 发送好友消息
    public static void sendFriendInfoMessage(FriendInfoMessage friendInfoMessage, String objectId) {
        Message message = new Message(friendInfoMessage);
        Session session = Dispatcher.userId2SessionMap.get(objectId);
        Dispatcher.sendMessage(session, message);
    }


    public void handle(FriendOpMessage friendOpMessage, Session session) throws MessageException, FriendInfoException {
        // 查看表中是否有添加好友双方user
        String subjectId = friendOpMessage.getSubjectId();
        String objectId = friendOpMessage.getObjectId();
        String type = friendOpMessage.getType();

        User user_s = userMapper.selectById(subjectId);
        User user_o = userMapper.selectById(objectId);

        // 不判断user_o是否存在，因为对方可能已经注销
        if (user_s == null) {
            throw new FriendInfoException(ExceptionCode._301);
        }
        if (user_s.equals(user_o)) {
            throw new FriendInfoException(ExceptionCode._302);
        }

        QueryWrapper<Friend> queryWrapper_so = new QueryWrapper<>();
        queryWrapper_so
                .eq("subjectId", subjectId)
                .eq("objectId", objectId);
        Friend friend_so = friendMapper.selectOne(queryWrapper_so);

        QueryWrapper<Friend> queryWrapper_os = new QueryWrapper<>();
        queryWrapper_os
                .eq("subjectId", objectId)
                .eq("objectId", subjectId);
        Friend friend_os = friendMapper.selectOne(queryWrapper_os);

        switch (type) {
            case "add" -> {
                if (friend_so != null) {
                    // s申请将o加入好友列表
                    if (friend_so.getStatus() == 0) {
                        throw new FriendInfoException(ExceptionCode._303);
                    }
                    if (friend_so.getStatus() == 1) {
                        throw new FriendInfoException(ExceptionCode._304);
                    }
                    if (friend_so.getStatus() == 2) {
                        // 把拉黑状态删除
                        friendMapper.delete(queryWrapper_so);
                    }
                }
                if (friend_os != null) {
                    if (friend_os.getStatus() == 2) {
                        throw new FriendInfoException(ExceptionCode._305);
                    }
                }
                // 对方刚好在线，直接发给o
                if (Dispatcher.isOnline(objectId)) {
                    FriendInfoMessage friendInfoMessage = new FriendInfoMessage();
                    friendInfoMessage.setId(subjectId);
                    friendInfoMessage.setNickname(user_s.getNickname());
                    friendInfoMessage.setStatus(0);
                    friendInfoMessage.setMemo(friendOpMessage.getMemo());
                    sendFriendInfoMessage(friendInfoMessage, objectId);
                }
                // 否则先存在数据库里，状态为0
                else {
                    friendMapper.insert(new Friend(subjectId, objectId, 0, null, friendOpMessage.getMemo()));
                }
            }
            case "consent" -> {
                // s申请将o加入好友列表，o同意
                UpdateWrapper<Friend> updateWrapper_so = new UpdateWrapper<>();
                updateWrapper_so
                        .eq("subjectId", subjectId)
                        .eq("objectId", objectId)
                        .set("status", 1);
                // s将o加入好友列表
                friendMapper.update(null, updateWrapper_so);
                // o也将s加入好友列表
                // o没给s发过好友申请，或s不是o的好友
                if (friend_os == null) {
                    // s将o加入好友列表
                    friendMapper.insert(new Friend(objectId, subjectId, 1, null, friend_so.getMemo()));
                } else {
                    UpdateWrapper<Friend> updateWrapper_os = new UpdateWrapper<>();
                    updateWrapper_os
                            .eq("subjectId", objectId)
                            .eq("objectId", subjectId)
                            .set("status", 1);
                    // 如果o给s发过好友申请，更新为成功；如果s是o的好友，一样重复更新，不会造成影响
                    friendMapper.update(null, updateWrapper_os);
                }
                // 告知s
                if (Dispatcher.isOnline(subjectId)) {
                    FriendInfoMessage friendInfoMessage = new FriendInfoMessage();
                    friendInfoMessage.setId(subjectId);
                    friendInfoMessage.setNickname(user_s.getNickname());
                    friendInfoMessage.setStatus(1);
                    sendFriendInfoMessage(friendInfoMessage, subjectId);
                }
                // 不在线就不用告知了，待s登录将s的好友状态一并告诉s
            }
            case "refuse" -> {
                // s申请将o加入好友列表，o拒绝
                friendMapper.delete(queryWrapper_so);
                // 告知s
                if (Dispatcher.isOnline(subjectId)) {
                    FriendInfoMessage friendInfoMessage = new FriendInfoMessage();
                    friendInfoMessage.setId(subjectId);
                    friendInfoMessage.setNickname(user_s.getNickname());
                    friendInfoMessage.setStatus(3);
                    friendInfoMessage.setMemo(friendOpMessage.getMemo());
                    sendFriendInfoMessage(friendInfoMessage, subjectId);
                } else {
                    UpdateWrapper<Friend> updateWrapper_so = new UpdateWrapper<>();
                    updateWrapper_so
                            .eq("subjectId", subjectId)
                            .eq("objectId", objectId)
                            .set("status", 3);
                    friendMapper.update(null, updateWrapper_so);
                }
            }
            case "delete" -> {
                // 这个不用告知o，因为是单向删除，s还在o的好友列表中，只是不能发消息了
                // s申请将o从好友列表删除
                friendMapper.delete(queryWrapper_so);
            }
            case "exist" -> {
                User user = userMapper.selectById(objectId);

                FriendInfoMessage friendInfoMessage = new FriendInfoMessage();
                friendInfoMessage.setId(objectId);
                if (user != null) {
                    friendInfoMessage.setNickname(user.getNickname());
                    friendInfoMessage.setStatus(4);
                } else {
                    friendInfoMessage.setStatus(5);
                }
                sendFriendInfoMessage(friendInfoMessage, subjectId);

            }
            case "online" -> {
                FriendInfoMessage friendInfoMessage = new FriendInfoMessage();
                friendInfoMessage.setId(objectId);
                if (Dispatcher.isOnline(objectId)) {
                    friendInfoMessage.setStatus(6);
                } else {
                    friendInfoMessage.setStatus(7);
                }
                sendFriendInfoMessage(friendInfoMessage, subjectId);
            }
            case "block" -> {
                // 拉黑好友
                UpdateWrapper<Friend> updateWrapper_so = new UpdateWrapper<>();
                updateWrapper_so
                        .eq("subjectId", subjectId)
                        .eq("objectId", objectId)
                        .set("status", 2);
                friendMapper.update(null, updateWrapper_so);
            }
            case "custom" -> {
                // 更新对好友的自定义备注
                UpdateWrapper<Friend> updateWrapper_so = new UpdateWrapper<>();
                updateWrapper_so
                        .eq("subjectId", subjectId)
                        .eq("objectId", objectId)
                        .set("customNickname", friendOpMessage.getCustomNickname());
                friendMapper.update(null, updateWrapper_so);
            }
            default -> throw new MessageException(ExceptionCode._300);
        }
    }

}
