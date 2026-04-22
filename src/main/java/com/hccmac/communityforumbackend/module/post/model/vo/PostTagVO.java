package com.hccmac.communityforumbackend.module.post.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 标签视图对象
 */
@Data
public class PostTagVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String tagName;
    private Integer sortOrder;
    private Integer tagStatus;
    private Long useCount;
    private Date createTime;
}
