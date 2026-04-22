package com.hccmac.communityforumbackend.constant;

/**
 * 操作日志常量
 */
public interface OperationLogConstant {

    String BIZ_TYPE_USER = "user";
    String BIZ_TYPE_BOARD = "board";
    String BIZ_TYPE_BOARD_CATEGORY = "boardCategory";
    String BIZ_TYPE_POST = "post";
    String BIZ_TYPE_POST_TAG = "postTag";
    String BIZ_TYPE_COMMENT = "comment";
    String BIZ_TYPE_REPORT = "report";
    String BIZ_TYPE_SYSTEM_CONFIG = "systemConfig";
    String BIZ_TYPE_FILE = "file";

    int SUCCESS_CODE = 0;
    int PARAM_MAX_LENGTH = 4000;
    int MESSAGE_MAX_LENGTH = 500;
}
