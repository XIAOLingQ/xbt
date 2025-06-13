package com.xbt.server.service;

import com.xbt.server.pojo.dto.UpdateVideoProgressDTO;

public interface ProgressService {

    /**
     * 更新学生观看视频的进度
     * 
     * @param progressDTO
     */
    void updateVideoProgress(UpdateVideoProgressDTO progressDTO);
}