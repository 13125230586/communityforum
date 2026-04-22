package com.hccmac.communityforumbackend.module.board.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hccmac.communityforumbackend.module.board.entity.ForumBoard;
import com.hccmac.communityforumbackend.module.board.model.dto.BoardAddReq;
import com.hccmac.communityforumbackend.module.board.model.dto.BoardQueryReq;
import com.hccmac.communityforumbackend.module.board.model.dto.BoardUpdateReq;
import com.hccmac.communityforumbackend.module.board.model.vo.BoardVO;
import com.hccmac.communityforumbackend.module.user.entity.ForumUser;

import java.util.List;

/**
 * 板块服务
 */
public interface BoardService extends IService<ForumBoard> {

    Long addBoard(BoardAddReq boardAddReq, ForumUser loginUser);

    Boolean updateBoard(BoardUpdateReq boardUpdateReq);

    Boolean deleteBoard(Long id);

    Page<BoardVO> listBoardByPage(BoardQueryReq boardQueryReq);

    List<BoardVO> listEnableBoard(Long categoryId);

    BoardVO getBoardVO(Long id);
}
