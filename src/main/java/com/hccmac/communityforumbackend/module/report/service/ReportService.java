package com.hccmac.communityforumbackend.module.report.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hccmac.communityforumbackend.module.report.entity.ForumReportRecord;
import com.hccmac.communityforumbackend.module.report.model.dto.ReportAddReq;
import com.hccmac.communityforumbackend.module.report.model.dto.ReportProcessReq;
import com.hccmac.communityforumbackend.module.report.model.dto.ReportQueryReq;
import com.hccmac.communityforumbackend.module.report.model.vo.ReportVO;
import com.hccmac.communityforumbackend.module.user.entity.ForumUser;

/**
 * 举报服务
 */
public interface ReportService extends IService<ForumReportRecord> {

    Long addReport(ReportAddReq reportAddReq, ForumUser loginUser);

    Page<ReportVO> listReportByPage(ReportQueryReq reportQueryReq);

    Boolean processReport(ReportProcessReq reportProcessReq, ForumUser loginUser);
}
