package com.hccmac.communityforumbackend.module.post.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 帖子审核请求
 */
@Data
public class PostAuditReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer auditStatus;
    private String auditRemark;
}
