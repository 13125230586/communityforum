package com.hccmac.communityforumbackend.module.post.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 帖子视图对象
 */
@Data
public class PostVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long boardId;
    private String boardName;
    private Long userId;
    private String userName;
    private String userAvatar;
    private String postTitle;
    private String postSummary;
    private String coverImage;
    private Integer postStatus;
    private Integer auditStatus;
    private Integer anonymousFlag;
    private Integer topFlag;
    private Integer essenceFlag;
    private Integer commentAllowedFlag;
    private Long viewCount;
    private Long commentCount;
    private Long likeCount;
    private Long collectCount;
    private Long reportCount;
    private Long hotScore;
    private Date publishTime;
    private Date createTime;
    private List<Long> tagIdList;
    private List<String> tagNameList;
}
