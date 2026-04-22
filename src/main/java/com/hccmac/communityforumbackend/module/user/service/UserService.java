package com.hccmac.communityforumbackend.module.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hccmac.communityforumbackend.module.user.entity.ForumUser;
import com.hccmac.communityforumbackend.module.user.model.dto.UserLoginReq;
import com.hccmac.communityforumbackend.module.user.model.dto.UserMuteUpdateReq;
import com.hccmac.communityforumbackend.module.user.model.dto.UserQueryReq;
import com.hccmac.communityforumbackend.module.user.model.dto.UserRegisterReq;
import com.hccmac.communityforumbackend.module.user.model.dto.UserStatusUpdateReq;
import com.hccmac.communityforumbackend.module.user.model.dto.UserUpdateProfileReq;
import com.hccmac.communityforumbackend.module.user.model.vo.LoginUserVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务
 */
public interface UserService extends IService<ForumUser> {

    Long userRegister(UserRegisterReq userRegisterReq);

    LoginUserVO userLogin(UserLoginReq userLoginReq, HttpServletRequest request);

    LoginUserVO getLoginUser(HttpServletRequest request);

    ForumUser getLoginUserEntity(HttpServletRequest request);

    Boolean userLogout(HttpServletRequest request);

    Boolean updateProfile(UserUpdateProfileReq userUpdateProfileReq, ForumUser loginUser);

    Page<LoginUserVO> listUserByPage(UserQueryReq userQueryReq);

    Boolean updateUserStatus(UserStatusUpdateReq userStatusUpdateReq);

    Boolean updateUserMute(UserMuteUpdateReq userMuteUpdateReq);

    boolean isAdmin(ForumUser forumUser);

    boolean hasRole(ForumUser forumUser, String mustRole);
}
