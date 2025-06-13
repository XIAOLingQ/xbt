package com.xbt.server.pojo.dto;

import com.xbt.server.pojo.entity.Homework;
import com.xbt.server.pojo.entity.HomeworkQuestion;
import com.xbt.server.pojo.entity.HomeworkSubmission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeworkDetailDTO {
    private Homework homework;
    private List<HomeworkQuestion> questions;
    private HomeworkSubmission submission;
}