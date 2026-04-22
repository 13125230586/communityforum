package com.hccmac.communityforumbackend.module.comment.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hccmac.communityforumbackend.module.comment.entity.ForumComment;
import com.hccmac.communityforumbackend.module.comment.model.dto.CommentAddReq;
import com.hccmac.communityforumbackend.module.comment.model.dto.CommentAuditReq;
import com.hccmac.communityforumbackend.module.comment.model.dto.CommentQueryReq;
import com.hccmac.communityforumbackend.module.comment.model.vo.CommentVO;
import com.hccmac.communityforumbackend.module.user.entity.ForumUser;

/**
 * 评论服务
 */
public interface CommentService extends IService<ForumComment> {

    Long addComment(CommentAddReq commentAddReq, ForumUser loginUser);

    Boolean deleteComment(Long id, ForumUser loginUser);

    Page<CommentVO> listCommentByPage(CommentQueryReq commentQueryReq, boolean adminQuery);

    Boolean auditComment(CommentAuditReq commentAuditReq, ForumUser loginUser);
}
