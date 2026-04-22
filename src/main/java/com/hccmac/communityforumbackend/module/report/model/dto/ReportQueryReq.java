package com.hccmac.communityforumbackend.module.report.model.dto;

import com.hccmac.communityforumbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 举报分页查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ReportQueryReq extends PageRequest {

    private static final long serialVersionUID = 1L;

    private String bizType;

    private Integer processStatus;
}
