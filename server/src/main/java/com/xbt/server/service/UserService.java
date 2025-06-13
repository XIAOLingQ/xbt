package com.xbt.server.service;

import com.xbt.server.pojo.vo.LoginRequest;
import com.xbt.server.pojo.vo.RegisterRequest;
import com.xbt.server.pojo.vo.UpdateUserRequest;
import com.xbt.server.pojo.vo.UserVO;

public interface UserService {
    UserVO register(RegisterRequest request);

    UserVO login(LoginRequest request);

    UserVO getUserInfo(Long userId);

    UserVO updateUserInfo(Long userId, UpdateUserRequest request);

    boolean checkUsernameExists(String username);

    boolean checkEmailExists(String email);
}