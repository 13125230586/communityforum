package com.hccmac.communityforumbackend.module.post.model.dto;

import com.hccmac.communityforumbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 帖子分页查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostQueryReq extends PageRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 排序方式 comprehensive 综合排序 latest 最新发布
     */
    private String sortType;

    private Long boardId;
    private String postTitle;
    private Integer postStatus;
    private Integer auditStatus;
    private Long userId;
}
