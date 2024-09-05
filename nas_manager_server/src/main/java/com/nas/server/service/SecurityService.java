package com.nas.server.service;

import com.nas.server.entity.db.User;
import com.nas.server.mapper.UserMapper;
import com.nas.server.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class SecurityService {

    private static final Logger logger = LoggerFactory.getLogger(SecurityService.class);
    @Autowired
    private UserMapper userMapper;

    private final Lock createUserLock = new ReentrantLock();

    public void createUser(User user) throws Exception {
        createUserLock.lock();
        try {
            if (StringUtil.isEmpty(user.getUsername())) {
                throw new Exception("用户名不能为空");
            }
            if (StringUtil.isEmpty(user.getPassword())) {
                throw new Exception("密码为空");
            }
            // MD5加密密码（加盐NAS_）
            user.setPassword(StringUtil.getStringMD5("NAS_" + user.getPassword()));
            if (null == userMapper.findUserByUserName(user.getUsername())) {
                userMapper.saveUser(user);
            } else {
                throw new Exception("用户已存在");
            }
        } finally {
            createUserLock.unlock();
        }
    }

    public User findUserByUsername(String username) {
        return userMapper.findUserByUserName(username);
    }

    public User findUserByUserId(int id) {
        return userMapper.findUserById(id);
    }

    public boolean verifyUser (String username, String password) {
        String passwordMD5 = StringUtil.getStringMD5("NAS_" + password);
        return userMapper.verifyUser(username, passwordMD5) == 1;
    }

}
