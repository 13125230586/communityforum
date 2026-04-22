package com.hccmac.communityforumbackend.module.board.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hccmac.communityforumbackend.common.BaseResponse;
import com.hccmac.communityforumbackend.common.ResultUtils;
import com.hccmac.communityforumbackend.module.board.model.dto.BoardQueryReq;
import com.hccmac.communityforumbackend.module.board.model.vo.BoardVO;
import com.hccmac.communityforumbackend.module.board.service.BoardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 板块接口
 */
@RestController
@RequestMapping("/board")
public class BoardController {

    @Resource
    private BoardService boardService;

    @GetMapping("/list/page")
    public BaseResponse<Page<BoardVO>> listBoardByPage(BoardQueryReq boardQueryReq) {
        return ResultUtils.success(boardService.listBoardByPage(boardQueryReq));
    }

    @GetMapping("/list")
    public BaseResponse<List<BoardVO>> listEnableBoard(@RequestParam(required = false) Long categoryId) {
        return ResultUtils.success(boardService.listEnableBoard(categoryId));
    }

    @GetMapping("/get")
    public BaseResponse<BoardVO> getBoard(@RequestParam Long id) {
        return ResultUtils.success(boardService.getBoardVO(id));
    }
}
