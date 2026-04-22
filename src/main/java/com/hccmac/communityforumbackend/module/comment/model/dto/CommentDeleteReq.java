package com.hccmac.communityforumbackend.module.comment.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 评论删除请求
 */
@Data
public class CommentDeleteReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
}
