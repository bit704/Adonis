package com.reddish.adonis.service;

import com.reddish.adonis.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

}
