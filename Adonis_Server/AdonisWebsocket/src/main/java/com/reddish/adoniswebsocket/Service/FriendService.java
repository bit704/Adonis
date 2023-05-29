package com.reddish.adoniswebsocket.Service;

import com.reddish.adoniswebsocket.Utils.DTO.Friend;
import com.reddish.adoniswebsocket.Utils.DTO.User;
import com.reddish.adoniswebsocket.Utils.Exception.FriendInfoException;
import com.reddish.adoniswebsocket.Utils.Exception.MessageException;
import com.reddish.adoniswebsocket.FeignClient.DAOFeignClient;
import com.reddish.adoniswebsocket.Manager.SendManager;
import com.reddish.adoniswebsocket.Utils.Message.FriendOpMessage;
import com.reddish.adoniswebsocket.Utils.Message.MessageCode;
import com.reddish.adoniswebsocket.Websocket.Dispatcher;
import org.springframework.stereotype.Service;

import static com.reddish.adoniswebsocket.Utils.Exception.ExceptionCode.*;
import static com.reddish.adoniswebsocket.Utils.Message.MessageCode.*;


@Service
public class FriendService {

    private final DAOFeignClient daoFeignClient;
    private final SendManager sendManager;

    public FriendService(DAOFeignClient daoFeignClient, SendManager sendManager) {
        this.daoFeignClient = daoFeignClient;
        this.sendManager = sendManager;
    }

    public void handle(FriendOpMessage friendOpMessage) throws MessageException, FriendInfoException {

        int code = friendOpMessage.getCode();
        MessageCode messageCode = MessageCode.getCodeById(code);
        if (messageCode == null) {
            throw new MessageException(ILLEGAL_CODE);
        }

        // 查看表中是否有添加好友双方user
        String subjectId = friendOpMessage.getSubjectId();
        String objectId = friendOpMessage.getObjectId();
        User user_s = daoFeignClient.selectUserById(subjectId);
        User user_o = daoFeignClient.selectUserById(objectId);

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

        Friend friend_so = daoFeignClient.queryTheFriend(subjectId, objectId);
        Friend friend_os = daoFeignClient.queryTheFriend(objectId, subjectId);

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
                        daoFeignClient.deleteFriend(subjectId, objectId);
                        daoFeignClient.createNewFriendship(subjectId, objectId, 0, friendOpMessage.getCustomNickname(), friendOpMessage.getMemo());
                        // 已经申请过了，更新一下申请
                    } else if (friend_so.getStatus() == 0) {
                        daoFeignClient.updateFriendMemo(subjectId, objectId, friendOpMessage.getMemo());
                    }
                } else {
                    // 直接写入数据库
                    daoFeignClient.createNewFriendship(subjectId, objectId, 0, friendOpMessage.getCustomNickname(), friendOpMessage.getMemo());
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
                daoFeignClient.updateFriendStatus(objectId, subjectId, 1);

                // s将o加入好友列表
                if (friend_so == null) {
                    // o没给s发过好友申请，需要插入
                    daoFeignClient.createNewFriendship(subjectId, objectId, 1, "", "");
                } else {
                    // 已有申请，只需更新
                    daoFeignClient.updateFriendStatus(subjectId, objectId, 1);
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
                daoFeignClient.deleteFriend(objectId, subjectId);
                // 告知o，s拒绝
                if (Dispatcher.isOnline(objectId)) {
                    sendManager.sendFriendInfoForOp(friendOpMessage, FIF_REJECT, subjectId, objectId);
                } else {
                    // 存起来，等o上线后告知
                    daoFeignClient.updateFriendStatus(subjectId, objectId, 3);
                }
                sendManager.sendFriendInfoForOp(friendOpMessage, FIF_OP_SUCCESS, objectId, subjectId);

            }
            case FOP_DELETE -> {

                // 这个不用告知o，因为是单向删除，s还在o的好友列表中，只是o不能给s发消息了
                // s申请将o从好友列表删除
                daoFeignClient.deleteFriend(subjectId, objectId);
                sendManager.sendFriendInfoForOp(friendOpMessage, FIF_OP_SUCCESS, objectId, subjectId);

            }
            case FOP_QUERY_EXIST -> {

                User user = daoFeignClient.selectUserById(objectId);
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
                daoFeignClient.updateFriendStatus(subjectId, objectId, 2);
                sendManager.sendFriendInfoForOp(friendOpMessage, FIF_OP_SUCCESS, objectId, subjectId);

            }
            case FOP_CUSTOM_NICKNAME -> {

                // 更新对好友的自定义备注
                daoFeignClient.updateFriendCustomNickname(subjectId, objectId, friendOpMessage.getCustomNickname());
                sendManager.sendFriendInfoForOp(friendOpMessage, FIF_OP_SUCCESS, objectId, subjectId);

            }
            case FOP_QUERY_FRIENDSHIP -> {

                if (friend_so != null && friend_os != null &&
                        friend_so.getStatus() == 1 && friend_os.getStatus() == 1) {
                    sendManager.sendFriendInfoForOp(friendOpMessage, FIF_TWO_WAY, objectId, subjectId);
                } else if (friend_so != null && friend_so.getStatus() == 1) {
                    sendManager.sendFriendInfoForOp(friendOpMessage, FIF_SINGLE_FOR_YOU, objectId, subjectId);
                } else if (friend_os != null && friend_os.getStatus() == 1) {
                    sendManager.sendFriendInfoForOp(friendOpMessage, FIF_SINGLE_ON_YOU, objectId, subjectId);
                } else {
                    sendManager.sendFriendInfoForOp(friendOpMessage, FIF_FREE, objectId, subjectId);
                }

            }
            default -> throw new MessageException(ILLEGAL_FOP_CODE);
        }
    }

}
