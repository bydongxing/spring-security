package com.xavier.dong.gateway.server.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xavier.dong.gateway.server.dao.RoleMapper;
import com.xavier.dong.gateway.server.entity.po.Role;
import com.xavier.dong.gateway.server.service.RoleService;
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
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

}
