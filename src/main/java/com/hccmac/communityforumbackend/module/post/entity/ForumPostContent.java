package com.hccmac.communityforumbackend.module.post.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 帖子内容实体
 */
@TableName(value = "forum_post_content")
@Data
public class ForumPostContent implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long postId;
    private String contentType;
    private String content;
    private String contentText;
    private Integer wordCount;
    private Date createTime;
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
