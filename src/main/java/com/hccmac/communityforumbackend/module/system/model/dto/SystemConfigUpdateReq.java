package com.hccmac.communityforumbackend.module.system.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 系统配置更新请求
 */
@Data
public class SystemConfigUpdateReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String configValue;

    private Integer configStatus;

    private String remark;
}
