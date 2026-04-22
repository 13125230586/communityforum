package com.hccmac.communityforumbackend.module.interaction.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 互动切换请求
 */
@Data
public class ToggleActionReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long bizId;
}
