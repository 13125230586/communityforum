package com.hccmac.communityforumbackend.module.system.service.impl;

import org.springframework.beans.BeanUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hccmac.communityforumbackend.common.ErrorCode;
import com.hccmac.communityforumbackend.exception.ThrowUtils;
import com.hccmac.communityforumbackend.module.system.entity.ForumSystemConfig;
import com.hccmac.communityforumbackend.mapper.ForumSystemConfigMapper;
import com.hccmac.communityforumbackend.module.system.model.dto.SystemConfigQueryReq;
import com.hccmac.communityforumbackend.module.system.model.dto.SystemConfigUpdateReq;
import com.hccmac.communityforumbackend.module.system.model.vo.SystemConfigVO;
import com.hccmac.communityforumbackend.module.system.service.SystemConfigService;
import com.hccmac.communityforumbackend.module.user.entity.ForumUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统配置服务实现
 */
@Service
@Slf4j
public class SystemConfigServiceImpl extends ServiceImpl<ForumSystemConfigMapper, ForumSystemConfig> implements SystemConfigService {

    @Override
    public Page<SystemConfigVO> listConfigByPage(SystemConfigQueryReq systemConfigQueryReq) {
        ThrowUtils.throwIf(systemConfigQueryReq == null, ErrorCode.PARAMS_ERROR);
        QueryWrapper<ForumSystemConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(systemConfigQueryReq.getConfigGroup()), "configGroup", systemConfigQueryReq.getConfigGroup());
        queryWrapper.orderByAsc("configGroup");
        queryWrapper.orderByAsc("configKey");
        Page<ForumSystemConfig> configPage = this.page(new Page<>(systemConfigQueryReq.getCurrent(), systemConfigQueryReq.getPageSize()), queryWrapper);
        Page<SystemConfigVO> systemConfigVOPage = new Page<>(systemConfigQueryReq.getCurrent(), systemConfigQueryReq.getPageSize(), configPage.getTotal());
        systemConfigVOPage.setRecords(configPage.getRecords().stream().map(this::toSystemConfigVO).collect(Collectors.toList()));
        return systemConfigVOPage;
    }

    @Override
    public Boolean updateConfig(SystemConfigUpdateReq systemConfigUpdateReq, ForumUser loginUser) {
        ThrowUtils.throwIf(systemConfigUpdateReq == null || systemConfigUpdateReq.getId() == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null || loginUser.getId() == null, ErrorCode.NOT_LOGIN_ERROR);
        ForumSystemConfig forumSystemConfig = this.getById(systemConfigUpdateReq.getId());
        ThrowUtils.throwIf(forumSystemConfig == null, ErrorCode.NOT_FOUND_ERROR, "配置不存在");
        ForumSystemConfig updateConfig = new ForumSystemConfig();
        updateConfig.setId(systemConfigUpdateReq.getId());
        updateConfig.setConfigValue(systemConfigUpdateReq.getConfigValue());
        updateConfig.setConfigStatus(systemConfigUpdateReq.getConfigStatus());
        updateConfig.setRemark(systemConfigUpdateReq.getRemark());
        updateConfig.setUpdateUserId(loginUser.getId());
        boolean result = this.updateById(updateConfig);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新配置失败");
        log.info("更新配置 uid:{} configId:{}", loginUser.getId(), systemConfigUpdateReq.getId());
        return true;
    }

    @Override
    public List<SystemConfigVO> listConfigByGroup(String configGroup) {
        QueryWrapper<ForumSystemConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(configGroup), "configGroup", configGroup);
        queryWrapper.orderByAsc("configKey");
        return this.list(queryWrapper).stream().map(this::toSystemConfigVO).collect(Collectors.toList());
    }

    private SystemConfigVO toSystemConfigVO(ForumSystemConfig forumSystemConfig) {
        SystemConfigVO systemConfigVO = new SystemConfigVO();
        BeanUtils.copyProperties(forumSystemConfig, systemConfigVO);
        return systemConfigVO;
    }
}
