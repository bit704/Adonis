package com.reddish.adonis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reddish.adonis.mapper.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {


}
