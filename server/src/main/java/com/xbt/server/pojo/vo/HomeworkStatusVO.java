package com.xbt.server.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeworkStatusVO {
    private Long id;
    private String title;
    private String endTime;
    private String startTime;
    /**
     * 学生提交状态
     * 1-未开始, 2-已提交, 3-已批改
     */
    private Integer status;
    private String score;
}