package com.hccmac.communityforumbackend.module.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hccmac.communityforumbackend.annotation.AuthCheck;
import com.hccmac.communityforumbackend.annotation.OperationLog;
import com.hccmac.communityforumbackend.common.BaseResponse;
import com.hccmac.communityforumbackend.common.ResultUtils;
import com.hccmac.communityforumbackend.constant.OperationLogConstant;
import com.hccmac.communityforumbackend.constant.UserConstant;
import com.hccmac.communityforumbackend.module.user.model.dto.UserMuteUpdateReq;
import com.hccmac.communityforumbackend.module.user.model.dto.UserQueryReq;
import com.hccmac.communityforumbackend.module.user.model.dto.UserStatusUpdateReq;
import com.hccmac.communityforumbackend.module.user.model.vo.LoginUserVO;
import com.hccmac.communityforumbackend.module.user.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 后台用户管理接口
 */
@RestController
@RequestMapping("/admin/user")
public class AdminUserController {

    @Resource
    private UserService userService;

    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<LoginUserVO>> listUserByPage(@RequestBody UserQueryReq userQueryReq) {
        return ResultUtils.success(userService.listUserByPage(userQueryReq));
    }

    @PostMapping("/update/status")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @OperationLog(bizType = OperationLogConstant.BIZ_TYPE_USER, actionType = "updateStatus")
    public BaseResponse<Boolean> updateUserStatus(@RequestBody UserStatusUpdateReq userStatusUpdateReq) {
        return ResultUtils.success(userService.updateUserStatus(userStatusUpdateReq));
    }

    @PostMapping("/update/mute")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @OperationLog(bizType = OperationLogConstant.BIZ_TYPE_USER, actionType = "updateMute")
    public BaseResponse<Boolean> updateUserMute(@RequestBody UserMuteUpdateReq userMuteUpdateReq) {
        return ResultUtils.success(userService.updateUserMute(userMuteUpdateReq));
    }
}
