package com.hccmac.communityforumbackend.module.post.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 帖子新增请求
 */
@Data
public class PostAddReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long boardId;
    private String postTitle;
    private String postSummary;
    private String coverImage;
    private String contentType;
    private String content;
    private Integer anonymousFlag;
    private Integer commentAllowedFlag;
    private List<Long> tagIdList;
}
