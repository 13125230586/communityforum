package com.hccmac.communityforumbackend.module.system.model.dto;

import com.hccmac.communityforumbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统配置查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SystemConfigQueryReq extends PageRequest {

    private static final long serialVersionUID = 1L;

    private String configGroup;
}
