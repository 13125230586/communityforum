package com.hccmac.communityforumbackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用分页请求
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页
     */
    private long current = 1;

    /**
     * 分页大小
     */
    private long pageSize = 10;
}
