package com.hccmac.communityforumbackend.module.user.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户状态更新请求
 */
@Data
public class UserStatusUpdateReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Integer userStatus;
}
