package com.hccmac.communityforumbackend.module.interaction.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 点赞记录实体
 */
@TableName(value = "forum_like_record")
@Data
public class ForumLikeRecord implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String bizType;

    private Long bizId;

    private Long userId;

    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
