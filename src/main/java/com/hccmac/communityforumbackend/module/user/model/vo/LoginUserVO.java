package com.hccmac.communityforumbackend.module.user.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录用户视图对象
 */
@Data
public class LoginUserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String userAccount;

    private String userName;

    private String userAvatar;

    private String userProfile;

    private String userRole;

    private Integer userStatus;
}
