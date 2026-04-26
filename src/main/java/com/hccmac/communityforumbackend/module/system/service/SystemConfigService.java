package com.hccmac.communityforumbackend.module.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hccmac.communityforumbackend.module.system.entity.ForumSystemConfig;
import com.hccmac.communityforumbackend.module.system.model.dto.SystemConfigQueryReq;
import com.hccmac.communityforumbackend.module.system.model.dto.SystemConfigUpdateReq;
import com.hccmac.communityforumbackend.module.system.model.vo.SystemConfigVO;
import com.hccmac.communityforumbackend.module.user.entity.ForumUser;

import java.util.List;

/**
 * 系统配置服务
 */
public interface SystemConfigService extends IService<ForumSystemConfig> {

    Page<SystemConfigVO> listConfigByPage(SystemConfigQueryReq systemConfigQueryReq);

    Boolean updateConfig(SystemConfigUpdateReq systemConfigUpdateReq, ForumUser loginUser);

    List<SystemConfigVO> listConfigByGroup(String configGroup);

    List<SystemConfigVO> listPublicConfigByGroup(String configGroup);
}
