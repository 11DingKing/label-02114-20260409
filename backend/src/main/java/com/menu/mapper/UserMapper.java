package com.menu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.menu.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
