package com.hccmac.communityforumbackend.module.board.model.dto;

import com.hccmac.communityforumbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 板块查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BoardQueryReq extends PageRequest {

    private static final long serialVersionUID = 1L;

    private Long categoryId;

    private String boardName;
}
