package com.reddish.adonisbase.Service;

import com.reddish.adonisbase.Message.FriendOpMessage;
import com.reddish.adonisbase.Message.MessageCode;
import com.reddish.adonisbase.DO.Friend;
import com.reddish.adonisbase.DO.User;
import com.reddish.adonisbase.Manager.DAOManager;
import com.reddish.adonisbase.Manager.Exception.FriendInfoException;
import com.reddish.adonisbase.Manager.Exception.MessageException;
import com.reddish.adonisbase.Manager.SendManager;
import com.reddish.adonisbase.Websocket.Dispatcher;
import org.springframework.stereotype.Service;

import javax.websocket.Session;

import static com.reddish.adonisbase.Message.MessageCode.*;
import static com.reddish.adonisbase.Manager.Exception.ExceptionCode.*;


@Service
public class FriendService {

    private final DAOManager daoManager;
    private final SendManager sendManager;

    public FriendService(DAOManager daoManager, SendManager sendManager) {
        this.daoManager = daoManager;
        this.sendManager = sendManager;
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
        User user_s = daoManager.selectUserById(subjectId);
        User user_o = daoManager.selectUserById(objectId);

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

        Friend friend_so = daoManager.queryTheFriend(subjectId, objectId);
        Friend friend_os = daoManager.queryTheFriend(objectId, subjectId);

        switch (messageCode) {
            case FOP_ADD -> {

                // 被对方拉黑了
                if (friend_os != null && friend_os.getStatus() == 2) {
                    sendManager.sendFriendInfoForOp(friendOpMessage, FIF_BLOCK, objectId, subjectId);
                    return;
                }

                if (friend_so != null) {
                    // s申请将o加入好友列表
                    if (friend_so.getStatus() == 1) {
                        sendManager.sendFriendInfoForOp(friendOpMessage, FIF_ADD_CONSENT, objectId, subjectId);
                        return;
                    } else if (friend_so.getStatus() == 2 || friend_so.getStatus() == 3) {
                        // 拉黑了对方，又重新给对方发送好友申请，自动把拉黑状态删除
                        // 因此先删除好友关系
                        // 或被对方拒绝，又重新给对方发送好友申请
                        // 也要先删除好友关系
                        daoManager.deleteFriend(subjectId, objectId);
                        daoManager.createNewFriendship(subjectId, objectId, 0, friendOpMessage.getCustomNickname(), friendOpMessage.getMemo());
                        // 已经申请过了，更新一下申请
                    } else if (friend_so.getStatus() == 0) {
                        daoManager.updateFriendMemo(subjectId, objectId, friendOpMessage.getMemo());
                    }
                } else {
                    // 直接写入数据库
                    daoManager.createNewFriendship(subjectId, objectId, 0, friendOpMessage.getCustomNickname(), friendOpMessage.getMemo());
                }

                // 对方刚好在线，直接发给o
                if (Dispatcher.isOnline(objectId)) {
                    sendManager.sendFriendInfoForOp(friendOpMessage, FIF_ADD_YOU, subjectId, objectId);
                }

                sendManager.sendFriendInfoForOp(friendOpMessage, FIF_OP_SUCCESS, objectId, subjectId);

            }
            case FOP_CONSENT -> {

                // o申请将s加入好友列表，s同意
                // o对s的申请改为o和s已是好友
                daoManager.updateFriendStatus(objectId, subjectId, 1);

                // s将o加入好友列表
                if (friend_so == null) {
                    // o没给s发过好友申请，需要插入
                    daoManager.createNewFriendship(subjectId, objectId, 1, null, null);
                } else {
                    // 已有申请，只需更新
                    daoManager.updateFriendStatus(subjectId, objectId, 1);
                }

                // o刚好在线，告知o，s已经同意
                if (Dispatcher.isOnline(objectId)) {
                    sendManager.sendFriendInfoForOp(friendOpMessage, FIF_ADD_CONSENT, subjectId, objectId);
                }

                // 同意之后给双方发送系统默认消息
                sendManager.sendDialogue(subjectId, objectId,
                        "我们已经是好友了，快来聊天吧！", System.currentTimeMillis(), 0);
                sendManager.sendDialogue(objectId, subjectId,
                        "我们已经是好友了，快来聊天吧！", System.currentTimeMillis(), 0);

                sendManager.sendFriendInfoForOp(friendOpMessage, FIF_OP_SUCCESS, objectId, subjectId);

            }
            case FOP_REJECT -> {

                // o申请将s加入好友列表，s拒绝
                daoManager.deleteFriend(objectId, subjectId);
                // 告知o，s拒绝
                if (Dispatcher.isOnline(objectId)) {
                    sendManager.sendFriendInfoForOp(friendOpMessage, FIF_REJECT, subjectId, objectId);
                } else {
                    // 存起来，等o上线后告知
                    daoManager.updateFriendStatus(subjectId, objectId, 3);
                }
                sendManager.sendFriendInfoForOp(friendOpMessage, FIF_OP_SUCCESS, objectId, subjectId);

            }
            case FOP_DELETE -> {

                // 这个不用告知o，因为是单向删除，s还在o的好友列表中，只是o不能给s发消息了
                // s申请将o从好友列表删除
                daoManager.deleteFriend(subjectId, objectId);
                sendManager.sendFriendInfoForOp(friendOpMessage, FIF_OP_SUCCESS, objectId, subjectId);

            }
            case FOP_QUERY_EXIST -> {

                User user = daoManager.selectUserById(objectId);
                if (user != null) {
                    sendManager.sendFriendInfoForOp(friendOpMessage, FIF_EXIST, objectId, subjectId);
                } else {
                    sendManager.sendFriendInfoForOp(friendOpMessage, FIF_NOT_EXIST, objectId, subjectId);
                }

            }
            case FOP_QUERY_ONLINE -> {

                if (Dispatcher.isOnline(objectId)) {
                    sendManager.sendFriendInfoForOp(friendOpMessage, FIF_ONLINE, objectId, subjectId);
                } else {
                    sendManager.sendFriendInfoForOp(friendOpMessage, FIF_OFFLINE, objectId, subjectId);
                }

            }
            case FOP_BLOCK -> {

                // 拉黑好友
                daoManager.updateFriendStatus(subjectId, objectId, 2);
                sendManager.sendFriendInfoForOp(friendOpMessage, FIF_OP_SUCCESS, objectId, subjectId);

            }
            case FOP_CUSTOM_NICKNAME -> {

                // 更新对好友的自定义备注
                daoManager.updateFriendCustomNickname(subjectId, objectId, friendOpMessage.getCustomNickname());
                sendManager.sendFriendInfoForOp(friendOpMessage, FIF_OP_SUCCESS, objectId, subjectId);

            }
            case FOP_QUERY_FRIENDSHIP -> {

                if (friend_so.getStatus() == 1 && friend_os.getStatus() == 1) {
                    sendManager.sendFriendInfoForOp(friendOpMessage, FIF_TWO_WAY, objectId, subjectId);
                } else if (friend_so.getStatus() == 1) {
                    sendManager.sendFriendInfoForOp(friendOpMessage, FIF_SINGLE_FOR_YOU, objectId, subjectId);
                } else if (friend_os.getStatus() == 1) {
                    sendManager.sendFriendInfoForOp(friendOpMessage, FIF_SINGLE_ON_YOU, objectId, subjectId);
                } else {
                    sendManager.sendFriendInfoForOp(friendOpMessage, FIF_FREE, objectId, subjectId);
                }

            }
            default -> throw new MessageException(ILLEGAL_FOP_CODE);
        }
    }

}
