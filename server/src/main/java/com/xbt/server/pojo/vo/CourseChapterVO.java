package com.xbt.server.pojo.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class CourseChapterVO {
    private Long id;
    private Long courseId;
    private Long parentId;
    private Integer type;
    private String title;
    private String url;
    private Integer duration;
    private Integer orderNum;
    @JsonInclude(JsonInclude.Include.NON_EMPTY) // 仅当children不为空时序列化
    private List<CourseChapterVO> children; // 子节点
} 