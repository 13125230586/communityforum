package com.hccmac.communityforumbackend.module.comment.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 评论审核请求
 */
@Data
public class CommentAuditReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer auditStatus;
}
