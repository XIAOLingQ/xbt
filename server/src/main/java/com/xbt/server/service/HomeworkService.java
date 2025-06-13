package com.xbt.server.service;

import com.xbt.server.pojo.dto.HomeworkDetailDTO;
import com.xbt.server.pojo.dto.SaveHomeworkRequest;
import com.xbt.server.pojo.dto.SubmitHomeworkRequest;
import com.xbt.server.pojo.entity.Homework;
import com.xbt.server.pojo.vo.HomeworkStatusVO;
import java.util.List;

public interface HomeworkService {

    List<HomeworkStatusVO> getHomeworkListByCourseId(Long courseId, Long studentId);

    Homework saveHomework(SaveHomeworkRequest request);

    void deleteHomework(Long homeworkId);

    HomeworkDetailDTO getHomeworkDetail(Long id, Long studentId);

    void submitHomework(Long studentId, SubmitHomeworkRequest request);
}