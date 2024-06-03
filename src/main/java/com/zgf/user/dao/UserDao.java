package com.zgf.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zgf.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import javax.xml.ws.soap.MTOM;

/**
 * (User)表数据库访问层
 *
 * @author makejava
 * @since 2024-05-29 09:58:49
 */
@Mapper
public interface UserDao extends BaseMapper<User> {

}

