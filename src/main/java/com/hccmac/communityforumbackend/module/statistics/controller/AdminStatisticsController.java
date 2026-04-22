package com.hccmac.communityforumbackend.module.statistics.controller;

import com.hccmac.communityforumbackend.annotation.AuthCheck;
import com.hccmac.communityforumbackend.common.BaseResponse;
import com.hccmac.communityforumbackend.common.ResultUtils;
import com.hccmac.communityforumbackend.constant.UserConstant;
import com.hccmac.communityforumbackend.module.statistics.model.dto.StatisticsTrendQueryReq;
import com.hccmac.communityforumbackend.module.statistics.model.vo.StatisticsBoardRankVO;
import com.hccmac.communityforumbackend.module.statistics.model.vo.StatisticsOverviewVO;
import com.hccmac.communityforumbackend.module.statistics.model.vo.StatisticsTrendPointVO;
import com.hccmac.communityforumbackend.module.statistics.service.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 后台统计接口
 */
@RestController
@RequestMapping("/admin/statistics")
public class AdminStatisticsController {

    @Resource
    private StatisticsService statisticsService;

    @GetMapping("/overview")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<StatisticsOverviewVO> getOverview() {
        return ResultUtils.success(statisticsService.getOverview());
    }

    @GetMapping("/trend")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<StatisticsTrendPointVO>> listTrend(StatisticsTrendQueryReq statisticsTrendQueryReq) {
        return ResultUtils.success(statisticsService.listTrend(statisticsTrendQueryReq));
    }

    @GetMapping("/board/rank")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<StatisticsBoardRankVO>> listBoardRank(StatisticsTrendQueryReq statisticsTrendQueryReq) {
        return ResultUtils.success(statisticsService.listBoardRank(statisticsTrendQueryReq));
    }
}
