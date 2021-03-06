package com.xavier.dong.resource.server.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xavier.dong.resource.server.dao.UserMapper;
import com.xavier.dong.resource.server.entity.po.User;
import com.xavier.dong.resource.server.service.UserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xavierdong
 * @since 2020-06-05
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
