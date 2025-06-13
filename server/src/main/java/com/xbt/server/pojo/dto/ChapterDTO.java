package com.xbt.server.pojo.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChapterDTO {
    private Long id;
    private Long courseId;
    private Long parentId;
    private Integer type;
    private String title;
    private String url;
    private Integer duration;
    private Integer orderNum;
    private List<ChapterDTO> children;
}