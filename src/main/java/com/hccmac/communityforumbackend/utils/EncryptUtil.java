package com.hccmac.communityforumbackend.utils;

import cn.hutool.crypto.digest.DigestUtil;

/**
 * 加密工具类
 */
public class EncryptUtil {

    private static final String SALT = "communityforum";

    public static String md5(String content) {
        return DigestUtil.md5Hex(SALT + content);
    }
}
