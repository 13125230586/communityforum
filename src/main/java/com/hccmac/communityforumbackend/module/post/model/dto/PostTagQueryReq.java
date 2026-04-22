package com.hccmac.communityforumbackend.module.post.model.dto;

import com.hccmac.communityforumbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 标签分页请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostTagQueryReq extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tagName;
    private Integer tagStatus;
}
