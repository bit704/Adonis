package com.reddish.adonis.Manager;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.reddish.adonis.AO.DialogueMessage;
import com.reddish.adonis.DAO.DialogueMapper;
import com.reddish.adonis.DAO.FriendMapper;
import com.reddish.adonis.DAO.UserMapper;
import com.reddish.adonis.DO.Dialogue;
import com.reddish.adonis.DO.Friend;
import com.reddish.adonis.Websocket.Dispatcher;
import org.springframework.stereotype.Component;

@Component
public class DAOManager {

    private final UserMapper userMapper;
    private final FriendMapper friendMapper;
    private final DialogueMapper dialogueMapper;

    public DAOManager(UserMapper userMapper, FriendMapper friendMapper, DialogueMapper dialogueMapper) {
        this.userMapper = userMapper;
        this.friendMapper = friendMapper;
        this.dialogueMapper = dialogueMapper;
    }

    // 判断objectId是不是subjectId的单向好友
    public boolean judgeFriendshipOneWay(String subjectId, String objectId) {
        QueryWrapper<Friend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("subjectId", subjectId).eq("objectId", objectId);
        Friend friend = friendMapper.selectOne(queryWrapper);
        return friend.getStatus() == 1;
    }

    // 判断用户userId是否存在
    public boolean judgeUserExist(String userId) {
        return userMapper.selectById(userId) != null;
    }

    public void saveDialogue(String senderId, String receiverId, String content, long occurredTime, long lastedTime) {
        dialogueMapper.insert(
                new Dialogue(senderId, receiverId, content, occurredTime, lastedTime));
    }
}
