package com.hccmac.communityforumbackend.module.board.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hccmac.communityforumbackend.common.BaseResponse;
import com.hccmac.communityforumbackend.common.ResultUtils;
import com.hccmac.communityforumbackend.module.board.model.dto.BoardCategoryQueryReq;
import com.hccmac.communityforumbackend.module.board.model.vo.BoardCategoryVO;
import com.hccmac.communityforumbackend.module.board.service.BoardCategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 板块分类接口
 */
@RestController
@RequestMapping("/boardCategory")
public class BoardCategoryController {

    @Resource
    private BoardCategoryService boardCategoryService;

    @GetMapping("/list/page")
    public BaseResponse<Page<BoardCategoryVO>> listBoardCategoryByPage(BoardCategoryQueryReq boardCategoryQueryReq) {
        return ResultUtils.success(boardCategoryService.listBoardCategoryByPage(boardCategoryQueryReq));
    }

    @GetMapping("/list")
    public BaseResponse<List<BoardCategoryVO>> listEnableBoardCategory() {
        return ResultUtils.success(boardCategoryService.listEnableBoardCategory());
    }
}
