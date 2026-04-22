package com.hccmac.communityforumbackend.module.report.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 举报视图对象
 */
@Data
public class ReportVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String bizType;
    private Long bizId;
    private Long reportUserId;
    private String reportUserName;
    private String reportType;
    private String reportReason;
    private Integer processStatus;
    private Long processUserId;
    private String processUserName;
    private String processRemark;
    private Date processTime;
    private Date createTime;
}
