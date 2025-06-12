package com.xbt.server.controller;

import com.xbt.server.pojo.vo.LoginRequest;
import com.xbt.server.pojo.vo.RegisterRequest;
import com.xbt.server.pojo.vo.UpdateUserRequest;
import com.xbt.server.pojo.vo.UserVO;
import com.xbt.server.service.UserService;
import com.xbt.server.util.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody RegisterRequest request) {
        UserVO userVO = userService.register(request);
        return Result.success(userVO);
    }

    @PostMapping("/login")
    public Result<UserVO> login(@RequestBody LoginRequest request, HttpSession session) {
        UserVO user = userService.login(request);
        session.setAttribute("userId", user.getId());
        if (user.getRole() != null) {
            session.setAttribute("role", user.getRole() == 2 ? "TEACHER" : "STUDENT");
        }
        return Result.success(user);
    }

    @GetMapping("/info")
    public Result<UserVO> getUserInfo(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return Result.success(userService.getUserInfo(userId));
    }

    @PutMapping("/info")
    public Result<UserVO> updateUserInfo(@RequestBody UpdateUserRequest request, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        UserVO userVO = userService.updateUserInfo(userId, request);
        return Result.success(userVO);
    }

    @GetMapping("/check-username")
    public Result<Boolean> checkUsername(@RequestParam String username) {
        System.out.println(userService.checkUsernameExists(username));
        return Result.success(userService.checkUsernameExists(username));
    }

    @GetMapping("/check-email")
    public Result<Boolean> checkEmail(@RequestParam String email) {
        return Result.success(userService.checkEmailExists(email));
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        session.removeAttribute("userId");
        session.removeAttribute("role");
        session.invalidate();
        return Result.success();
    }
}