package com.xbt.server.controller;

import com.xbt.server.util.Result;
import com.xbt.server.pojo.dto.UpdateProgressRequest;
import com.xbt.server.pojo.dto.UpdateVideoProgressDTO;
import com.xbt.server.service.ProgressService;
import com.xbt.server.util.AuthUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    @Resource
    private ProgressService progressService;

    @PostMapping("/video")
    public Result updateVideoProgress(@RequestBody UpdateProgressRequest request) {
        Long studentId = AuthUtils.getCurrentUserId();

        UpdateVideoProgressDTO progressDTO = new UpdateVideoProgressDTO();
        progressDTO.setStudentId(studentId);
        progressDTO.setVideoId(request.getVideoId());
        progressDTO.setWatchDuration(request.getWatchDuration());

        progressService.updateVideoProgress(progressDTO);

        return Result.success();
    }
}