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
import com.reddish.adonis.service.entity.Message;
import com.reddish.adonis.websocket.Dispatcher;
import org.springframework.stereotype.Service;

import javax.websocket.Session;


@Service
public class FriendInfoService {

    private final UserMapper userMapper;
    private final FriendMapper friendMapper;

    public FriendInfoService(UserMapper userMapper, FriendMapper friendMapper) {
        this.userMapper = userMapper;
        this.friendMapper = friendMapper;
    }

    // 发送好友消息
    public static void sendFriendInfoMessage(FriendInfoMessage friendInfoMessage, String objectId) {
        Message message = new Message();
        message.setType("FriendInfoMessage");
        message.setFriendInfoMessage(friendInfoMessage);
        Session session = Dispatcher.userId2SessionMap.get(objectId);
        Dispatcher.sendMessage(session, message);
    }

    public static void sendFriendInfoMessage(FriendInfoMessage friendInfoMessage, Session session) {
        Message message = new Message();
        message.setType("FriendInfoMessage");
        message.setFriendInfoMessage(friendInfoMessage);
        Dispatcher.sendMessage(session, message);
    }


    public void handle(FriendInfoMessage friendInfoMessage, Session session) throws MessageException, FriendInfoException {
        // 查看表中是否有添加好友双方user
        String subjectId = friendInfoMessage.getSubjectId();
        String objectId = friendInfoMessage.getObjectId();
        User user1 = userMapper.selectById(subjectId);
        User user2 = userMapper.selectById(objectId);
        if (user1 == null || user2 == null) {
            throw new FriendInfoException(ExceptionCode._301);
        }
        if (user1.equals(user2)) {
            throw new FriendInfoException(ExceptionCode._302);
        }

        QueryWrapper<Friend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("subjectId", friendInfoMessage.getSubjectId()).eq("objectId", friendInfoMessage.getObjectId());
        Friend friend = friendMapper.selectOne(queryWrapper);

        String type = friendInfoMessage.getType();
        switch (type) {
            case "add" -> {
                // s申请将o加入好友列表
                if (friend.getStatus() == 0) {
                    throw new FriendInfoException(ExceptionCode._303);
                }
                if (friend.getStatus() == 1) {
                    throw new FriendInfoException(ExceptionCode._304);
                }
                // 对方刚好在线,直接发给o
                if (Dispatcher.onlineMap.get(objectId)) {
                    sendFriendInfoMessage(friendInfoMessage, objectId);
                }
                // 否则先存在数据库里
                else {
                    friendMapper.insert(new Friend(subjectId, objectId, 0, friendInfoMessage.getMemo()));
                }
            }
            case "consent" -> {
                // s申请将o加入好友列表，o同意
                UpdateWrapper<Friend> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("subjectId", friendInfoMessage.getSubjectId()).eq("objectId", friendInfoMessage.getObjectId())
                        .set("status", 1);
                // s将o加入好友列表
                friendMapper.update(null, updateWrapper);
                // o也将s加入好友列表
                friendMapper.insert(new Friend(objectId, subjectId, 1, friend.getMemo()));
            }
            case "refuse", "delete" -> {
                // s申请将o加入好友列表，o拒绝
                // 或：s申请将o从好友列表删除
                friendMapper.delete(queryWrapper);
                // 只能单向删除，s还在o的好友列表中，只是不能发消息了
            }
            default -> throw new MessageException(ExceptionCode._300);
        }

    }
}
