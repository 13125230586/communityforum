package com.hccmac.communityforumbackend.module.board.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 板块分类视图对象
 */
@Data
public class BoardCategoryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String categoryName;

    private String categoryIcon;

    private Integer sortOrder;

    private Integer categoryStatus;

    private Long boardCount;
}
