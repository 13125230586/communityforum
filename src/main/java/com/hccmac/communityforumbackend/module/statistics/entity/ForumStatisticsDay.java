package com.hccmac.communityforumbackend.module.statistics.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 日统计实体
 */
@TableName(value = "forum_statistics_day")
@Data
public class ForumStatisticsDay implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Date statDate;
    private Long newUserCount;
    private Long activeUserCount;
    private Long newPostCount;
    private Long publishPostCount;
    private Long newCommentCount;
    private Long newLikeCount;
    private Long newCollectCount;
    private Long reportCount;
    private Date createTime;
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
