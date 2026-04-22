package com.hccmac.communityforumbackend.module.statistics.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 统计趋势请求
 */
@Data
public class StatisticsTrendQueryReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer dayCount = 7;
    private String startDate;
    private String endDate;
    private Integer rankSize = 10;
}
