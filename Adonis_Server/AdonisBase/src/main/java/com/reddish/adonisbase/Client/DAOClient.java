package com.reddish.adonisbase.Client;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.reddish.adonisbase.DAO.DialogueMapper;
import com.reddish.adonisbase.DAO.FriendMapper;
import com.reddish.adonisbase.DAO.UserMapper;
import com.reddish.adonisbase.DO.Dialogue;
import com.reddish.adonisbase.DO.Friend;
import com.reddish.adonisbase.DO.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DAOClient {

    private final UserMapper userMapper;
    private final FriendMapper friendMapper;
    private final DialogueMapper dialogueMapper;

    public DAOClient(UserMapper userMapper, FriendMapper friendMapper, DialogueMapper dialogueMapper) {
        this.userMapper = userMapper;
        this.friendMapper = friendMapper;
        this.dialogueMapper = dialogueMapper;
    }

    // 判断用户userId是否存在
    @GetMapping("/DAO/selectUserById")
    public User selectUserById(@RequestParam String userId) {
        return userMapper.selectById(userId);
    }

    // 创建新用户
    @GetMapping("/DAO/createNewUser")
    public void createNewUser(@RequestParam String userId, @RequestParam String nickname, @RequestParam String password) {
        userMapper.insert(new User(userId, nickname, password));
    }

    // 注销用户前删除与其相关的的好友信息和所有对话消息，避免违反外键约束
    // 使用事务
    @Transactional
    @GetMapping("/DAO/deleteUser")
    public void deleteUser(@RequestParam String userId) {
        QueryWrapper<Friend> friendQueryWrapper = new QueryWrapper<>();
        friendQueryWrapper
                .eq("subjectId", userId)
                .or()
                .eq("objectId", userId);
        friendMapper.delete(friendQueryWrapper);
        QueryWrapper<Dialogue> dialogueQueryWrapper = new QueryWrapper<>();
        friendQueryWrapper
                .eq("senderId", userId)
                .or()
                .eq("receiverId", userId);
        dialogueMapper.delete(dialogueQueryWrapper);
        userMapper.deleteById(userId);
    }

    @GetMapping("/DAO/updateUserPassword")
    public void updateUserPassword(@RequestParam String userId, @RequestParam String password) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", userId).set("password", password);
        userMapper.update(null, updateWrapper);
    }

    @GetMapping("/DAO/updateUserNickname")
    public void updateUserNickname(@RequestParam String userId, @RequestParam String nickname) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", userId).set("nickname", nickname);
        userMapper.update(null, updateWrapper);
    }

    @GetMapping("/DAO/createNewFriendship1")
    public void createNewFriendship(@RequestParam String subjectId, @RequestParam String objectId) {
        friendMapper.insert(new Friend(subjectId, objectId, 1, null, null));
    }

    @GetMapping("/DAO/createNewFriendship2")
    public void createNewFriendship(@RequestParam String subjectId, @RequestParam String objectId, @RequestParam int Status, @RequestParam String customNickname, @RequestParam String memo) {
        friendMapper.insert(new Friend(subjectId, objectId, Status, customNickname, memo));
    }

    @GetMapping("/DAO/updateFriendStatus")
    public void updateFriendStatus(@RequestParam String subjectId, @RequestParam String objectId, @RequestParam int status) {
        UpdateWrapper<Friend> updateWrapper = new UpdateWrapper<>();
        updateWrapper
                .eq("subjectId", subjectId)
                .eq("objectId", objectId)
                .set("status", status);
        friendMapper.update(null, updateWrapper);
    }

    @GetMapping("/DAO/updateFriendCustomNickname")
    public void updateFriendCustomNickname(@RequestParam String subjectId, @RequestParam String objectId, @RequestParam String customNickname) {
        UpdateWrapper<Friend> updateWrapper = new UpdateWrapper<>();
        updateWrapper
                .eq("subjectId", subjectId)
                .eq("objectId", objectId)
                .set("customNickname", customNickname);
        friendMapper.update(null, updateWrapper);
    }

    @GetMapping("/DAO/updateFriendMemo")
    public void updateFriendMemo(@RequestParam String subjectId, @RequestParam String objectId, @RequestParam String memo) {
        UpdateWrapper<Friend> updateWrapper = new UpdateWrapper<>();
        updateWrapper
                .eq("subjectId", subjectId)
                .eq("objectId", objectId)
                .set("memo", memo);
        friendMapper.update(null, updateWrapper);
    }

    // 判断objectId是不是subjectId的单向好友
    @GetMapping("/DAO/judgeFriendshipOneWay")
    public boolean judgeFriendshipOneWay(@RequestParam String subjectId, @RequestParam String objectId) {
        QueryWrapper<Friend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("subjectId", subjectId).eq("objectId", objectId);
        Friend friend = friendMapper.selectOne(queryWrapper);
        return friend.getStatus() == 1;
    }

    // 查询userId作为客体的好友关系
    @GetMapping("/DAO/queryFriendsOfObject")
    public List<Friend> queryFriendsOfObject(@RequestParam String userId) {
        QueryWrapper<Friend> friendQueryWrapper = new QueryWrapper<>();
        friendQueryWrapper.eq("objectId", userId);
        return friendMapper.selectList(friendQueryWrapper);
    }

    // 查询userId作为主体的好友关系
    @GetMapping("/DAO/queryFriendsOfSubject")
    public List<Friend> queryFriendsOfSubject(@RequestParam String userId) {
        QueryWrapper<Friend> friendQueryWrapper = new QueryWrapper<>();
        friendQueryWrapper.eq("subjectId", userId);
        return friendMapper.selectList(friendQueryWrapper);
    }

    @GetMapping("/DAO/queryTheFriend")
    public Friend queryTheFriend(@RequestParam String subjectId, @RequestParam String objectId) {
        QueryWrapper<Friend> friendQueryWrapper = new QueryWrapper<>();
        friendQueryWrapper
                .eq("subjectId", subjectId)
                .eq("objectId", objectId);
        return friendMapper.selectOne(friendQueryWrapper);
    }

    @GetMapping("/DAO/deleteFriend")
    public void deleteFriend(@RequestParam String subjectId, @RequestParam String objectId) {
        QueryWrapper<Friend> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("subjectId", subjectId)
                .eq("objectId", objectId);
        friendMapper.delete(queryWrapper);
    }

    @GetMapping("/DAO/saveDialogue")
    public void saveDialogue(@RequestParam String senderId, @RequestParam String receiverId, @RequestParam String content, @RequestParam long occurredTime, @RequestParam long lastedTime) {
        dialogueMapper.insert(
                new Dialogue(senderId, receiverId, content, occurredTime, lastedTime));
    }

    @GetMapping("/DAO/queryDialogues")
    public List<Dialogue> queryDialogues(@RequestParam String senderId, @RequestParam String receiverId) {
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
