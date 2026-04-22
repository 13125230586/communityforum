package com.hccmac.communityforumbackend.module.comment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hccmac.communityforumbackend.annotation.AuthCheck;
import com.hccmac.communityforumbackend.annotation.OperationLog;
import com.hccmac.communityforumbackend.common.BaseResponse;
import com.hccmac.communityforumbackend.common.ResultUtils;
import com.hccmac.communityforumbackend.constant.OperationLogConstant;
import com.hccmac.communityforumbackend.constant.UserConstant;
import com.hccmac.communityforumbackend.module.comment.model.dto.CommentAuditReq;
import com.hccmac.communityforumbackend.module.comment.model.dto.CommentQueryReq;
import com.hccmac.communityforumbackend.module.comment.model.vo.CommentVO;
import com.hccmac.communityforumbackend.module.comment.service.CommentService;
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
 * 后台评论接口
 */
@RestController
@RequestMapping("/admin/comment")
public class AdminCommentController {

    @Resource
    private CommentService commentService;

    @Resource
    private UserService userService;

    @GetMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<CommentVO>> listCommentByPage(CommentQueryReq commentQueryReq) {
        return ResultUtils.success(commentService.listCommentByPage(commentQueryReq, true));
    }

    @PostMapping("/audit")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @OperationLog(bizType = OperationLogConstant.BIZ_TYPE_COMMENT, actionType = "audit")
    public BaseResponse<Boolean> auditComment(@RequestBody CommentAuditReq commentAuditReq, HttpServletRequest request) {
        ForumUser loginUser = userService.getLoginUserEntity(request);
        return ResultUtils.success(commentService.auditComment(commentAuditReq, loginUser));
    }
}
