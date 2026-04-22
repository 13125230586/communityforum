package com.hccmac.communityforumbackend.module.system.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 系统配置视图对象
 */
@Data
public class SystemConfigVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String configGroup;
    private String configKey;
    private String configName;
    private String configValue;
}
