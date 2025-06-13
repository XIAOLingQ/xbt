package com.xbt.server.service.impl;

import com.xbt.server.mapper.StudentVideoProgressMapper;
import com.xbt.server.pojo.dto.UpdateVideoProgressDTO;
import com.xbt.server.service.ProgressService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ProgressServiceImpl implements ProgressService {

    @Resource
    private StudentVideoProgressMapper studentVideoProgressMapper;

    @Override
    public void updateVideoProgress(UpdateVideoProgressDTO progressDTO) {
        // 在这里可以添加一些业务校验，例如检查 videoId 是否有效等
        // 为简化流程，我们直接调用 mapper
        studentVideoProgressMapper.upsertProgress(progressDTO);
    }
}