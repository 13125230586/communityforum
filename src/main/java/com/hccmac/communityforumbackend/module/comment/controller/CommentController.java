package com.hccmac.communityforumbackend.module.comment.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hccmac.communityforumbackend.common.BaseResponse;
import com.hccmac.communityforumbackend.common.ResultUtils;
import com.hccmac.communityforumbackend.module.comment.model.dto.CommentAddReq;
import com.hccmac.communityforumbackend.module.comment.model.dto.CommentDeleteReq;
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
 * 评论接口
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private CommentService commentService;

    @Resource
    private UserService userService;

    @PostMapping("/add")
    public BaseResponse<Long> addComment(@RequestBody CommentAddReq commentAddReq, HttpServletRequest request) {
        ForumUser loginUser = userService.getLoginUserEntity(request);
        return ResultUtils.success(commentService.addComment(commentAddReq, loginUser));
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteComment(@RequestBody CommentDeleteReq commentDeleteReq, HttpServletRequest request) {
        ForumUser loginUser = userService.getLoginUserEntity(request);
        return ResultUtils.success(commentService.deleteComment(commentDeleteReq.getId(), loginUser));
    }

    @GetMapping("/list/page")
    public BaseResponse<Page<CommentVO>> listCommentByPage(CommentQueryReq commentQueryReq) {
        return ResultUtils.success(commentService.listCommentByPage(commentQueryReq, false));
    }
}
