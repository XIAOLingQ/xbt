package com.xbt.server.service;

import com.xbt.server.pojo.dto.HomeworkDetailDTO;
import com.xbt.server.pojo.dto.SaveHomeworkRequest;
import com.xbt.server.pojo.dto.SubmitHomeworkRequest;
import com.xbt.server.pojo.dto.HomeworkDTO;
import com.xbt.server.pojo.dto.GradeSubmissionRequest;
import com.xbt.server.pojo.entity.Homework;
import com.xbt.server.pojo.vo.HomeworkStatusVO;
import com.xbt.server.pojo.vo.HomeworkSubmissionSummaryVO;
import com.xbt.server.pojo.vo.SubmissionDetailVO;
import java.util.List;
import java.util.Map;

public interface HomeworkService {

    List<HomeworkStatusVO> getHomeworkListByCourseId(Long courseId, Long studentId);

    Homework saveHomework(HomeworkDTO request);

    void deleteHomework(Long homeworkId);

    HomeworkDetailDTO getHomeworkDetail(Long id, Long studentId);

    void submitHomework(Long studentId, SubmitHomeworkRequest request);

    List<HomeworkSubmissionSummaryVO> getSubmissionsByHomeworkId(Long homeworkId);

    SubmissionDetailVO getSubmissionDetails(Long submissionId);

    void gradeSubmission(Long submissionId, GradeSubmissionRequest request);
}