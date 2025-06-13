package com.xbt.server.controller;
import com.xbt.server.util.Result;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Value("${qiniu.accessKey}")
    private String accessKey;

    @Value("${qiniu.secretKey}")
    private String secretKey;

    @Value("${qiniu.bucket}")
    private String bucket;

    @GetMapping("/upload-token")
    public Result<String> getUploadToken() {
        Auth auth = Auth.create(accessKey, secretKey);
        // 简单上传，使用 bucket 作为唯一凭证
        String upToken = auth.uploadToken(bucket);
        return Result.success(upToken);
    }
}