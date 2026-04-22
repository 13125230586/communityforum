package com.hccmac.communityforumbackend.manager.upload;

import cn.hutool.core.io.FileUtil;
import com.hccmac.communityforumbackend.common.ErrorCode;
import com.hccmac.communityforumbackend.constant.FileConstant;
import com.hccmac.communityforumbackend.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 图片上传模板
 */
public abstract class PictureUploadTemplate {

    private static final List<String> ALLOW_SUFFIX_LIST = Arrays.asList("jpg", "jpeg", "png", "webp", "gif");

    public String uploadPicture(MultipartFile multipartFile, String uploadPathPrefix) {
        validFile(multipartFile);
        String suffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        String objectKey = uploadPathPrefix + "/" + UUID.randomUUID().toString().replace("-", "") + "." + suffix;
        try (InputStream inputStream = multipartFile.getInputStream()) {
            return doUpload(objectKey, inputStream);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        }
    }

    protected abstract String doUpload(String objectKey, InputStream inputStream);

    private void validFile(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件不能为空");
        }
        if (multipartFile.getSize() > FileConstant.MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件过大");
        }
        String suffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        if (StringUtils.isBlank(suffix) || !ALLOW_SUFFIX_LIST.contains(suffix.toLowerCase())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误");
        }
    }
}
