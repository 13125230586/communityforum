package com.hccmac.communityforumbackend.module.report.service.impl;

import org.springframework.beans.BeanUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hccmac.communityforumbackend.common.ErrorCode;
import com.hccmac.communityforumbackend.exception.ThrowUtils;
import com.hccmac.communityforumbackend.module.comment.entity.ForumComment;
import com.hccmac.communityforumbackend.mapper.ForumCommentMapper;
import com.hccmac.communityforumbackend.module.post.entity.ForumPost;
import com.hccmac.communityforumbackend.mapper.ForumPostMapper;
import com.hccmac.communityforumbackend.module.report.entity.ForumReportRecord;
import com.hccmac.communityforumbackend.mapper.ForumReportRecordMapper;
import com.hccmac.communityforumbackend.module.report.model.dto.ReportAddReq;
import com.hccmac.communityforumbackend.module.report.model.dto.ReportProcessReq;
import com.hccmac.communityforumbackend.module.report.model.dto.ReportQueryReq;
import com.hccmac.communityforumbackend.module.report.model.vo.ReportVO;
import com.hccmac.communityforumbackend.module.report.service.ReportService;
import com.hccmac.communityforumbackend.module.user.entity.ForumUser;
import com.hccmac.communityforumbackend.module.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * 举报服务实现
 */
@Service
@Slf4j
public class ReportServiceImpl extends ServiceImpl<ForumReportRecordMapper, ForumReportRecord> implements ReportService {

    private static final String BIZ_TYPE_POST = "post";
    private static final String BIZ_TYPE_COMMENT = "comment";
    private static final int DEFAULT_PROCESS_STATUS = 0;

    @Resource
    private ForumPostMapper forumPostMapper;

    @Resource
    private ForumCommentMapper forumCommentMapper;

    @Resource
    private UserService userService;

    @Override
    public Long addReport(ReportAddReq reportAddReq, ForumUser loginUser) {
        ThrowUtils.throwIf(reportAddReq == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null || loginUser.getId() == null, ErrorCode.NOT_LOGIN_ERROR);
        ThrowUtils.throwIf(StringUtils.isAnyBlank(reportAddReq.getBizType(), reportAddReq.getReportType()), ErrorCode.PARAMS_ERROR, "举报参数不能为空");
        ThrowUtils.throwIf(reportAddReq.getBizId() == null, ErrorCode.PARAMS_ERROR, "业务ID不能为空");
        validBiz(reportAddReq.getBizType(), reportAddReq.getBizId());
        ForumReportRecord forumReportRecord = new ForumReportRecord();
        forumReportRecord.setBizType(reportAddReq.getBizType());
        forumReportRecord.setBizId(reportAddReq.getBizId());
        forumReportRecord.setReportUserId(loginUser.getId());
        forumReportRecord.setReportType(reportAddReq.getReportType());
        forumReportRecord.setReportReason(reportAddReq.getReportReason());
        forumReportRecord.setProcessStatus(DEFAULT_PROCESS_STATUS);
        boolean result = this.save(forumReportRecord);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "举报失败");
        updateReportCount(reportAddReq.getBizType(), reportAddReq.getBizId(), 1L);
        log.info("新增举报 uid:{} reportId:{} bizType:{} bizId:{}", loginUser.getId(), forumReportRecord.getId(), reportAddReq.getBizType(), reportAddReq.getBizId());
        return forumReportRecord.getId();
    }

    @Override
    public Page<ReportVO> listReportByPage(ReportQueryReq reportQueryReq) {
        ThrowUtils.throwIf(reportQueryReq == null, ErrorCode.PARAMS_ERROR);
        QueryWrapper<ForumReportRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(reportQueryReq.getBizType()), "bizType", reportQueryReq.getBizType());
        queryWrapper.eq(reportQueryReq.getProcessStatus() != null, "processStatus", reportQueryReq.getProcessStatus());
        queryWrapper.orderByDesc("createTime");
        Page<ForumReportRecord> reportPage = this.page(new Page<>(reportQueryReq.getCurrent(), reportQueryReq.getPageSize()), queryWrapper);
        Page<ReportVO> reportVOPage = new Page<>(reportQueryReq.getCurrent(), reportQueryReq.getPageSize(), reportPage.getTotal());
        reportVOPage.setRecords(reportPage.getRecords().stream().map(this::toReportVO).collect(Collectors.toList()));
        return reportVOPage;
    }

    @Override
    public Boolean processReport(ReportProcessReq reportProcessReq, ForumUser loginUser) {
        ThrowUtils.throwIf(reportProcessReq == null || reportProcessReq.getId() == null || reportProcessReq.getProcessStatus() == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null || loginUser.getId() == null, ErrorCode.NOT_LOGIN_ERROR);
        ForumReportRecord forumReportRecord = this.getById(reportProcessReq.getId());
        ThrowUtils.throwIf(forumReportRecord == null, ErrorCode.NOT_FOUND_ERROR, "举报不存在");
        forumReportRecord.setProcessStatus(reportProcessReq.getProcessStatus());
        forumReportRecord.setProcessRemark(reportProcessReq.getProcessRemark());
        forumReportRecord.setProcessUserId(loginUser.getId());
        forumReportRecord.setProcessTime(new Date());
        boolean result = this.updateById(forumReportRecord);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "处理举报失败");
        log.info("处理举报 uid:{} reportId:{} processStatus:{}", loginUser.getId(), reportProcessReq.getId(), reportProcessReq.getProcessStatus());
        return true;
    }

    private void validBiz(String bizType, Long bizId) {
        if (BIZ_TYPE_POST.equals(bizType)) {
            ThrowUtils.throwIf(forumPostMapper.selectById(bizId) == null, ErrorCode.NOT_FOUND_ERROR, "帖子不存在");
            return;
        }
        if (BIZ_TYPE_COMMENT.equals(bizType)) {
            ThrowUtils.throwIf(forumCommentMapper.selectById(bizId) == null, ErrorCode.NOT_FOUND_ERROR, "评论不存在");
            return;
        }
        ThrowUtils.throwIf(true, ErrorCode.PARAMS_ERROR, "业务类型错误");
    }

    private void updateReportCount(String bizType, Long bizId, Long delta) {
        if (BIZ_TYPE_POST.equals(bizType)) {
            ForumPost forumPost = forumPostMapper.selectById(bizId);
            if (forumPost != null) {
                forumPost.setReportCount(safeLong(forumPost.getReportCount()) + delta);
                forumPostMapper.updateById(forumPost);
            }
        }
    }

    private ReportVO toReportVO(ForumReportRecord forumReportRecord) {
        ReportVO reportVO = new ReportVO();
        BeanUtils.copyProperties(forumReportRecord, reportVO);
        ForumUser reportUser = userService.getById(forumReportRecord.getReportUserId());
        if (reportUser != null) {
            reportVO.setReportUserName(reportUser.getUserName());
        }
        if (forumReportRecord.getProcessUserId() != null) {
            ForumUser processUser = userService.getById(forumReportRecord.getProcessUserId());
            if (processUser != null) {
                reportVO.setProcessUserName(processUser.getUserName());
            }
        }
        return reportVO;
    }

    private long safeLong(Long value) {
        return value == null ? 0L : value;
    }
}
