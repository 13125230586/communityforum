package com.hccmac.communityforumbackend.module.board.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 板块更新请求
 */
@Data
public class BoardUpdateReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long categoryId;

    private String boardName;

    private String boardCode;

    private String boardIcon;

    private String boardDescription;

    private Integer sortOrder;

    private Integer boardStatus;

    private Integer postAuditFlag;

    private Integer commentAuditFlag;
}
