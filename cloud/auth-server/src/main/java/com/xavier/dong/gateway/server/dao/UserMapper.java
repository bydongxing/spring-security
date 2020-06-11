package com.xavier.dong.gateway.server.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xavier.dong.gateway.server.entity.dto.JwtUser;
import com.xavier.dong.gateway.server.entity.po.User;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author xavierdong
 * @since 2020-06-05
 */
public interface UserMapper extends BaseMapper<User> {


    JwtUser findJwtUserByUserName(String userName);

    JwtUser findJwtUserByPhone(String phone);
}
