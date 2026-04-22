package com.hccmac.communityforumbackend.module.board.model.dto;

import com.hccmac.communityforumbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 板块分类查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BoardCategoryQueryReq extends PageRequest {

    private static final long serialVersionUID = 1L;

    private String categoryName;
}
