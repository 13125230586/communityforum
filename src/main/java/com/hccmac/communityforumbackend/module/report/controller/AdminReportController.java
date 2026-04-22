package com.hccmac.communityforumbackend.module.report.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hccmac.communityforumbackend.annotation.AuthCheck;
import com.hccmac.communityforumbackend.annotation.OperationLog;
import com.hccmac.communityforumbackend.common.BaseResponse;
import com.hccmac.communityforumbackend.common.ResultUtils;
import com.hccmac.communityforumbackend.constant.OperationLogConstant;
import com.hccmac.communityforumbackend.constant.UserConstant;
import com.hccmac.communityforumbackend.module.report.model.dto.ReportProcessReq;
import com.hccmac.communityforumbackend.module.report.model.dto.ReportQueryReq;
import com.hccmac.communityforumbackend.module.report.model.vo.ReportVO;
import com.hccmac.communityforumbackend.module.report.service.ReportService;
import com.hccmac.communityforumbackend.module.user.entity.ForumUser;
import com.hccmac.communityforumbackend.module.user.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 后台举报接口
 */
@RestController
@RequestMapping("/admin/report")
public class AdminReportController {

    @Resource
    private ReportService reportService;

    @Resource
    private UserService userService;

    @GetMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<ReportVO>> listReportByPage(ReportQueryReq reportQueryReq) {
        return ResultUtils.success(reportService.listReportByPage(reportQueryReq));
    }

    @PostMapping("/process")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @OperationLog(bizType = OperationLogConstant.BIZ_TYPE_REPORT, actionType = "process")
    public BaseResponse<Boolean> processReport(@RequestBody ReportProcessReq reportProcessReq, HttpServletRequest request) {
        ForumUser loginUser = userService.getLoginUserEntity(request);
        return ResultUtils.success(reportService.processReport(reportProcessReq, loginUser));
    }
}
