package com.hccmac.communityforumbackend.module.comment.model.dto;

import com.hccmac.communityforumbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评论分页查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommentQueryReq extends PageRequest {

    private static final long serialVersionUID = 1L;

    private Long postId;
    private Long rootCommentId;
}
