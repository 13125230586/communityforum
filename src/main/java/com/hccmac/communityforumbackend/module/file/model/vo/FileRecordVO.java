package com.hccmac.communityforumbackend.module.file.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 文件记录视图对象
 */
@Data
public class FileRecordVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long uploaderUserId;
    private String uploaderUserName;
    private String uploadType;
    private String storageType;
    private String fileName;
    private String fileUrl;
    private Long fileSize;
    private String fileSuffix;
    private String bizCode;
    private Integer fileStatus;
    private Date createTime;
}
