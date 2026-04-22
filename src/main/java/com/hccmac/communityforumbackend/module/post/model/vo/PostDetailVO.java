package com.hccmac.communityforumbackend.module.post.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 帖子详情视图对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostDetailVO extends PostVO {

    private static final long serialVersionUID = 1L;

    private String contentType;

    private String content;

    private String contentText;

    private Integer wordCount;

    private Date lastCommentTime;

    private Date editTime;
}
