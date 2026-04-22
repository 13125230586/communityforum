package com.hccmac.communityforumbackend.module.file.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 上传文件视图对象
 */
@Data
public class UploadFileVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long fileId;

    private String fileUrl;

    private String fileName;

    private Long fileSize;

    private String uploadType;
}
