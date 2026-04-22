package com.hccmac.communityforumbackend.manager.upload;

import com.hccmac.communityforumbackend.manager.OssManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;

/**
 * OSS 图片上传实现
 */
@Component
public class FileOssPictureUpload extends PictureUploadTemplate {

    @Resource
    private OssManager ossManager;

    @Override
    protected String doUpload(String objectKey, InputStream inputStream) {
        return ossManager.uploadObject(objectKey, inputStream);
    }
}
