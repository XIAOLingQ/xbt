package com.xbt.server.pojo.vo;

import lombok.Data;
import java.util.List;

@Data
public class LearnPageVO {
    private Long id;
    private String title;
    private String description;
    private List<ChapterVO> chapters;
}