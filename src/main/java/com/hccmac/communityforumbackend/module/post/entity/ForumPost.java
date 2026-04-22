package com.hccmac.communityforumbackend.module.post.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 帖子实体
 */
@TableName(value = "forum_post")
@Data
public class ForumPost implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long boardId;
    private Long userId;
    private String postTitle;
    private String postSummary;
    private String coverImage;
    private Integer postSourceType;
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
    private Date lastCommentTime;
    private Date publishTime;
    private Long auditUserId;
    private String auditRemark;
    private Date auditTime;
    private Date editTime;
    private Date createTime;
    private Date updateTime;
    @TableLogic
    private Integer deleteFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
