package com.hccmac.communityforumbackend.module.user.service.impl;

import org.springframework.beans.BeanUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hccmac.communityforumbackend.common.ErrorCode;
import com.hccmac.communityforumbackend.constant.UserConstant;
import com.hccmac.communityforumbackend.exception.BusinessException;
import com.hccmac.communityforumbackend.exception.ThrowUtils;
import com.hccmac.communityforumbackend.module.user.entity.ForumUser;
import com.hccmac.communityforumbackend.mapper.ForumUserMapper;
import com.hccmac.communityforumbackend.module.user.model.dto.UserLoginReq;
import com.hccmac.communityforumbackend.module.user.model.dto.UserMuteUpdateReq;
import com.hccmac.communityforumbackend.module.user.model.dto.UserQueryReq;
import com.hccmac.communityforumbackend.module.user.model.dto.UserRegisterReq;
import com.hccmac.communityforumbackend.module.user.model.dto.UserStatusUpdateReq;
import com.hccmac.communityforumbackend.module.user.model.dto.UserUpdateProfileReq;
import com.hccmac.communityforumbackend.module.user.model.vo.LoginUserVO;
import com.hccmac.communityforumbackend.module.user.service.UserService;
import com.hccmac.communityforumbackend.utils.EncryptUtil;
import com.hccmac.communityforumbackend.utils.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<ForumUserMapper, ForumUser> implements UserService {

    private static final int ACCOUNT_MIN_LENGTH = 4;
    private static final int PASSWORD_MIN_LENGTH = 8;

    @Override
    public Long userRegister(UserRegisterReq userRegisterReq) {
        ThrowUtils.throwIf(userRegisterReq == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userRegisterReq.getUserAccount();
        String userPassword = userRegisterReq.getUserPassword();
        String checkPassword = userRegisterReq.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < ACCOUNT_MIN_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号过短");
        }
        if (userPassword.length() < PASSWORD_MIN_LENGTH || checkPassword.length() < PASSWORD_MIN_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码过短");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致");
        }
        QueryWrapper<ForumUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.count(queryWrapper);
        ThrowUtils.throwIf(count > 0, ErrorCode.PARAMS_ERROR, "账号已存在");

        ForumUser forumUser = new ForumUser();
        forumUser.setUserAccount(userAccount);
        forumUser.setUserPassword(EncryptUtil.md5(userPassword));
        forumUser.setUserName(userAccount);
        forumUser.setUserRole(UserConstant.USER_ROLE);
        forumUser.setUserStatus(UserConstant.USER_STATUS_NORMAL);
        forumUser.setPostCount(0L);
        forumUser.setCommentCount(0L);
        forumUser.setLikeReceivedCount(0L);
        boolean result = this.save(forumUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "注册失败");
        log.info("用户注册 uid:{} userAccount:{}", forumUser.getId(), userAccount);
        return forumUser.getId();
    }

    @Override
    public LoginUserVO userLogin(UserLoginReq userLoginReq, HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginReq == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userLoginReq.getUserAccount();
        String userPassword = userLoginReq.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        QueryWrapper<ForumUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", EncryptUtil.md5(userPassword));
        ForumUser forumUser = this.getOne(queryWrapper);
        ThrowUtils.throwIf(forumUser == null, ErrorCode.PARAMS_ERROR, "账号或密码错误");
        ThrowUtils.throwIf(UserConstant.USER_STATUS_DISABLED.equals(forumUser.getUserStatus()), ErrorCode.FORBIDDEN_ERROR, "账号已禁用");
        forumUser.setLastLoginTime(new Date());
        forumUser.setLastLoginIp(request.getRemoteAddr());
        this.updateById(forumUser);
        SessionUtil.setLoginUser(request, forumUser);
        log.info("用户登录 uid:{} userAccount:{}", forumUser.getId(), userAccount);
        return toLoginUserVO(forumUser);
    }

    @Override
    public LoginUserVO getLoginUser(HttpServletRequest request) {
        ForumUser sessionUser = SessionUtil.getLoginUser(request);
        ThrowUtils.throwIf(sessionUser == null || sessionUser.getId() == null, ErrorCode.NOT_LOGIN_ERROR);
        ForumUser forumUser = this.getById(sessionUser.getId());
        ThrowUtils.throwIf(forumUser == null, ErrorCode.NOT_LOGIN_ERROR);
        return toLoginUserVO(forumUser);
    }

    @Override
    public ForumUser getLoginUserEntity(HttpServletRequest request) {
        ForumUser sessionUser = SessionUtil.getLoginUser(request);
        ThrowUtils.throwIf(sessionUser == null || sessionUser.getId() == null, ErrorCode.NOT_LOGIN_ERROR);
        ForumUser forumUser = this.getById(sessionUser.getId());
        ThrowUtils.throwIf(forumUser == null, ErrorCode.NOT_LOGIN_ERROR);
        return forumUser;
    }

    @Override
    public Boolean userLogout(HttpServletRequest request) {
        SessionUtil.removeLoginUser(request);
        return true;
    }

    @Override
    public Boolean updateProfile(UserUpdateProfileReq userUpdateProfileReq, ForumUser loginUser) {
        ThrowUtils.throwIf(userUpdateProfileReq == null || loginUser == null, ErrorCode.PARAMS_ERROR);
        ForumUser updateUser = new ForumUser();
        updateUser.setId(loginUser.getId());
        updateUser.setUserName(userUpdateProfileReq.getUserName());
        updateUser.setUserAvatar(userUpdateProfileReq.getUserAvatar());
        updateUser.setUserProfile(userUpdateProfileReq.getUserProfile());
        updateUser.setPhone(userUpdateProfileReq.getPhone());
        updateUser.setEmail(userUpdateProfileReq.getEmail());
        boolean result = this.updateById(updateUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新资料失败");
        log.info("更新资料 uid:{}", loginUser.getId());
        return true;
    }

    @Override
    public Page<LoginUserVO> listUserByPage(UserQueryReq userQueryReq) {
        ThrowUtils.throwIf(userQueryReq == null, ErrorCode.PARAMS_ERROR);
        QueryWrapper<ForumUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(userQueryReq.getUserAccount()), "userAccount", userQueryReq.getUserAccount());
        queryWrapper.like(StringUtils.isNotBlank(userQueryReq.getUserName()), "userName", userQueryReq.getUserName());
        queryWrapper.eq(StringUtils.isNotBlank(userQueryReq.getUserRole()), "userRole", userQueryReq.getUserRole());
        queryWrapper.eq(userQueryReq.getUserStatus() != null, "userStatus", userQueryReq.getUserStatus());
        queryWrapper.orderByDesc("createTime");
        Page<ForumUser> forumUserPage = this.page(new Page<>(userQueryReq.getCurrent(), userQueryReq.getPageSize()), queryWrapper);
        Page<LoginUserVO> loginUserVOPage = new Page<>(userQueryReq.getCurrent(), userQueryReq.getPageSize(), forumUserPage.getTotal());
        List<LoginUserVO> loginUserVOList = forumUserPage.getRecords().stream().map(this::toLoginUserVO).collect(Collectors.toList());
        loginUserVOPage.setRecords(loginUserVOList);
        return loginUserVOPage;
    }

    @Override
    public Boolean updateUserStatus(UserStatusUpdateReq userStatusUpdateReq) {
        ThrowUtils.throwIf(userStatusUpdateReq == null || userStatusUpdateReq.getId() == null || userStatusUpdateReq.getUserStatus() == null, ErrorCode.PARAMS_ERROR);
        ForumUser forumUser = new ForumUser();
        forumUser.setId(userStatusUpdateReq.getId());
        forumUser.setUserStatus(userStatusUpdateReq.getUserStatus());
        boolean result = this.updateById(forumUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新用户状态失败");
        log.info("更新用户状态 uid:{} userStatus:{}", userStatusUpdateReq.getId(), userStatusUpdateReq.getUserStatus());
        return true;
    }

    @Override
    public Boolean updateUserMute(UserMuteUpdateReq userMuteUpdateReq) {
        ThrowUtils.throwIf(userMuteUpdateReq == null || userMuteUpdateReq.getId() == null, ErrorCode.PARAMS_ERROR);
        ForumUser forumUser = new ForumUser();
        forumUser.setId(userMuteUpdateReq.getId());
        forumUser.setMuteEndTime(userMuteUpdateReq.getMuteEndTime());
        boolean result = this.updateById(forumUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新禁言失败");
        log.info("更新禁言 uid:{} muteEndTime:{}", userMuteUpdateReq.getId(), userMuteUpdateReq.getMuteEndTime());
        return true;
    }

    @Override
    public boolean isAdmin(ForumUser forumUser) {
        return forumUser != null && UserConstant.ADMIN_ROLE.equals(forumUser.getUserRole());
    }

    @Override
    public boolean hasRole(ForumUser forumUser, String mustRole) {
        if (forumUser == null || StringUtils.isBlank(mustRole)) {
            return false;
        }
        return UserConstant.ADMIN_ROLE.equals(forumUser.getUserRole()) || mustRole.equals(forumUser.getUserRole());
    }

    private LoginUserVO toLoginUserVO(ForumUser forumUser) {
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(forumUser, loginUserVO);
        return loginUserVO;
    }
}
