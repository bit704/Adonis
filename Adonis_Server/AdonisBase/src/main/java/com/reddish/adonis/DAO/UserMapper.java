package com.reddish.adonis.DAO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reddish.adonis.DO.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {


}
