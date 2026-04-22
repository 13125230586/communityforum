package com.hccmac.communityforumbackend.constant;

/**
 * 帖子常量
 */
public interface PostConstant {

    Integer POST_STATUS_DRAFT = 0;
    Integer POST_STATUS_PUBLISHED = 1;
    Integer POST_STATUS_OFFLINE = 2;

    Integer AUDIT_STATUS_WAIT = 0;
    Integer AUDIT_STATUS_PASS = 1;
    Integer AUDIT_STATUS_REJECT = 2;
}
