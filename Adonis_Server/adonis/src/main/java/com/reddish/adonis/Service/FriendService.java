package com.reddish.adonis.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.reddish.adonis.Manager.DAOManager;
import com.reddish.adonis.Manager.Exception.FriendInfoException;
import com.reddish.adonis.Manager.Exception.MessageException;
import com.reddish.adonis.DAO.FriendMapper;
import com.reddish.adonis.DAO.UserMapper;
import com.reddish.adonis.DO.Friend;
import com.reddish.adonis.DO.User;
import com.reddish.adonis.AO.FriendInfoMessage;
import com.reddish.adonis.AO.FriendOpMessage;
import com.reddish.adonis.AO.Message;
import com.reddish.adonis.AO.MessageCode;
import com.reddish.adonis.Manager.SendManager;
import com.reddish.adonis.Websocket.Dispatcher;
import org.springframework.stereotype.Service;

import javax.websocket.Session;

import static com.reddish.adonis.Manager.Exception.ExceptionCode.*;
import static com.reddish.adonis.AO.MessageCode.*;


@Service
public class FriendService {

    private final UserMapper userMapper;
    private final FriendMapper friendMapper;

    private final DAOManager DAOManager;
    private final SendManager sendManager;

    public FriendService(UserMapper userMapper, FriendMapper friendMapper, DAOManager DAOManager, SendManager sendManager) {
        this.userMapper = userMapper;
        this.friendMapper = friendMapper;
        this.DAOManager = DAOManager;
        this.sendManager = sendManager;
    }


    private void sendInfoForOp(FriendOpMessage friendOpMessage, MessageCode messageCode, String infoId, String send2Id) {
        FriendInfoMessage friendInfoMessage = new FriendInfoMessage();

        // 消息代码
        friendInfoMessage.setCode(messageCode.getId());

        // 此消息要说明的用户
        friendInfoMessage.setId(infoId);

        // friendOpMessage指定要操作的用户
        User user = userMapper.selectById(friendOpMessage.getObjectId());
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

    private void updateMemo(String subjectId, String objectId, String memo) {
        UpdateWrapper<Friend> updateWrapper = new UpdateWrapper<>();
        updateWrapper
                .eq("subjectId", subjectId)
                .eq("objectId", objectId)
                .set("memo", memo);
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
            throw new MessageException(ILLEGAL_CODE);
        }

        // 查看表中是否有添加好友双方user
        String subjectId = friendOpMessage.getSubjectId();
        String objectId = friendOpMessage.getObjectId();
        User user_s = userMapper.selectById(subjectId);
        User user_o = userMapper.selectById(objectId);

        // 不判断user_o是否存在，因为对方可能已经注销
        if (user_s == null) {
            throw new FriendInfoException(EXTREME_ERROR);
        }
        if (user_s.equals(user_o)
                && !FOP_QUERY_EXIST.equals(messageCode)
                && !FOP_QUERY_ONLINE.equals(messageCode)
        ) {
            throw new FriendInfoException(NOT_SELF);
        }

        Friend friend_so = queryFriend(subjectId, objectId);
        Friend friend_os = queryFriend(objectId, subjectId);

        switch (messageCode) {
            case FOP_ADD -> {

                // 被对方拉黑了
                if (friend_os != null && friend_os.getStatus() == 2) {
                    sendInfoForOp(friendOpMessage, FIF_BLOCK, objectId, subjectId);
                    return;
                }

                if (friend_so != null) {
                    // s申请将o加入好友列表
                    if (friend_so.getStatus() == 1) {
                        sendInfoForOp(friendOpMessage, FIF_ADD_CONSENT, objectId, subjectId);
                        return;
                    } else if (friend_so.getStatus() == 2 || friend_so.getStatus() == 3) {
                        // 拉黑了对方，又重新给对方发送好友申请，自动把拉黑状态删除
                        // 因此先删除好友关系
                        // 或被对方拒绝，又重新给对方发送好友申请
                        // 也要先删除好友关系
                        deleteFriend(subjectId, objectId);
                        friendMapper.insert(new Friend(subjectId, objectId, 0, friendOpMessage.getCustomNickname(), friendOpMessage.getMemo()));
                        // 已经申请过了，更新一下申请
                    } else if (friend_so.getStatus() == 0) {
                        updateMemo(subjectId, objectId, friendOpMessage.getMemo());
                    }
                } else {
                    // 直接写入数据库
                    friendMapper.insert(new Friend(subjectId, objectId, 0, friendOpMessage.getCustomNickname(), friendOpMessage.getMemo()));
                }

                // 对方刚好在线，直接发给o
                if (Dispatcher.isOnline(objectId)) {
                    sendInfoForOp(friendOpMessage, FIF_ADD_YOU, subjectId, objectId);
                }

                sendInfoForOp(friendOpMessage, FIF_OP_SUCCESS, objectId, subjectId);

            }
            case FOP_CONSENT -> {

                // o申请将s加入好友列表，s同意
                // o对s的申请改为o和s已是好友
                updateStatus(objectId, subjectId, 1);

                // s将o加入好友列表
                if (friend_so == null) {
                    // o没给s发过好友申请，需要插入
                    friendMapper.insert(new Friend(subjectId, objectId, 1, null, null));
                } else {
                    // 已有申请，只需更新
                    updateStatus(subjectId, objectId, 1);
                }

                // o刚好在线，告知o，s已经同意
                if (Dispatcher.isOnline(objectId)) {
                    sendInfoForOp(friendOpMessage, FIF_ADD_CONSENT, subjectId, objectId);
                }

                // 同意之后给双方发送系统默认消息
                sendManager.sendDialogue(subjectId, objectId,
                        "我们已经是好友了，快来聊天吧！", System.currentTimeMillis(), 0);
                sendManager.sendDialogue(objectId, subjectId,
                        "我们已经是好友了，快来聊天吧！", System.currentTimeMillis(), 0);

                sendInfoForOp(friendOpMessage, FIF_OP_SUCCESS, objectId, subjectId);

            }
            case FOP_REJECT -> {

                // o申请将s加入好友列表，s拒绝
                deleteFriend(objectId, subjectId);
                // 告知o，s拒绝
                if (Dispatcher.isOnline(objectId)) {
                    sendInfoForOp(friendOpMessage, FIF_REJECT, subjectId, objectId);
                } else {
                    // 存起来，等o上线后告知
                    updateStatus(subjectId, objectId, 3);
                }
                sendInfoForOp(friendOpMessage, FIF_OP_SUCCESS, objectId, subjectId);

            }
            case FOP_DELETE -> {

                // 这个不用告知o，因为是单向删除，s还在o的好友列表中，只是o不能给s发消息了
                // s申请将o从好友列表删除
                deleteFriend(subjectId, objectId);
                sendInfoForOp(friendOpMessage, FIF_OP_SUCCESS, objectId, subjectId);

            }
            case FOP_QUERY_EXIST -> {

                User user = userMapper.selectById(objectId);
                if (user != null) {
                    sendInfoForOp(friendOpMessage, FIF_EXIST, objectId, subjectId);
                } else {
                    sendInfoForOp(friendOpMessage, FIF_NOT_EXIST, objectId, subjectId);
                }

            }
            case FOP_QUERY_ONLINE -> {

                if (Dispatcher.isOnline(objectId)) {
                    sendInfoForOp(friendOpMessage, FIF_ONLINE, objectId, subjectId);
                } else {
                    sendInfoForOp(friendOpMessage, FIF_OFFLINE, objectId, subjectId);
                }

            }
            case FOP_BLOCK -> {

                // 拉黑好友
                updateStatus(subjectId, objectId, 2);
                sendInfoForOp(friendOpMessage, FIF_OP_SUCCESS, objectId, subjectId);

            }
            case FOP_CUSTOM_NICKNAME -> {

                // 更新对好友的自定义备注
                updateCustomNickname(subjectId, objectId, friendOpMessage.getCustomNickname());
                sendInfoForOp(friendOpMessage, FIF_OP_SUCCESS, objectId, subjectId);

            }
            case FOP_QUERY_FRIENDSHIP -> {

                if (friend_so.getStatus() == 1 && friend_os.getStatus() == 1) {
                    sendInfoForOp(friendOpMessage, FIF_TWO_WAY, objectId, subjectId);
                } else if (friend_so.getStatus() == 1) {
                    sendInfoForOp(friendOpMessage, FIF_SINGLE_FOR_YOU, objectId, subjectId);
                } else if (friend_os.getStatus() == 1) {
                    sendInfoForOp(friendOpMessage, FIF_SINGLE_ON_YOU, objectId, subjectId);
                } else {
                    sendInfoForOp(friendOpMessage, FIF_FREE, objectId, subjectId);
                }

            }
            default -> throw new MessageException(ILLEGAL_FOP_CODE);
        }
    }

}
