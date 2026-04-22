package com.hccmac.communityforumbackend.module.interaction.controller;

import com.hccmac.communityforumbackend.common.BaseResponse;
import com.hccmac.communityforumbackend.common.ResultUtils;
import com.hccmac.communityforumbackend.module.interaction.model.dto.ToggleActionReq;
import com.hccmac.communityforumbackend.module.interaction.service.InteractionService;
import com.hccmac.communityforumbackend.module.user.entity.ForumUser;
import com.hccmac.communityforumbackend.module.user.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 互动接口
 */
@RestController
@RequestMapping("/")
public class InteractionController {

    @Resource
    private InteractionService interactionService;

    @Resource
    private UserService userService;

    @PostMapping("/post/like")
    public BaseResponse<Boolean> togglePostLike(@RequestBody ToggleActionReq toggleActionReq, HttpServletRequest request) {
        ForumUser loginUser = userService.getLoginUserEntity(request);
        return ResultUtils.success(interactionService.togglePostLike(toggleActionReq.getBizId(), loginUser));
    }

    @PostMapping("/post/collect")
    public BaseResponse<Boolean> togglePostCollect(@RequestBody ToggleActionReq toggleActionReq, HttpServletRequest request) {
        ForumUser loginUser = userService.getLoginUserEntity(request);
        return ResultUtils.success(interactionService.togglePostCollect(toggleActionReq.getBizId(), loginUser));
    }

    @PostMapping("/comment/like")
    public BaseResponse<Boolean> toggleCommentLike(@RequestBody ToggleActionReq toggleActionReq, HttpServletRequest request) {
        ForumUser loginUser = userService.getLoginUserEntity(request);
        return ResultUtils.success(interactionService.toggleCommentLike(toggleActionReq.getBizId(), loginUser));
    }
}
