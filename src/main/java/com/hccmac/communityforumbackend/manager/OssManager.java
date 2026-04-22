package com.hccmac.communityforumbackend.manager;

import com.aliyun.oss.OSS;
import com.hccmac.communityforumbackend.config.OssClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;

/**
 * OSS 管理器
 */
@Component
@Slf4j
public class OssManager {

    @Resource
    private OSS ossClient;

    @Resource
    private OssClientConfig ossClientConfig;

    public String uploadObject(String objectKey, InputStream inputStream) {
        ossClient.putObject(ossClientConfig.getBucketName(), objectKey, inputStream);
        String fileUrl = ossClientConfig.getHost() + "/" + objectKey;
        log.info("OSS上传 objectKey:{} fileUrl:{}", objectKey, fileUrl);
        return fileUrl;
    }

    public void deleteObject(String objectKey) {
        ossClient.deleteObject(ossClientConfig.getBucketName(), objectKey);
        log.info("OSS删除 objectKey:{}", objectKey);
    }
}
