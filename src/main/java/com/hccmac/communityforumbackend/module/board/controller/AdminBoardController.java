package com.hccmac.communityforumbackend.module.board.controller;

import com.hccmac.communityforumbackend.annotation.AuthCheck;
import com.hccmac.communityforumbackend.annotation.OperationLog;
import com.hccmac.communityforumbackend.common.BaseResponse;
import com.hccmac.communityforumbackend.common.DeleteRequest;
import com.hccmac.communityforumbackend.common.ResultUtils;
import com.hccmac.communityforumbackend.constant.OperationLogConstant;
import com.hccmac.communityforumbackend.constant.UserConstant;
import com.hccmac.communityforumbackend.module.board.model.dto.BoardAddReq;
import com.hccmac.communityforumbackend.module.board.model.dto.BoardUpdateReq;
import com.hccmac.communityforumbackend.module.board.service.BoardService;
import com.hccmac.communityforumbackend.module.user.entity.ForumUser;
import com.hccmac.communityforumbackend.module.user.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 后台板块接口
 */
@RestController
@RequestMapping("/admin/board")
public class AdminBoardController {

    @Resource
    private BoardService boardService;

    @Resource
    private UserService userService;

    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @OperationLog(bizType = OperationLogConstant.BIZ_TYPE_BOARD, actionType = "add")
    public BaseResponse<Long> addBoard(@RequestBody BoardAddReq boardAddReq, HttpServletRequest request) {
        ForumUser loginUser = userService.getLoginUserEntity(request);
        return ResultUtils.success(boardService.addBoard(boardAddReq, loginUser));
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @OperationLog(bizType = OperationLogConstant.BIZ_TYPE_BOARD, actionType = "update")
    public BaseResponse<Boolean> updateBoard(@RequestBody BoardUpdateReq boardUpdateReq) {
        return ResultUtils.success(boardService.updateBoard(boardUpdateReq));
    }

    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @OperationLog(bizType = OperationLogConstant.BIZ_TYPE_BOARD, actionType = "delete")
    public BaseResponse<Boolean> deleteBoard(@RequestBody DeleteRequest deleteRequest) {
        return ResultUtils.success(boardService.deleteBoard(deleteRequest.getId()));
    }
}
