package com.xbt.server.pojo.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String email;
    private Integer role;
    private String realName;
    private String avatar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}