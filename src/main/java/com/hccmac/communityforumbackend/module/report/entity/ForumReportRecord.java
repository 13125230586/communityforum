package com.hccmac.communityforumbackend.module.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 举报记录实体
 */
@TableName(value = "forum_report_record")
@Data
public class ForumReportRecord implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String bizType;

    private Long bizId;

    private Long reportUserId;

    private String reportType;

    private String reportReason;

    private Integer processStatus;

    private Long processUserId;

    private String processRemark;

    private Date processTime;

    private Date createTime;

    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
