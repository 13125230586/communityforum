package com.hccmac.communityforumbackend.module.board.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hccmac.communityforumbackend.module.board.entity.ForumBoardCategory;
import com.hccmac.communityforumbackend.module.board.model.dto.BoardCategoryAddReq;
import com.hccmac.communityforumbackend.module.board.model.dto.BoardCategoryQueryReq;
import com.hccmac.communityforumbackend.module.board.model.dto.BoardCategoryUpdateReq;
import com.hccmac.communityforumbackend.module.board.model.vo.BoardCategoryVO;

import java.util.List;

/**
 * 板块分类服务
 */
public interface BoardCategoryService extends IService<ForumBoardCategory> {

    Long addBoardCategory(BoardCategoryAddReq boardCategoryAddReq);

    Boolean updateBoardCategory(BoardCategoryUpdateReq boardCategoryUpdateReq);

    Boolean deleteBoardCategory(Long id);

    Page<BoardCategoryVO> listBoardCategoryByPage(BoardCategoryQueryReq boardCategoryQueryReq);

    List<BoardCategoryVO> listEnableBoardCategory();
}
