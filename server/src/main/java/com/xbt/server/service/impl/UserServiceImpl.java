package com.xbt.server.service.impl;

import com.xbt.server.exception.BusinessException;
import com.xbt.server.mapper.UserMapper;
import com.xbt.server.pojo.entity.User;
import com.xbt.server.pojo.vo.*;
import com.xbt.server.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserVO register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (checkUsernameExists(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (checkEmailExists(request.getEmail())) {
            throw new BusinessException("邮箱已被使用");
        }

        // 创建新用户
        User user = new User();
        BeanUtils.copyProperties(request, user);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userMapper.insert(user);

        // 返回用户信息
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public UserVO login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public UserVO getUserInfo(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public UserVO updateUserInfo(Long userId, UpdateUserRequest request) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 如果要更新邮箱，检查新邮箱是否已被使用
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (checkEmailExists(request.getEmail())) {
                throw new BusinessException("邮箱已被使用");
            }
        }

        BeanUtils.copyProperties(request, user);
        userMapper.update(user);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public boolean checkUsernameExists(String username) {
        return userMapper.findByUsername(username) != null;
    }

    @Override
    public boolean checkEmailExists(String email) {
        return userMapper.findByEmail(email) != null;
    }
}