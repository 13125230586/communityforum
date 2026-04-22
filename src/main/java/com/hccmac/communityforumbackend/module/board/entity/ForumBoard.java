package com.hccmac.communityforumbackend.module.board.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 板块实体
 */
@TableName(value = "forum_board")
@Data
public class ForumBoard implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long categoryId;
    private String boardName;
    private String boardCode;
    private String boardIcon;
    private String boardDescription;
    private Integer sortOrder;
    private Integer boardStatus;
    private Integer postAuditFlag;
    private Integer commentAuditFlag;
    private Long postCount;
    private Long commentCount;
    private Long followCount;
    private Long createUserId;
    private Date createTime;
    private Date updateTime;
    @TableLogic
    private Integer deleteFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
