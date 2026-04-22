package com.hccmac.communityforumbackend.module.file.model.dto;

import com.hccmac.communityforumbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 文件记录分页请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FileRecordQueryReq extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long uploaderUserId;
    private String uploadType;
    private Integer fileStatus;
    private String fileName;
}
