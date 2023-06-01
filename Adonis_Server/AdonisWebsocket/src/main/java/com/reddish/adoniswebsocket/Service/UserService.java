package com.reddish.adoniswebsocket.Service;

import com.reddish.adoniswebsocket.FeignClient.DAOFeignClient;
import com.reddish.adoniswebsocket.Manager.SendManager;
import com.reddish.adoniswebsocket.Utils.DTO.User;
import com.reddish.adoniswebsocket.Utils.Exception.MessageException;
import com.reddish.adoniswebsocket.Utils.Exception.UserInfoException;
import com.reddish.adoniswebsocket.Utils.Message.MessageCode;
import com.reddish.adoniswebsocket.Utils.Message.UserOpMessage;
import com.reddish.adoniswebsocket.Websocket.Dispatcher;
import org.springframework.stereotype.Service;

import javax.websocket.Session;

import static com.reddish.adoniswebsocket.Utils.Exception.ExceptionCode.*;
import static com.reddish.adoniswebsocket.Utils.Message.MessageCode.*;

@Service
public class UserService {
    private final DAOFeignClient daoFeignClient;

    private final SendManager sendManager;

    public UserService(DAOFeignClient daoFeignClient, SendManager sendManager) {
        this.daoFeignClient = daoFeignClient;
        this.sendManager = sendManager;
    }

    public void handle(UserOpMessage userOpMessage, Session session) throws MessageException, UserInfoException {

        String userId = userOpMessage.getId();
        // 查看表中是否有该user
        User user = daoFeignClient.selectUserById(userId);

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
                // 存userId和Session的对应关系
                Dispatcher.setOnline(userId, session);
                uif = UIF_OP_SUCCESS;
            }

        } else if (UOP_SIGN_UP.equals(messageCode)) {
            // 已存在，不可注册
            if (user != null) {
                uif = UIF_ALREADY_EXIST;
            } else if (userId == null || userOpMessage.getNickname() == null || userOpMessage.getPassword() == null) {
                uif = UIF_INCOMPLETE_INFO;
            } else {
                daoFeignClient.createNewUser(userId, userOpMessage.getNickname(), userOpMessage.getPassword());
                // 默认自己是自己的好友
                daoFeignClient.createNewFriendship(userId, userId, 1, "","");
                uif = UIF_OP_SUCCESS;
            }
            // 校验消息内用户ID与session拥有用户ID是否一致
        } else if (!userId.equals(Dispatcher.getIdBySession(session))) {
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
                    daoFeignClient.deleteUser(userId);
                    uif = UIF_OP_SUCCESS;
                }
                case UOP_CHANGE_NICKNAME -> {
                    String nicknameNew = userOpMessage.getNickname();
                    if (nicknameNew == null) {
                        uif = UIF_NO_NICKNAME;
                    } else {
                        daoFeignClient.updateUserNickname(userId, nicknameNew);
                        uif = UIF_OP_SUCCESS;
                    }
                }
                case UOP_CHANGE_PASSWORD -> {
                    String passwordNew = userOpMessage.getPassword();
                    if (passwordNew == null) {
                        uif = UIF_NO_PASSWORD;
                    } else {
                        daoFeignClient.updateUserPassword(userId, passwordNew);
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
            sendManager.sendUserOnlineMessage(session, userId);
        }
    }
}
