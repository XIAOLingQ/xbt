package com.xbt.server.pojo.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private Integer role;  // 1-学生，2-教师
    private String realName;
    private String avatar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 