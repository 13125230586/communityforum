package com.hccmac.communityforumbackend.module.post.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 帖子标签关系实体
 */
@TableName(value = "forum_post_tag_rel")
@Data
public class ForumPostTagRel implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long postId;
    private Long tagId;
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
