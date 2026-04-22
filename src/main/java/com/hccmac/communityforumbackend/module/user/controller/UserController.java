package com.hccmac.communityforumbackend.module.user.controller;

import com.hccmac.communityforumbackend.common.BaseResponse;
import com.hccmac.communityforumbackend.common.ResultUtils;
import com.hccmac.communityforumbackend.module.user.entity.ForumUser;
import com.hccmac.communityforumbackend.module.user.model.dto.UserLoginReq;
import com.hccmac.communityforumbackend.module.user.model.dto.UserRegisterReq;
import com.hccmac.communityforumbackend.module.user.model.dto.UserUpdateProfileReq;
import com.hccmac.communityforumbackend.module.user.model.vo.LoginUserVO;
import com.hccmac.communityforumbackend.module.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterReq userRegisterReq) {
        return ResultUtils.success(userService.userRegister(userRegisterReq));
    }

    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginReq userLoginReq, HttpServletRequest request) {
        return ResultUtils.success(userService.userLogin(userLoginReq, request));
    }

    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        return ResultUtils.success(userService.getLoginUser(request));
    }

    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        return ResultUtils.success(userService.userLogout(request));
    }

    @PostMapping("/update/profile")
    public BaseResponse<Boolean> updateProfile(@RequestBody UserUpdateProfileReq userUpdateProfileReq, HttpServletRequest request) {
        ForumUser loginUser = userService.getLoginUserEntity(request);
        return ResultUtils.success(userService.updateProfile(userUpdateProfileReq, loginUser));
    }
}
