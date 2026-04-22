package com.hccmac.communityforumbackend.module.board.controller;

import com.hccmac.communityforumbackend.annotation.AuthCheck;
import com.hccmac.communityforumbackend.annotation.OperationLog;
import com.hccmac.communityforumbackend.common.BaseResponse;
import com.hccmac.communityforumbackend.common.DeleteRequest;
import com.hccmac.communityforumbackend.common.ResultUtils;
import com.hccmac.communityforumbackend.constant.OperationLogConstant;
import com.hccmac.communityforumbackend.constant.UserConstant;
import com.hccmac.communityforumbackend.module.board.model.dto.BoardCategoryAddReq;
import com.hccmac.communityforumbackend.module.board.model.dto.BoardCategoryUpdateReq;
import com.hccmac.communityforumbackend.module.board.service.BoardCategoryService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 后台板块分类接口
 */
@RestController
@RequestMapping("/admin/boardCategory")
public class AdminBoardCategoryController {

    @Resource
    private BoardCategoryService boardCategoryService;

    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @OperationLog(bizType = OperationLogConstant.BIZ_TYPE_BOARD_CATEGORY, actionType = "add")
    public BaseResponse<Long> addBoardCategory(@RequestBody BoardCategoryAddReq boardCategoryAddReq) {
        return ResultUtils.success(boardCategoryService.addBoardCategory(boardCategoryAddReq));
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @OperationLog(bizType = OperationLogConstant.BIZ_TYPE_BOARD_CATEGORY, actionType = "update")
    public BaseResponse<Boolean> updateBoardCategory(@RequestBody BoardCategoryUpdateReq boardCategoryUpdateReq) {
        return ResultUtils.success(boardCategoryService.updateBoardCategory(boardCategoryUpdateReq));
    }

    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @OperationLog(bizType = OperationLogConstant.BIZ_TYPE_BOARD_CATEGORY, actionType = "delete")
    public BaseResponse<Boolean> deleteBoardCategory(@RequestBody DeleteRequest deleteRequest) {
        return ResultUtils.success(boardCategoryService.deleteBoardCategory(deleteRequest.getId()));
    }
}
