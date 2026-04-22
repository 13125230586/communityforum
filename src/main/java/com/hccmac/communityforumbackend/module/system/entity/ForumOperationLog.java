package com.hccmac.communityforumbackend.module.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 操作日志实体
 */
@TableName(value = "forum_operation_log")
@Data
public class ForumOperationLog implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long operatorUserId;
    private String operatorRole;
    private String bizType;
    private Long bizId;
    private String actionType;
    private String requestPath;
    private String requestParam;
    private Integer resultCode;
    private String resultMessage;
    private String operateIp;
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
