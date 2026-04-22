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
 * 帖子标签实体
 */
@TableName(value = "forum_post_tag")
@Data
public class ForumPostTag implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String tagName;
    private Integer sortOrder;
    private Integer tagStatus;
    private Long useCount;
    private Date createTime;
    private Date updateTime;
    @TableLogic
    private Integer deleteFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
