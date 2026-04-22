package com.hccmac.communityforumbackend.module.post.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 标签新增请求
 */
@Data
public class PostTagAddReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tagName;
    private Integer sortOrder;
    private Integer tagStatus;
}
