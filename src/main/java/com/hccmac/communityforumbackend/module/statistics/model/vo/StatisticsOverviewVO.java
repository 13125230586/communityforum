package com.hccmac.communityforumbackend.module.statistics.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 统计概览视图对象
 */
@Data
public class StatisticsOverviewVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private long totalUserCount;
    private long totalBoardCount;
    private long totalPostCount;
    private long totalCommentCount;
    private long totalReportCount;
    private long waitAuditPostCount;
    private long waitAuditCommentCount;
    private long waitProcessReportCount;
    private long todayNewUserCount;
    private long todayNewPostCount;
    private long todayNewCommentCount;
}
