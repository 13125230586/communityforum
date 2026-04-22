package com.hccmac.communityforumbackend.constant;

/**
 * 文件常量
 */
public interface FileConstant {

    String AVATAR_PATH = "avatar";
    String BOARD_PATH = "board";
    String POST_COVER_PATH = "post/cover";
    String POST_CONTENT_PATH = "post/content";
    String UPLOAD_TYPE_AVATAR = "avatar";
    String UPLOAD_TYPE_BOARD = "board";
    String UPLOAD_TYPE_POST_COVER = "postCover";
    String UPLOAD_TYPE_POST_CONTENT_IMAGE = "postContentImage";
    String STORAGE_TYPE_OSS = "oss";

    long MAX_FILE_SIZE = 10L * 1024 * 1024;
}
