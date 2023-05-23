package com.reddish.adonisbase.Manager;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.reddish.adonisbase.DAO.DialogueMapper;
import com.reddish.adonisbase.DAO.FriendMapper;
import com.reddish.adonisbase.DAO.UserMapper;
import com.reddish.adonisbase.DO.Dialogue;
import com.reddish.adonisbase.DO.Friend;
import com.reddish.adonisbase.DO.User;
import org.springframework.stereotype.Component;

import java.util.List;

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

    // 判断用户userId是否存在
    public User selectUserById(String userId) {
        return userMapper.selectById(userId);
    }

    // 创建新用户
    public void createNewUser(String userId, String nickname, String password) {
        userMapper.insert(new User(userId, nickname, password));
    }

    // 删除用户
    public void deleteUser(String userId) {
        userMapper.deleteById(userId);
    }

    public void updateUserPassword(String userId, String password) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", userId).set("password", password);
        userMapper.update(null, updateWrapper);
    }

    public void updateUserNickname(String userId, String nickname) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", userId).set("nickname", nickname);
        userMapper.update(null, updateWrapper);
    }

    public void createNewFriendship(String subjectId, String objectId) {
        friendMapper.insert(new Friend(subjectId, objectId, 1, null, null));
    }

    public void createNewFriendship(String subjectId, String objectId, int Status, String customNickname, String memo) {
        friendMapper.insert(new Friend(subjectId, objectId, Status, customNickname, memo));
    }

    public void updateFriendStatus(String subjectId, String objectId, int status) {
        UpdateWrapper<Friend> updateWrapper = new UpdateWrapper<>();
        updateWrapper
                .eq("subjectId", subjectId)
                .eq("objectId", objectId)
                .set("status", status);
        friendMapper.update(null, updateWrapper);
    }

    public void updateFriendCustomNickname(String subjectId, String objectId, String customNickname) {
        UpdateWrapper<Friend> updateWrapper = new UpdateWrapper<>();
        updateWrapper
                .eq("subjectId", subjectId)
                .eq("objectId", objectId)
                .set("customNickname", customNickname);
        friendMapper.update(null, updateWrapper);
    }

    public void updateFriendMemo(String subjectId, String objectId, String memo) {
        UpdateWrapper<Friend> updateWrapper = new UpdateWrapper<>();
        updateWrapper
                .eq("subjectId", subjectId)
                .eq("objectId", objectId)
                .set("memo", memo);
        friendMapper.update(null, updateWrapper);
    }

    // 判断objectId是不是subjectId的单向好友
    public boolean judgeFriendshipOneWay(String subjectId, String objectId) {
        QueryWrapper<Friend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("subjectId", subjectId).eq("objectId", objectId);
        Friend friend = friendMapper.selectOne(queryWrapper);
        return friend.getStatus() == 1;
    }

    // 查询userId作为客体的好友关系
    public List<Friend> queryFriendsOfObject(String userId) {
        QueryWrapper<Friend> friendQueryWrapper = new QueryWrapper<>();
        friendQueryWrapper.eq("objectId", userId);
        return friendMapper.selectList(friendQueryWrapper);
    }

    // 查询userId作为主体的好友关系
    public List<Friend> queryFriendsOfSubject(String userId) {
        QueryWrapper<Friend> friendQueryWrapper = new QueryWrapper<>();
        friendQueryWrapper.eq("subjectId", userId);
        return friendMapper.selectList(friendQueryWrapper);
    }

    public Friend queryTheFriend(String subjectId, String objectId) {
        QueryWrapper<Friend> friendQueryWrapper = new QueryWrapper<>();
        friendQueryWrapper
                .eq("subjectId", subjectId)
                .eq("objectId", objectId);
        return friendMapper.selectOne(friendQueryWrapper);
    }

    public void deleteFriend(String subjectId, String objectId) {
        QueryWrapper<Friend> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("subjectId", subjectId)
                .eq("objectId", objectId);
        friendMapper.delete(queryWrapper);
    }

    public void saveDialogue(String senderId, String receiverId, String content, long occurredTime, long lastedTime) {
        dialogueMapper.insert(
                new Dialogue(senderId, receiverId, content, occurredTime, lastedTime));
    }

    public List<Dialogue> queryDialogues(String senderId, String receiverId) {
        QueryWrapper<Dialogue> dialogueQueryWrapper = new QueryWrapper<>();
        dialogueQueryWrapper
                .eq("senderId", senderId)
                .eq("receiverId", receiverId);
        List<Dialogue> dialogueList = dialogueMapper.selectList(dialogueQueryWrapper);
        // 聊天记录查完之后即删除
        dialogueMapper.delete(dialogueQueryWrapper);
        return dialogueList;
    }
}
