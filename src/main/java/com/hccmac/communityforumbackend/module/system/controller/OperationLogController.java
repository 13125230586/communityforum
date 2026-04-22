package com.hccmac.communityforumbackend.module.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hccmac.communityforumbackend.annotation.AuthCheck;
import com.hccmac.communityforumbackend.common.BaseResponse;
import com.hccmac.communityforumbackend.common.ResultUtils;
import com.hccmac.communityforumbackend.constant.UserConstant;
import com.hccmac.communityforumbackend.module.system.model.dto.OperationLogQueryReq;
import com.hccmac.communityforumbackend.module.system.model.vo.OperationLogVO;
import com.hccmac.communityforumbackend.module.system.service.OperationLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 操作日志接口
 */
@RestController
@RequestMapping("/admin/system/operationLog")
public class OperationLogController {

    @Resource
    private OperationLogService operationLogService;

    @GetMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<OperationLogVO>> listOperationLogByPage(OperationLogQueryReq operationLogQueryReq) {
        return ResultUtils.success(operationLogService.listOperationLogByPage(operationLogQueryReq));
    }
}
