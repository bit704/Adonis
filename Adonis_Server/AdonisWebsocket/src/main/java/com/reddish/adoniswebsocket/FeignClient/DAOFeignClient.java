package com.reddish.adoniswebsocket.FeignClient;


import com.reddish.adoniswebsocket.DTO.Dialogue;
import com.reddish.adoniswebsocket.DTO.Friend;
import com.reddish.adoniswebsocket.DTO.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "Adonis-Base", contextId = "DAO")
public interface DAOFeignClient {

    // 判断用户userId是否存在
    @GetMapping("/DAO/selectUserById")
    User selectUserById(@RequestParam String userId);

    // 创建新用户
    @GetMapping("/DAO/createNewUser")
    void createNewUser(@RequestParam String userId,
                       @RequestParam String nickname,
                       @RequestParam String password);

    // 删除用户
    @GetMapping("/DAO/deleteUser")
    void deleteUser(@RequestParam String userId);

    @GetMapping("/DAO/updateUserPassword")
    void updateUserPassword(@RequestParam String userId,
                            @RequestParam String password);

    @GetMapping("/DAO/updateUserNickname")
    public void updateUserNickname(@RequestParam String userId,
                                   @RequestParam String nickname);

    @GetMapping("/DAO/createNewFriendship")
    void createNewFriendship(@RequestParam String subjectId,
                             @RequestParam String objectId,
                             @RequestParam int Status,
                             @RequestParam String customNickname,
                             @RequestParam String memo);

    @GetMapping("/DAO/updateFriendStatus")
    void updateFriendStatus(@RequestParam String subjectId,
                            @RequestParam String objectId,
                            @RequestParam int status);

    @GetMapping("/DAO/updateFriendCustomNickname")
    void updateFriendCustomNickname(@RequestParam String subjectId,
                                    @RequestParam String objectId,
                                    @RequestParam String customNickname);

    @GetMapping("/DAO/updateFriendMemo")
    void updateFriendMemo(@RequestParam String subjectId,
                          @RequestParam String objectId,
                          @RequestParam String memo);

    // 判断objectId是不是subjectId的单向好友
    @GetMapping("/DAO/judgeFriendshipOneWay")
    boolean judgeFriendshipOneWay(@RequestParam String subjectId,
                                  @RequestParam String objectId);

    // 查询userId作为客体的好友关系
    @GetMapping("/DAO/queryFriendsOfObject")
    List<Friend> queryFriendsOfObject(@RequestParam String userId);

    // 查询userId作为主体的好友关系
    @GetMapping("/DAO/queryFriendsOfSubject")
    List<Friend> queryFriendsOfSubject(@RequestParam String userId);

    @GetMapping("/DAO/queryTheFriend")
    Friend queryTheFriend(@RequestParam String subjectId,
                          @RequestParam String objectId);

    @GetMapping("/DAO/deleteFriend")
    void deleteFriend(@RequestParam String subjectId,
                      @RequestParam String objectId);

    @GetMapping("/DAO/saveDialogue")
    void saveDialogue(@RequestParam String senderId,
                      @RequestParam String receiverId,
                      @RequestParam String content,
                      @RequestParam long occurredTime,
                      @RequestParam long lastedTime);

    @GetMapping("/DAO/queryDialogues")
    List<Dialogue> queryDialogues(@RequestParam String senderId,
                                  @RequestParam String receiverId);
}
