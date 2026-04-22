package com.hccmac.communityforumbackend.module.comment.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 评论新增请求
 */
@Data
public class CommentAddReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long postId;
    private Long rootCommentId;
    private Long parentCommentId;
    private Long replyUserId;
    private String contentType;
    private String content;
    private Integer anonymousFlag;
}
