package com.xbt.server.service.impl;

import com.qiniu.util.Auth;
import com.xbt.server.service.QiniuService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class QiniuServiceImpl extends QiniuService {

    @Value("${qiniu.accessKey}")
    private String accessKey;

    @Value("${qiniu.secretKey}")
    private String secretKey;

    @Value("${qiniu.bucket}")
    private String bucket;

    @Override
    public String getUploadToken() {
        Auth auth = Auth.create(accessKey, secretKey);
        // 简单上传，覆盖同名文件
        return auth.uploadToken(bucket);
    }
}