package com.hccmac.communityforumbackend.module.report.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 举报新增请求
 */
@Data
public class ReportAddReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private String bizType;

    private Long bizId;

    private String reportType;

    private String reportReason;
}
