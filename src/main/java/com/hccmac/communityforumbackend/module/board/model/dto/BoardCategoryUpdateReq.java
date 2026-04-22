package com.hccmac.communityforumbackend.module.board.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 板块分类更新请求
 */
@Data
public class BoardCategoryUpdateReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String categoryName;

    private String categoryIcon;

    private Integer sortOrder;

    private Integer categoryStatus;
}
