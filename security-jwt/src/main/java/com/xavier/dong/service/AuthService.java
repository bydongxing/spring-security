package com.xavier.dong.service;

import com.xavier.dong.entity.po.User;

/**
 *
 * @author xavierdong
 */
public interface AuthService {

    int register(User userToAdd);

    String login(String username, String password);
}