package com.hccmac.communityforumbackend.module.post.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hccmac.communityforumbackend.annotation.AuthCheck;
import com.hccmac.communityforumbackend.annotation.OperationLog;
import com.hccmac.communityforumbackend.common.BaseResponse;
import com.hccmac.communityforumbackend.common.ResultUtils;
import com.hccmac.communityforumbackend.constant.OperationLogConstant;
import com.hccmac.communityforumbackend.constant.UserConstant;
import com.hccmac.communityforumbackend.module.post.model.dto.PostAuditReq;
import com.hccmac.communityforumbackend.module.post.model.dto.PostFlagUpdateReq;
import com.hccmac.communityforumbackend.module.post.model.dto.PostQueryReq;
import com.hccmac.communityforumbackend.module.post.model.vo.PostVO;
import com.hccmac.communityforumbackend.module.post.service.PostService;
import com.hccmac.communityforumbackend.module.user.entity.ForumUser;
import com.hccmac.communityforumbackend.module.user.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 后台帖子接口
 */
@RestController
@RequestMapping("/admin/post")
public class AdminPostController {

    @Resource
    private PostService postService;

    @Resource
    private UserService userService;

    @GetMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<PostVO>> listPostByPage(PostQueryReq postQueryReq) {
        return ResultUtils.success(postService.listPostByPage(postQueryReq, true));
    }

    @PostMapping("/audit")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @OperationLog(bizType = OperationLogConstant.BIZ_TYPE_POST, actionType = "audit")
    public BaseResponse<Boolean> auditPost(@RequestBody PostAuditReq postAuditReq, HttpServletRequest request) {
        ForumUser loginUser = userService.getLoginUserEntity(request);
        return ResultUtils.success(postService.auditPost(postAuditReq, loginUser));
    }

    @PostMapping("/update/top")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @OperationLog(bizType = OperationLogConstant.BIZ_TYPE_POST, actionType = "updateTop")
    public BaseResponse<Boolean> updateTopFlag(@RequestBody PostFlagUpdateReq postFlagUpdateReq) {
        return ResultUtils.success(postService.updateTopFlag(postFlagUpdateReq));
    }

    @PostMapping("/update/essence")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @OperationLog(bizType = OperationLogConstant.BIZ_TYPE_POST, actionType = "updateEssence")
    public BaseResponse<Boolean> updateEssenceFlag(@RequestBody PostFlagUpdateReq postFlagUpdateReq) {
        return ResultUtils.success(postService.updateEssenceFlag(postFlagUpdateReq));
    }
}
