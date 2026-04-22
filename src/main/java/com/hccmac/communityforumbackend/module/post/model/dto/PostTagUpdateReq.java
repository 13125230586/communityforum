package com.hccmac.communityforumbackend.module.post.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 标签更新请求
 */
@Data
public class PostTagUpdateReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String tagName;
    private Integer sortOrder;
    private Integer tagStatus;
}
