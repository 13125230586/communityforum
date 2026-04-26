package com.hccmac.communityforumbackend.module.comment.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 评论视图对象
 */
@Data
public class CommentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long postId;
    private Long userId;
    private String userName;
    private String userAvatar;
    private Long rootCommentId;
    private Long parentCommentId;
    private Long replyUserId;
    private String replyUserName;
    private String contentType;
    private String content;
    private Integer auditStatus;
    private Integer anonymousFlag;
    private Long likeCount;
    private Long childCount;
    private Date createTime;
}
