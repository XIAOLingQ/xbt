package com.xbt.server.pojo.vo;

import lombok.Data;
import java.util.List;

@Data
public class ChapterVO {
    private Long id;
    private String title;
    private Integer orderNum;
    private List<SectionVO> sections;
    private List<HomeworkStatusVO> homeworks;
}