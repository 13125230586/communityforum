package com.hccmac.communityforumbackend.module.board.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 板块视图对象
 */
@Data
public class BoardVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long categoryId;

    private String categoryName;

    private String boardName;

    private String boardCode;

    private String boardIcon;

    private String boardDescription;

    private Integer sortOrder;

    private Integer boardStatus;

    private Integer postAuditFlag;

    private Integer commentAuditFlag;

    private Long postCount;

    private Long commentCount;
}
