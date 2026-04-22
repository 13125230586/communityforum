package com.hccmac.communityforumbackend.module.post.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 帖子标记更新请求
 */
@Data
public class PostFlagUpdateReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer flagValue;
}
