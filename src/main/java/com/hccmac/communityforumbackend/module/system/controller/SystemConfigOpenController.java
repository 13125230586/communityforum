package com.hccmac.communityforumbackend.module.system.controller;

import com.hccmac.communityforumbackend.common.BaseResponse;
import com.hccmac.communityforumbackend.common.ResultUtils;
import com.hccmac.communityforumbackend.module.system.model.vo.SystemConfigVO;
import com.hccmac.communityforumbackend.module.system.service.SystemConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 前台系统配置接口
 */
@RestController
@RequestMapping("/system/config")
public class SystemConfigOpenController {

    @Resource
    private SystemConfigService systemConfigService;

    @GetMapping("/public/list")
    public BaseResponse<List<SystemConfigVO>> listPublicConfigByGroup(@RequestParam(required = false) String configGroup) {
        return ResultUtils.success(systemConfigService.listPublicConfigByGroup(configGroup));
    }
}
