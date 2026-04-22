package com.hccmac.communityforumbackend.module.board.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 板块分类新增请求
 */
@Data
public class BoardCategoryAddReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private String categoryName;

    private String categoryIcon;

    private Integer sortOrder;

    private Integer categoryStatus;
}
