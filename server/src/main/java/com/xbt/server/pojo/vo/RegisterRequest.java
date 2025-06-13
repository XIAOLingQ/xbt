package com.xbt.server.pojo.vo;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private Integer role;
    private String realName;
}