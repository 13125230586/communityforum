package com.hccmac.communityforumbackend.module.statistics.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hccmac.communityforumbackend.module.statistics.entity.ForumStatisticsDay;
import com.hccmac.communityforumbackend.module.statistics.model.dto.StatisticsTrendQueryReq;
import com.hccmac.communityforumbackend.module.statistics.model.vo.StatisticsBoardRankVO;
import com.hccmac.communityforumbackend.module.statistics.model.vo.StatisticsOverviewVO;
import com.hccmac.communityforumbackend.module.statistics.model.vo.StatisticsTrendPointVO;

import java.util.List;

/**
 * 统计服务
 */
public interface StatisticsService extends IService<ForumStatisticsDay> {

    /**
     * 获取统计概览
     *
     * @return 统计概览
     */
    StatisticsOverviewVO getOverview();

    /**
     * 获取趋势数据
     *
     * @param statisticsTrendQueryReq 趋势请求
     * @return 趋势列表
     */
    List<StatisticsTrendPointVO> listTrend(StatisticsTrendQueryReq statisticsTrendQueryReq);

    /**
     * 获取板块排行
     *
     * @param statisticsTrendQueryReq 排行请求
     * @return 板块排行
     */
    List<StatisticsBoardRankVO> listBoardRank(StatisticsTrendQueryReq statisticsTrendQueryReq);
}
