package com.hccmac.communityforumbackend.module.comment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 评论实体
 */
@TableName(value = "forum_comment")
@Data
public class ForumComment implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long postId;
    private Long userId;
    private Long rootCommentId;
    private Long parentCommentId;
    private Long replyUserId;
    private String contentType;
    private String content;
    private Integer commentStatus;
    private Integer auditStatus;
    private Integer anonymousFlag;
    private Long likeCount;
    private Long childCount;
    private Date createTime;
    private Date updateTime;
    @TableLogic
    private Integer deleteFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
