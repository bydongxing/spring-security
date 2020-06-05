package com.xavier.dong.dao;

import com.xavier.dong.entity.dto.JwtUser;
import com.xavier.dong.entity.po.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xavierdong
 * @since 2020-06-05
 */
public interface UserMapper extends BaseMapper<User> {


    JwtUser findJwtUserByUserName(String userName);

}
