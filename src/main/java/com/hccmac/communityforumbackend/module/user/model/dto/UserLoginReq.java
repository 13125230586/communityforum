package com.hccmac.communityforumbackend.module.user.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求
 */
@Data
public class UserLoginReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userAccount;

    private String userPassword;
}
