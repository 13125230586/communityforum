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
 * 板块分类实体
 */
@TableName(value = "forum_board_category")
@Data
public class ForumBoardCategory implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String categoryName;

    private String categoryIcon;

    private Integer sortOrder;

    private Integer categoryStatus;

    private Long boardCount;

    private Date createTime;

    private Date updateTime;

    @TableLogic
    private Integer deleteFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
