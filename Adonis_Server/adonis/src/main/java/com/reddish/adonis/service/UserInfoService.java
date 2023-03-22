package com.reddish.adonis.service;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.reddish.adonis.exception.ExceptionCode;
import com.reddish.adonis.exception.MessageException;
import com.reddish.adonis.exception.UserInfoException;
import com.reddish.adonis.mapper.UserMapper;
import com.reddish.adonis.mapper.entity.User;
import com.reddish.adonis.service.entity.UserInfoMessage;
import com.reddish.adonis.websocket.Dispatcher;
import org.springframework.stereotype.Service;

import javax.websocket.Session;

@Service
public class UserInfoService {

    private final UserMapper userMapper;

    public UserInfoService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }


    public void handle(UserInfoMessage userInfoMessage, Session session) throws MessageException, UserInfoException {

        // 查看表中是否有该user
        User user = userMapper.selectById(userInfoMessage.getId());

        String type = userInfoMessage.getType();

        if ("sign_in".equals(type)) {
            if (user == null) {
                throw new UserInfoException(ExceptionCode._202);
            }

            if (!user.getPassword().equals(userInfoMessage.getPassword())) {
                throw new UserInfoException(ExceptionCode._203);
            }
            // 标记为在线
            Dispatcher.onlineMap.put(userInfoMessage.getId(), true);
            // 存userId和Session的对应关系
            Dispatcher.userId2SessionMap.put(userInfoMessage.getId(), session);
            Dispatcher.session2UserIdMap.put(session, userInfoMessage.getId());
        } else if ("sign_up".equals(type)) {
            // 已存在，不可注册
            if (user != null) {
                throw new UserInfoException(ExceptionCode._201);
            }
            if (userInfoMessage.getId() == null || userInfoMessage.getNickname() == null || userInfoMessage.getPassword() == null) {
                throw new UserInfoException(ExceptionCode._207);
            }
            userMapper.insert(new User(userInfoMessage.getId(), userInfoMessage.getNickname(), userInfoMessage.getPassword()));
        } else {
            // 校验消息内用户ID与session拥有用户ID是否一致
            if (!Dispatcher.session2UserIdMap.get(session).equals(userInfoMessage.getId())) {
                throw new UserInfoException(ExceptionCode._204);
            }
            // 校验用户是否在线
            if (!Dispatcher.onlineMap.get(userInfoMessage.getId())) {
                throw new UserInfoException(ExceptionCode._208);
            }

            switch (type) {
                case "sign_out" -> {
                    // 标记为离线
                    Dispatcher.onlineMap.put(userInfoMessage.getId(), false);
                }
                case "delete" -> {
                    userMapper.deleteById(userInfoMessage.getId());
                }
                case "change_nickname" -> {
                    UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
                    String nickname_new = userInfoMessage.getNickname();
                    if (nickname_new == null) {
                        throw new UserInfoException(ExceptionCode._205);
                    }
                    updateWrapper.eq("id", userInfoMessage.getId()).set("nickname", nickname_new);
                    userMapper.update(null, updateWrapper);
                }
                case "change_password" -> {
                    UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
                    String password_new = userInfoMessage.getPassword();
                    if (password_new == null) {
                        throw new UserInfoException(ExceptionCode._206);
                    }
                    updateWrapper.eq("id", userInfoMessage.getId()).set("password", password_new);
                    userMapper.update(null, updateWrapper);
                }
                default -> throw new MessageException(ExceptionCode._200);
            }
        }
    }
}
