package com.xbt.server.pojo.vo;

import lombok.Data;

@Data
public class SectionVO {
    private Long id;
    private String title;
    private String url;
    private Integer orderNum;
    private boolean isCompleted; // 学生是否已完成该节
}