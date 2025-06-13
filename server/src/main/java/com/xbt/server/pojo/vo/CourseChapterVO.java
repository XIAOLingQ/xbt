package com.xbt.server.pojo.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_EMPTY) // 如果children为空，则不序列化
public class CourseChapterVO {
    private Long id;
    private Long courseId;
    private Long parentId;
    private Integer type;
    private String title;
    private String url;
    private Integer duration;
    private Integer orderNum;
    private List<CourseChapterVO> children; // 子节点
} 