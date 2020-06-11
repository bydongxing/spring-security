package com.xavier.dong.resource.server.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xavier.dong.resource.server.dao.UserRoleMapper;
import com.xavier.dong.resource.server.entity.po.UserRole;
import com.xavier.dong.resource.server.service.UserRoleService;
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
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

}
