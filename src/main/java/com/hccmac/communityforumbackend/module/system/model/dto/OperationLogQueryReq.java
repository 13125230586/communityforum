package com.hccmac.communityforumbackend.module.system.model.dto;

import com.hccmac.communityforumbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 操作日志分页请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OperationLogQueryReq extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long operatorUserId;
    private String bizType;
    private String actionType;
    private Integer resultCode;
}
