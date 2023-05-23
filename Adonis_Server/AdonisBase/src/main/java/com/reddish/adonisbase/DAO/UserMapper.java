package com.reddish.adonisbase.DAO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reddish.adonisbase.DO.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {


}
