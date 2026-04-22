package com.hccmac.communityforumbackend.utils;

import com.hccmac.communityforumbackend.constant.UserConstant;
import com.hccmac.communityforumbackend.module.user.entity.ForumUser;

import javax.servlet.http.HttpServletRequest;

/**
 * Session 工具类
 */
public class SessionUtil {

    public static void setLoginUser(HttpServletRequest request, ForumUser forumUser) {
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, forumUser);
    }

    public static ForumUser getLoginUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (userObj instanceof ForumUser) {
            return (ForumUser) userObj;
        }
        return null;
    }

    public static void removeLoginUser(HttpServletRequest request) {
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
    }
}
