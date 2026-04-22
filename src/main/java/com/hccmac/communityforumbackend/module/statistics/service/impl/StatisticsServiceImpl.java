package com.hccmac.communityforumbackend.module.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hccmac.communityforumbackend.constant.PostConstant;
import com.hccmac.communityforumbackend.module.board.entity.ForumBoard;
import com.hccmac.communityforumbackend.mapper.ForumBoardMapper;
import com.hccmac.communityforumbackend.module.comment.entity.ForumComment;
import com.hccmac.communityforumbackend.mapper.ForumCommentMapper;
import com.hccmac.communityforumbackend.module.interaction.entity.ForumCollectRecord;
import com.hccmac.communityforumbackend.module.interaction.entity.ForumLikeRecord;
import com.hccmac.communityforumbackend.mapper.ForumCollectRecordMapper;
import com.hccmac.communityforumbackend.mapper.ForumLikeRecordMapper;
import com.hccmac.communityforumbackend.module.post.entity.ForumPost;
import com.hccmac.communityforumbackend.mapper.ForumPostMapper;
import com.hccmac.communityforumbackend.module.report.entity.ForumReportRecord;
import com.hccmac.communityforumbackend.mapper.ForumReportRecordMapper;
import com.hccmac.communityforumbackend.module.statistics.entity.ForumStatisticsDay;
import com.hccmac.communityforumbackend.mapper.ForumStatisticsDayMapper;
import com.hccmac.communityforumbackend.module.statistics.model.dto.StatisticsTrendQueryReq;
import com.hccmac.communityforumbackend.module.statistics.model.vo.StatisticsBoardRankVO;
import com.hccmac.communityforumbackend.module.statistics.model.vo.StatisticsOverviewVO;
import com.hccmac.communityforumbackend.module.statistics.model.vo.StatisticsTrendPointVO;
import com.hccmac.communityforumbackend.module.statistics.service.StatisticsService;
import com.hccmac.communityforumbackend.module.user.entity.ForumUser;
import com.hccmac.communityforumbackend.mapper.ForumUserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 统计服务实现
 */
@Service
public class StatisticsServiceImpl extends ServiceImpl<ForumStatisticsDayMapper, ForumStatisticsDay> implements StatisticsService {

    private static final int DEFAULT_DAY_COUNT = 7;
    private static final int DEFAULT_RANK_SIZE = 10;
    private static final int WAIT_PROCESS_STATUS = 0;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Resource
    private ForumUserMapper forumUserMapper;

    @Resource
    private ForumBoardMapper forumBoardMapper;

    @Resource
    private ForumPostMapper forumPostMapper;

    @Resource
    private ForumCommentMapper forumCommentMapper;

    @Resource
    private ForumReportRecordMapper forumReportRecordMapper;

    @Resource
    private ForumLikeRecordMapper forumLikeRecordMapper;

    @Resource
    private ForumCollectRecordMapper forumCollectRecordMapper;

    @Override
    public StatisticsOverviewVO getOverview() {
        StatisticsOverviewVO statisticsOverviewVO = new StatisticsOverviewVO();
        statisticsOverviewVO.setTotalUserCount(forumUserMapper.selectCount(null));
        statisticsOverviewVO.setTotalBoardCount(forumBoardMapper.selectCount(null));
        statisticsOverviewVO.setTotalPostCount(forumPostMapper.selectCount(null));
        statisticsOverviewVO.setTotalCommentCount(forumCommentMapper.selectCount(null));
        statisticsOverviewVO.setTotalReportCount(forumReportRecordMapper.selectCount(null));
        statisticsOverviewVO.setWaitAuditPostCount(countByField(forumPostMapper, "auditStatus", PostConstant.AUDIT_STATUS_WAIT));
        statisticsOverviewVO.setWaitAuditCommentCount(countByField(forumCommentMapper, "auditStatus", PostConstant.AUDIT_STATUS_WAIT));
        statisticsOverviewVO.setWaitProcessReportCount(countByField(forumReportRecordMapper, "processStatus", WAIT_PROCESS_STATUS));
        statisticsOverviewVO.setTodayNewUserCount(countTodayUser());
        statisticsOverviewVO.setTodayNewPostCount(countTodayPost());
        statisticsOverviewVO.setTodayNewCommentCount(countTodayComment());
        return statisticsOverviewVO;
    }

    @Override
    public List<StatisticsTrendPointVO> listTrend(StatisticsTrendQueryReq statisticsTrendQueryReq) {
        DateRange dateRange = buildDateRange(statisticsTrendQueryReq);
        Map<String, StatisticsTrendPointVO> trendMap = buildEmptyTrendMap(dateRange);
        fillCountMap(trendMap, queryCountByDate(forumUserMapper, buildDateQueryWrapper("createTime", dateRange), "newUserCount"));
        fillCountMap(trendMap, queryCountByDate(forumPostMapper, buildDateQueryWrapper("createTime", dateRange), "newPostCount"));
        fillCountMap(trendMap, queryCountByDate(forumPostMapper, buildDateQueryWrapper("publishTime", dateRange), "publishPostCount"));
        fillCountMap(trendMap, queryCountByDate(forumCommentMapper, buildDateQueryWrapper("createTime", dateRange), "newCommentCount"));
        fillCountMap(trendMap, queryCountByDate(forumLikeRecordMapper, buildDateQueryWrapper("createTime", dateRange), "newLikeCount"));
        fillCountMap(trendMap, queryCountByDate(forumCollectRecordMapper, buildDateQueryWrapper("createTime", dateRange), "newCollectCount"));
        fillCountMap(trendMap, queryCountByDate(forumReportRecordMapper, buildDateQueryWrapper("createTime", dateRange), "reportCount"));
        return new ArrayList<>(trendMap.values());
    }

    @Override
    public List<StatisticsBoardRankVO> listBoardRank(StatisticsTrendQueryReq statisticsTrendQueryReq) {
        int rankSize = statisticsTrendQueryReq == null || statisticsTrendQueryReq.getRankSize() == null || statisticsTrendQueryReq.getRankSize() <= 0
            ? DEFAULT_RANK_SIZE : statisticsTrendQueryReq.getRankSize();
        QueryWrapper<ForumBoard> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("postCount");
        queryWrapper.orderByDesc("commentCount");
        queryWrapper.orderByAsc("sortOrder");
        queryWrapper.last("limit " + rankSize);
        return forumBoardMapper.selectList(queryWrapper).stream().map(this::toBoardRankVO).collect(Collectors.toList());
    }

    private <T> long countByField(com.baomidou.mybatisplus.core.mapper.BaseMapper<T> baseMapper, String fieldName, Object fieldValue) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(fieldName, fieldValue);
        return baseMapper.selectCount(queryWrapper);
    }

    private long countTodayUser() {
        return countToday(forumUserMapper, "createTime");
    }

    private long countTodayPost() {
        return countToday(forumPostMapper, "createTime");
    }

    private long countTodayComment() {
        return countToday(forumCommentMapper, "createTime");
    }

    private <T> long countToday(com.baomidou.mybatisplus.core.mapper.BaseMapper<T> baseMapper, String fieldName) {
        LocalDate today = LocalDate.now();
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge(fieldName, toDate(today.atStartOfDay()));
        queryWrapper.lt(fieldName, toDate(today.plusDays(1).atStartOfDay()));
        return baseMapper.selectCount(queryWrapper);
    }

    private <T> QueryWrapper<T> buildDateQueryWrapper(String fieldName, DateRange dateRange) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DATE_FORMAT(" + fieldName + ", '%Y-%m-%d') statDate", "COUNT(*) countValue");
        queryWrapper.isNotNull(fieldName);
        queryWrapper.ge(fieldName, toDate(dateRange.getStart().atStartOfDay()));
        queryWrapper.lt(fieldName, toDate(dateRange.getEnd().plusDays(1).atStartOfDay()));
        queryWrapper.groupBy("DATE_FORMAT(" + fieldName + ", '%Y-%m-%d')");
        queryWrapper.orderByAsc("DATE_FORMAT(" + fieldName + ", '%Y-%m-%d')");
        return queryWrapper;
    }

    private <T> Map<String, Long> queryCountByDate(com.baomidou.mybatisplus.core.mapper.BaseMapper<T> baseMapper, QueryWrapper<T> queryWrapper, String fieldKey) {
        Map<String, Long> countMap = new LinkedHashMap<>();
        List<Map<String, Object>> mapList = baseMapper.selectMaps(queryWrapper);
        for (Map<String, Object> map : mapList) {
            String statDate = String.valueOf(map.get("statDate"));
            Long countValue = Long.valueOf(String.valueOf(map.get("countValue")));
            countMap.put(statDate + "#" + fieldKey, countValue);
        }
        return countMap;
    }

    private void fillCountMap(Map<String, StatisticsTrendPointVO> trendMap, Map<String, Long> countMap) {
        for (Map.Entry<String, Long> entry : countMap.entrySet()) {
            String[] keyArr = entry.getKey().split("#");
            if (keyArr.length != 2) {
                continue;
            }
            StatisticsTrendPointVO statisticsTrendPointVO = trendMap.get(keyArr[0]);
            if (statisticsTrendPointVO == null) {
                continue;
            }
            long value = entry.getValue() == null ? 0L : entry.getValue();
            if ("newUserCount".equals(keyArr[1])) {
                statisticsTrendPointVO.setNewUserCount(value);
            } else if ("newPostCount".equals(keyArr[1])) {
                statisticsTrendPointVO.setNewPostCount(value);
            } else if ("publishPostCount".equals(keyArr[1])) {
                statisticsTrendPointVO.setPublishPostCount(value);
            } else if ("newCommentCount".equals(keyArr[1])) {
                statisticsTrendPointVO.setNewCommentCount(value);
            } else if ("newLikeCount".equals(keyArr[1])) {
                statisticsTrendPointVO.setNewLikeCount(value);
            } else if ("newCollectCount".equals(keyArr[1])) {
                statisticsTrendPointVO.setNewCollectCount(value);
            } else if ("reportCount".equals(keyArr[1])) {
                statisticsTrendPointVO.setReportCount(value);
            }
        }
    }

    private Map<String, StatisticsTrendPointVO> buildEmptyTrendMap(DateRange dateRange) {
        Map<String, StatisticsTrendPointVO> trendMap = new LinkedHashMap<>();
        LocalDate currentDate = dateRange.getStart();
        while (!currentDate.isAfter(dateRange.getEnd())) {
            StatisticsTrendPointVO statisticsTrendPointVO = new StatisticsTrendPointVO();
            statisticsTrendPointVO.setStatDate(currentDate.format(DATE_FORMATTER));
            trendMap.put(statisticsTrendPointVO.getStatDate(), statisticsTrendPointVO);
            currentDate = currentDate.plusDays(1);
        }
        return trendMap;
    }

    private StatisticsBoardRankVO toBoardRankVO(ForumBoard forumBoard) {
        StatisticsBoardRankVO statisticsBoardRankVO = new StatisticsBoardRankVO();
        BeanUtils.copyProperties(forumBoard, statisticsBoardRankVO);
        statisticsBoardRankVO.setBoardId(forumBoard.getId());
        return statisticsBoardRankVO;
    }

    private DateRange buildDateRange(StatisticsTrendQueryReq statisticsTrendQueryReq) {
        int dayCount = statisticsTrendQueryReq == null || statisticsTrendQueryReq.getDayCount() == null || statisticsTrendQueryReq.getDayCount() <= 0
            ? DEFAULT_DAY_COUNT : statisticsTrendQueryReq.getDayCount();
        LocalDate endDate = parseDate(statisticsTrendQueryReq == null ? null : statisticsTrendQueryReq.getEndDate(), LocalDate.now());
        LocalDate startDate = parseDate(statisticsTrendQueryReq == null ? null : statisticsTrendQueryReq.getStartDate(), endDate.minusDays(dayCount - 1L));
        if (startDate.isAfter(endDate)) {
            LocalDate tempDate = startDate;
            startDate = endDate;
            endDate = tempDate;
        }
        return new DateRange(startDate, endDate);
    }

    private LocalDate parseDate(String value, LocalDate defaultDate) {
        if (StringUtils.isBlank(value)) {
            return defaultDate;
        }
        return LocalDate.parse(value, DATE_FORMATTER);
    }

    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private static class DateRange {

        private final LocalDate start;
        private final LocalDate end;

        private DateRange(LocalDate start, LocalDate end) {
            this.start = start;
            this.end = end;
        }

        public LocalDate getStart() {
            return start;
        }

        public LocalDate getEnd() {
            return end;
        }
    }
}
