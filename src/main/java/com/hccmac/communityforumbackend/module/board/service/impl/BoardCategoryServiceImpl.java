package com.hccmac.communityforumbackend.module.board.service.impl;

import org.springframework.beans.BeanUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hccmac.communityforumbackend.common.ErrorCode;
import com.hccmac.communityforumbackend.exception.ThrowUtils;
import com.hccmac.communityforumbackend.module.board.entity.ForumBoardCategory;
import com.hccmac.communityforumbackend.mapper.ForumBoardCategoryMapper;
import com.hccmac.communityforumbackend.module.board.model.dto.BoardCategoryAddReq;
import com.hccmac.communityforumbackend.module.board.model.dto.BoardCategoryQueryReq;
import com.hccmac.communityforumbackend.module.board.model.dto.BoardCategoryUpdateReq;
import com.hccmac.communityforumbackend.module.board.model.vo.BoardCategoryVO;
import com.hccmac.communityforumbackend.module.board.service.BoardCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 板块分类服务实现
 */
@Service
@Slf4j
public class BoardCategoryServiceImpl extends ServiceImpl<ForumBoardCategoryMapper, ForumBoardCategory> implements BoardCategoryService {

    private static final int DEFAULT_SORT_ORDER = 0;
    private static final int DEFAULT_CATEGORY_STATUS = 0;

    @Override
    public Long addBoardCategory(BoardCategoryAddReq boardCategoryAddReq) {
        ThrowUtils.throwIf(boardCategoryAddReq == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StringUtils.isBlank(boardCategoryAddReq.getCategoryName()), ErrorCode.PARAMS_ERROR, "分类名称不能为空");
        ForumBoardCategory forumBoardCategory = new ForumBoardCategory();
        BeanUtils.copyProperties(boardCategoryAddReq, forumBoardCategory);
        if (forumBoardCategory.getSortOrder() == null) {
            forumBoardCategory.setSortOrder(DEFAULT_SORT_ORDER);
        }
        if (forumBoardCategory.getCategoryStatus() == null) {
            forumBoardCategory.setCategoryStatus(DEFAULT_CATEGORY_STATUS);
        }
        forumBoardCategory.setBoardCount(0L);
        boolean result = this.save(forumBoardCategory);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "新增分类失败");
        log.info("新增分类 categoryId:{}", forumBoardCategory.getId());
        return forumBoardCategory.getId();
    }

    @Override
    public Boolean updateBoardCategory(BoardCategoryUpdateReq boardCategoryUpdateReq) {
        ThrowUtils.throwIf(boardCategoryUpdateReq == null || boardCategoryUpdateReq.getId() == null, ErrorCode.PARAMS_ERROR);
        ForumBoardCategory oldCategory = this.getById(boardCategoryUpdateReq.getId());
        ThrowUtils.throwIf(oldCategory == null, ErrorCode.NOT_FOUND_ERROR, "分类不存在");
        ForumBoardCategory forumBoardCategory = new ForumBoardCategory();
        BeanUtils.copyProperties(boardCategoryUpdateReq, forumBoardCategory);
        boolean result = this.updateById(forumBoardCategory);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新分类失败");
        log.info("更新分类 categoryId:{}", boardCategoryUpdateReq.getId());
        return true;
    }

    @Override
    public Boolean deleteBoardCategory(Long id) {
        ThrowUtils.throwIf(id == null, ErrorCode.PARAMS_ERROR);
        ForumBoardCategory forumBoardCategory = this.getById(id);
        ThrowUtils.throwIf(forumBoardCategory == null, ErrorCode.NOT_FOUND_ERROR, "分类不存在");
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "删除分类失败");
        log.info("删除分类 categoryId:{}", id);
        return true;
    }

    @Override
    public Page<BoardCategoryVO> listBoardCategoryByPage(BoardCategoryQueryReq boardCategoryQueryReq) {
        ThrowUtils.throwIf(boardCategoryQueryReq == null, ErrorCode.PARAMS_ERROR);
        QueryWrapper<ForumBoardCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(boardCategoryQueryReq.getCategoryName()), "categoryName", boardCategoryQueryReq.getCategoryName());
        queryWrapper.orderByAsc("sortOrder");
        queryWrapper.orderByDesc("createTime");
        Page<ForumBoardCategory> categoryPage = this.page(new Page<>(boardCategoryQueryReq.getCurrent(), boardCategoryQueryReq.getPageSize()), queryWrapper);
        Page<BoardCategoryVO> boardCategoryVOPage = new Page<>(boardCategoryQueryReq.getCurrent(), boardCategoryQueryReq.getPageSize(), categoryPage.getTotal());
        boardCategoryVOPage.setRecords(categoryPage.getRecords().stream().map(this::toBoardCategoryVO).collect(Collectors.toList()));
        return boardCategoryVOPage;
    }

    @Override
    public List<BoardCategoryVO> listEnableBoardCategory() {
        QueryWrapper<ForumBoardCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("categoryStatus", DEFAULT_CATEGORY_STATUS);
        queryWrapper.orderByAsc("sortOrder");
        return this.list(queryWrapper).stream().map(this::toBoardCategoryVO).collect(Collectors.toList());
    }

    private BoardCategoryVO toBoardCategoryVO(ForumBoardCategory forumBoardCategory) {
        BoardCategoryVO boardCategoryVO = new BoardCategoryVO();
        BeanUtils.copyProperties(forumBoardCategory, boardCategoryVO);
        return boardCategoryVO;
    }
}
