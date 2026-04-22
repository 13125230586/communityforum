package com.hccmac.communityforumbackend.module.user.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户资料更新请求
 */
@Data
public class UserUpdateProfileReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userName;

    private String userAvatar;

    private String userProfile;

    private String phone;

    private String email;
}
