package com.hccmac.communityforumbackend.module.board.service.impl;

import org.springframework.beans.BeanUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hccmac.communityforumbackend.common.ErrorCode;
import com.hccmac.communityforumbackend.exception.ThrowUtils;
import com.hccmac.communityforumbackend.module.board.entity.ForumBoard;
import com.hccmac.communityforumbackend.module.board.entity.ForumBoardCategory;
import com.hccmac.communityforumbackend.mapper.ForumBoardMapper;
import com.hccmac.communityforumbackend.module.board.model.dto.BoardAddReq;
import com.hccmac.communityforumbackend.module.board.model.dto.BoardQueryReq;
import com.hccmac.communityforumbackend.module.board.model.dto.BoardUpdateReq;
import com.hccmac.communityforumbackend.module.board.model.vo.BoardVO;
import com.hccmac.communityforumbackend.module.board.service.BoardCategoryService;
import com.hccmac.communityforumbackend.module.board.service.BoardService;
import com.hccmac.communityforumbackend.module.user.entity.ForumUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 板块服务实现
 */
@Service
@Slf4j
public class BoardServiceImpl extends ServiceImpl<ForumBoardMapper, ForumBoard> implements BoardService {

    private static final int DEFAULT_BOARD_STATUS = 0;
    private static final int DEFAULT_AUDIT_FLAG = 0;

    @Resource
    private BoardCategoryService boardCategoryService;

    @Override
    public Long addBoard(BoardAddReq boardAddReq, ForumUser loginUser) {
        ThrowUtils.throwIf(boardAddReq == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null || loginUser.getId() == null, ErrorCode.NOT_LOGIN_ERROR);
        ThrowUtils.throwIf(boardAddReq.getCategoryId() == null, ErrorCode.PARAMS_ERROR, "分类不能为空");
        ThrowUtils.throwIf(StringUtils.isAnyBlank(boardAddReq.getBoardName(), boardAddReq.getBoardCode()), ErrorCode.PARAMS_ERROR, "板块信息不能为空");
        ForumBoardCategory forumBoardCategory = boardCategoryService.getById(boardAddReq.getCategoryId());
        ThrowUtils.throwIf(forumBoardCategory == null, ErrorCode.NOT_FOUND_ERROR, "分类不存在");

        QueryWrapper<ForumBoard> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("boardCode", boardAddReq.getBoardCode());
        ThrowUtils.throwIf(this.count(queryWrapper) > 0, ErrorCode.PARAMS_ERROR, "板块编码已存在");

        ForumBoard forumBoard = new ForumBoard();
        BeanUtils.copyProperties(boardAddReq, forumBoard);
        if (forumBoard.getBoardStatus() == null) {
            forumBoard.setBoardStatus(DEFAULT_BOARD_STATUS);
        }
        if (forumBoard.getPostAuditFlag() == null) {
            forumBoard.setPostAuditFlag(DEFAULT_AUDIT_FLAG);
        }
        if (forumBoard.getCommentAuditFlag() == null) {
            forumBoard.setCommentAuditFlag(DEFAULT_AUDIT_FLAG);
        }
        forumBoard.setPostCount(0L);
        forumBoard.setCommentCount(0L);
        forumBoard.setFollowCount(0L);
        forumBoard.setCreateUserId(loginUser.getId());
        boolean result = this.save(forumBoard);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "新增板块失败");
        log.info("新增板块 boardId:{} categoryId:{}", forumBoard.getId(), forumBoard.getCategoryId());
        return forumBoard.getId();
    }

    @Override
    public Boolean updateBoard(BoardUpdateReq boardUpdateReq) {
        ThrowUtils.throwIf(boardUpdateReq == null || boardUpdateReq.getId() == null, ErrorCode.PARAMS_ERROR);
        ForumBoard oldBoard = this.getById(boardUpdateReq.getId());
        ThrowUtils.throwIf(oldBoard == null, ErrorCode.NOT_FOUND_ERROR, "板块不存在");
        if (StringUtils.isNotBlank(boardUpdateReq.getBoardCode())) {
            QueryWrapper<ForumBoard> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("boardCode", boardUpdateReq.getBoardCode());
            queryWrapper.ne("id", boardUpdateReq.getId());
            ThrowUtils.throwIf(this.count(queryWrapper) > 0, ErrorCode.PARAMS_ERROR, "板块编码已存在");
        }
        ForumBoard forumBoard = new ForumBoard();
        BeanUtils.copyProperties(boardUpdateReq, forumBoard);
        boolean result = this.updateById(forumBoard);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新板块失败");
        log.info("更新板块 boardId:{}", boardUpdateReq.getId());
        return true;
    }

    @Override
    public Boolean deleteBoard(Long id) {
        ThrowUtils.throwIf(id == null, ErrorCode.PARAMS_ERROR);
        ForumBoard forumBoard = this.getById(id);
        ThrowUtils.throwIf(forumBoard == null, ErrorCode.NOT_FOUND_ERROR, "板块不存在");
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "删除板块失败");
        log.info("删除板块 boardId:{}", id);
        return true;
    }

    @Override
    public Page<BoardVO> listBoardByPage(BoardQueryReq boardQueryReq) {
        ThrowUtils.throwIf(boardQueryReq == null, ErrorCode.PARAMS_ERROR);
        QueryWrapper<ForumBoard> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(boardQueryReq.getCategoryId() != null, "categoryId", boardQueryReq.getCategoryId());
        queryWrapper.like(StringUtils.isNotBlank(boardQueryReq.getBoardName()), "boardName", boardQueryReq.getBoardName());
        queryWrapper.orderByAsc("sortOrder");
        queryWrapper.orderByDesc("createTime");
        Page<ForumBoard> boardPage = this.page(new Page<>(boardQueryReq.getCurrent(), boardQueryReq.getPageSize()), queryWrapper);
        Page<BoardVO> boardVOPage = new Page<>(boardQueryReq.getCurrent(), boardQueryReq.getPageSize(), boardPage.getTotal());
        boardVOPage.setRecords(boardPage.getRecords().stream().map(this::toBoardVO).collect(Collectors.toList()));
        return boardVOPage;
    }

    @Override
    public List<BoardVO> listEnableBoard(Long categoryId) {
        QueryWrapper<ForumBoard> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("boardStatus", DEFAULT_BOARD_STATUS);
        queryWrapper.eq(categoryId != null, "categoryId", categoryId);
        queryWrapper.orderByAsc("sortOrder");
        return this.list(queryWrapper).stream().map(this::toBoardVO).collect(Collectors.toList());
    }

    @Override
    public BoardVO getBoardVO(Long id) {
        ThrowUtils.throwIf(id == null, ErrorCode.PARAMS_ERROR);
        ForumBoard forumBoard = this.getById(id);
        ThrowUtils.throwIf(forumBoard == null, ErrorCode.NOT_FOUND_ERROR, "板块不存在");
        return toBoardVO(forumBoard);
    }

    private BoardVO toBoardVO(ForumBoard forumBoard) {
        BoardVO boardVO = new BoardVO();
        BeanUtils.copyProperties(forumBoard, boardVO);
        ForumBoardCategory forumBoardCategory = boardCategoryService.getById(forumBoard.getCategoryId());
        if (forumBoardCategory != null) {
            boardVO.setCategoryName(forumBoardCategory.getCategoryName());
        }
        return boardVO;
    }
}
