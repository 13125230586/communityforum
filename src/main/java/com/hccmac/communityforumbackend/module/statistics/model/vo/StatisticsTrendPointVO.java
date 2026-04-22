package com.hccmac.communityforumbackend.module.statistics.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 统计趋势点视图对象
 */
@Data
public class StatisticsTrendPointVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String statDate;
    private long newUserCount;
    private long newPostCount;
    private long publishPostCount;
    private long newCommentCount;
    private long newLikeCount;
    private long newCollectCount;
    private long reportCount;
}
