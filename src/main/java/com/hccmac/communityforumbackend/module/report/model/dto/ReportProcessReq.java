package com.hccmac.communityforumbackend.module.report.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 举报处理请求
 */
@Data
public class ReportProcessReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Integer processStatus;

    private String processRemark;
}
