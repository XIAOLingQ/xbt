package com.xbt.server.pojo.vo;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String email;
    private String realName;
    private String avatar;
}