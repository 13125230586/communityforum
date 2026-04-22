package com.hccmac.communityforumbackend.module.user.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户禁言更新请求
 */
@Data
public class UserMuteUpdateReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Date muteEndTime;
}
