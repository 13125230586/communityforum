package com.hccmac.communityforumbackend.module.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hccmac.communityforumbackend.annotation.AuthCheck;
import com.hccmac.communityforumbackend.annotation.OperationLog;
import com.hccmac.communityforumbackend.common.BaseResponse;
import com.hccmac.communityforumbackend.common.ResultUtils;
import com.hccmac.communityforumbackend.constant.OperationLogConstant;
import com.hccmac.communityforumbackend.constant.UserConstant;
import com.hccmac.communityforumbackend.module.system.model.dto.SystemConfigQueryReq;
import com.hccmac.communityforumbackend.module.system.model.dto.SystemConfigUpdateReq;
import com.hccmac.communityforumbackend.module.system.model.vo.SystemConfigVO;
import com.hccmac.communityforumbackend.module.system.service.SystemConfigService;
import com.hccmac.communityforumbackend.module.user.entity.ForumUser;
import com.hccmac.communityforumbackend.module.user.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 系统配置接口
 */
@RestController
@RequestMapping("/admin/system/config")
public class SystemConfigController {

    @Resource
    private SystemConfigService systemConfigService;

    @Resource
    private UserService userService;

    @GetMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<SystemConfigVO>> listConfigByPage(SystemConfigQueryReq systemConfigQueryReq) {
        return ResultUtils.success(systemConfigService.listConfigByPage(systemConfigQueryReq));
    }

    @GetMapping("/list")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<SystemConfigVO>> listConfigByGroup(@RequestParam(required = false) String configGroup) {
        return ResultUtils.success(systemConfigService.listConfigByGroup(configGroup));
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @OperationLog(bizType = OperationLogConstant.BIZ_TYPE_SYSTEM_CONFIG, actionType = "update")
    public BaseResponse<Boolean> updateConfig(@RequestBody SystemConfigUpdateReq systemConfigUpdateReq, HttpServletRequest request) {
        ForumUser loginUser = userService.getLoginUserEntity(request);
        return ResultUtils.success(systemConfigService.updateConfig(systemConfigUpdateReq, loginUser));
    }
}
