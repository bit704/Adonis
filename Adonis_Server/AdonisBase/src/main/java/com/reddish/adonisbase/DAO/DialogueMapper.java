package com.reddish.adonisbase.DAO;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reddish.adonisbase.DO.Dialogue;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DialogueMapper extends BaseMapper<Dialogue> {
}