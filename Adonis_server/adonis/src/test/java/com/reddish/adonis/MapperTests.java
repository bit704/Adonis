package com.reddish.adonis;

import com.reddish.adonis.mapper.UserMapper;
import com.reddish.adonis.mapper.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MapperTests extends AdonisApplicationTests {

    @Autowired
    UserMapper userMapper;

    @Test
    void deleteAllUser() {
        userMapper.delete(null);
    }

    @Test
    void insertFakeUsers() {
        User user1 = new User("555", "湖北人", "8955");
        User user2 = new User("569", "湖南人", "758955");
        User user3 = new User("754", "河北人", "1225666");
        User user4 = new User("956", "河南人", "56778");
        userMapper.insert(user1);
        userMapper.insert(user2);
        userMapper.insert(user3);
        userMapper.insert(user4);
    }

    @Test
    void showAllUser() {
        System.out.println(userMapper.selectList(null));
    }
}
