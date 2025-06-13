package com.xbt.server.service;

import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class QiniuService {

    @Value("${qiniu.accessKey}")
    private String accessKey;

    @Value("${qiniu.secretKey}")
    private String secretKey;

    @Value("${qiniu.bucket}")
    private String bucket;

    /**
     * 获取七牛云上传凭证
     *
     * @return 上传凭证token
     */
    public String getUploadToken() {
        Auth auth = Auth.create(accessKey, secretKey);
        // 简单上传，使用key作为文件名
        return auth.uploadToken(bucket);
    }

    /**
     * 删除七牛云上的文件
     * 
     * @param key 文件名
     */
    public void deleteFile(String key) throws QiniuException {
        Auth auth = Auth.create(accessKey, secretKey);
        // 构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.huabei()); // z1 代表华北-北京区域
        BucketManager bucketManager = new BucketManager(auth, cfg);
        bucketManager.delete(bucket, key);
    }
}