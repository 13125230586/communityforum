package com.hccmac.communityforumbackend.module.statistics.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 板块排行视图对象
 */
@Data
public class StatisticsBoardRankVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long boardId;
    private String boardName;
    private Long postCount;
    private Long commentCount;
    private Long followCount;
}
