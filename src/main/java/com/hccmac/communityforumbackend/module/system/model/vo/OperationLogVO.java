package com.hccmac.communityforumbackend.module.system.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 操作日志视图对象
 */
@Data
public class OperationLogVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long operatorUserId;
    private String operatorUserName;
    private String operatorRole;
    private String bizType;
    private Long bizId;
    private String actionType;
    private String requestPath;
    private String requestParam;
    private Integer resultCode;
    private String resultMessage;
    private String operateIp;
    private Date createTime;
}
