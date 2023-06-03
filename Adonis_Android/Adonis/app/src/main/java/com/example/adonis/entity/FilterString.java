package com.example.adonis.entity;

public class FilterString {
    public static final String OFF_LINE = "off_line";
    public static final String ONLINE_STATE = "online_state";
    public static final String REPLY_MESSAGE = "replyMessage";
    public static final String USER_OP_MESSAGE = "userOpMessage";
    public static final String FRIEND_OP_MESSAGE = "friendOpMessage";
    public static final String FRIEND_INFO_MESSAGE = "friendInfoMessage";
    public static final String DIALOGUE_INFO_MESSAGE = "dialogueMessage";
    public static final String USER_ONLINE_MESSAGE = "userOnlineMessage";
    public static final String USER_INFO_MESSAGE = "userInfoMessage";
    public static final String RESULT = "result";
    public static final String DATA = "data";
    public static final String TYPE = "type";
    public static final String ID = "id";
    public static final String PASSWORD = "password";
    public static final String AUTO_LOGIN = "auto_login";
    public static final String NICKNAME = "nickname";
    public static final String CUSTOM_NICKNAME = "customNickname";
    public static final String MEMO = "memo";
    public static final String REMARK = "remark";
    public static final String IF_ONLINE = "if_online";
    public static final String CODE = "code";
    public static final String SENDER_ID = "senderId";
    public static final String RECEIVER_ID = "receiverId";
    public static final String CONTENT = "content";
    public static final String OCCURRED_TIME = "occurredTime";
    public static final String LASTED_TIME = "lastedTime";
    public static final String UPDATE_NEWS = "updateNews";
    public static final String UPDATE_UNREAD_NEWS = "updateUnreadNews";
    public static final String NEW_FRIENDS_TABLE = "newFriends";
    public static final String LAST_TIME_TABLE = "lastTime";
    public static final String CONTACTS_TABLE = "contacts";
    public static final String UNREAD_NEWS_TABLE = "unreadNews";
    public static final String LASTED_NEWS_TABLE = "lastedNews";
    public static final String NEWS_TABLE = "news";
    public static final String LATEST_NEWS_TABLE = "latestNews";
    public static final String SOFT_INPUT_HEIGHT = "soft_input_height";

    public static final String LASTED_MESSAGE = "[限时消息]";
    public static final String DELETE_FRIEND = "删除好友";
    public static final String BLOCK_FRIEND = "拉黑用户";
    public static final String CONFIRM = "确认";
    public static final String CANCEl = "取消";
    public static final String ADD_FRIEND = "添加好友";
    public static final String ME = "我";
    public static String makeDeleteDialogue(String name) {
        return "从好友列表删除”"+name+"”，将清除所有与该用户的聊天记录";
    }
    public static String BLOCK_INFO = "拉黑后该用户无法给您发送消息，也无法向您发送好友申请";

}
