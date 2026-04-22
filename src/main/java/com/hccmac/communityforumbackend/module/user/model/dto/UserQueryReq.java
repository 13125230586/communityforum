package com.hccmac.communityforumbackend.module.user.model.dto;

import com.hccmac.communityforumbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户分页查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryReq extends PageRequest {

    private static final long serialVersionUID = 1L;

    private String userAccount;

    private String userName;

    private String userRole;

    private Integer userStatus;
}
