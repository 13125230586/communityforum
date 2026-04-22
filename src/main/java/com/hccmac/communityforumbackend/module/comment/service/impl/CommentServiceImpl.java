package com.hccmac.communityforumbackend.module.comment.service.impl;

import org.springframework.beans.BeanUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hccmac.communityforumbackend.common.ErrorCode;
import com.hccmac.communityforumbackend.constant.CommentConstant;
import com.hccmac.communityforumbackend.constant.PostConstant;
import com.hccmac.communityforumbackend.exception.ThrowUtils;
import com.hccmac.communityforumbackend.module.board.entity.ForumBoard;
import com.hccmac.communityforumbackend.module.board.service.BoardService;
import com.hccmac.communityforumbackend.module.comment.entity.ForumComment;
import com.hccmac.communityforumbackend.mapper.ForumCommentMapper;
import com.hccmac.communityforumbackend.module.comment.model.dto.CommentAddReq;
import com.hccmac.communityforumbackend.module.comment.model.dto.CommentAuditReq;
import com.hccmac.communityforumbackend.module.comment.model.dto.CommentQueryReq;
import com.hccmac.communityforumbackend.module.comment.model.vo.CommentVO;
import com.hccmac.communityforumbackend.module.comment.service.CommentService;
import com.hccmac.communityforumbackend.module.post.entity.ForumPost;
import com.hccmac.communityforumbackend.module.post.service.PostService;
import com.hccmac.communityforumbackend.module.user.entity.ForumUser;
import com.hccmac.communityforumbackend.module.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论服务实现
 */
@Service
@Slf4j
public class CommentServiceImpl extends ServiceImpl<ForumCommentMapper, ForumComment> implements CommentService {

    private static final int DEFAULT_FLAG = 0;

    @Resource
    private PostService postService;

    @Resource
    private UserService userService;

    @Resource
    private BoardService boardService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addComment(CommentAddReq commentAddReq, ForumUser loginUser) {
        ThrowUtils.throwIf(commentAddReq == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null || loginUser.getId() == null, ErrorCode.NOT_LOGIN_ERROR);
        ThrowUtils.throwIf(commentAddReq.getPostId() == null, ErrorCode.PARAMS_ERROR, "帖子不能为空");
        ThrowUtils.throwIf(StringUtils.isBlank(commentAddReq.getContent()), ErrorCode.PARAMS_ERROR, "评论内容不能为空");

        ForumPost forumPost = postService.getById(commentAddReq.getPostId());
        ThrowUtils.throwIf(forumPost == null, ErrorCode.NOT_FOUND_ERROR, "帖子不存在");
        ThrowUtils.throwIf(!PostConstant.POST_STATUS_PUBLISHED.equals(forumPost.getPostStatus()), ErrorCode.OPERATION_ERROR, "帖子不可评论");
        ThrowUtils.throwIf(Integer.valueOf(0).equals(forumPost.getCommentAllowedFlag()), ErrorCode.OPERATION_ERROR, "帖子已关闭评论");
        ForumBoard forumBoard = boardService.getById(forumPost.getBoardId());

        Long rootCommentId = commentAddReq.getRootCommentId() == null ? CommentConstant.ROOT_COMMENT_ID : commentAddReq.getRootCommentId();
        Long parentCommentId = commentAddReq.getParentCommentId() == null ? CommentConstant.ROOT_COMMENT_ID : commentAddReq.getParentCommentId();
        if (!CommentConstant.ROOT_COMMENT_ID.equals(parentCommentId)) {
            ForumComment parentComment = this.getById(parentCommentId);
            ThrowUtils.throwIf(parentComment == null, ErrorCode.NOT_FOUND_ERROR, "父评论不存在");
            if (CommentConstant.ROOT_COMMENT_ID.equals(rootCommentId)) {
                rootCommentId = parentComment.getRootCommentId() == null || CommentConstant.ROOT_COMMENT_ID.equals(parentComment.getRootCommentId()) ? parentComment.getId() : parentComment.getRootCommentId();
            }
        }

        ForumComment forumComment = new ForumComment();
        forumComment.setPostId(commentAddReq.getPostId());
        forumComment.setUserId(loginUser.getId());
        forumComment.setRootCommentId(rootCommentId);
        forumComment.setParentCommentId(parentCommentId);
        forumComment.setReplyUserId(commentAddReq.getReplyUserId());
        forumComment.setContentType(commentAddReq.getContentType());
        forumComment.setContent(commentAddReq.getContent().trim());
        forumComment.setCommentStatus(CommentConstant.COMMENT_STATUS_NORMAL);
        forumComment.setAuditStatus(forumBoard != null && Integer.valueOf(1).equals(forumBoard.getCommentAuditFlag()) ? PostConstant.AUDIT_STATUS_WAIT : PostConstant.AUDIT_STATUS_PASS);
        forumComment.setAnonymousFlag(commentAddReq.getAnonymousFlag() == null ? DEFAULT_FLAG : commentAddReq.getAnonymousFlag());
        forumComment.setLikeCount(0L);
        forumComment.setChildCount(0L);
        boolean result = this.save(forumComment);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "新增评论失败");

        forumPost.setCommentCount(safeLong(forumPost.getCommentCount()) + 1L);
        forumPost.setLastCommentTime(new Date());
        forumPost.setHotScore(forumPost.getHotScore() == null ? 0L : forumPost.getHotScore() + 5L);
        postService.updateById(forumPost);
        ForumUser commentUser = userService.getById(loginUser.getId());
        if (commentUser != null) {
            commentUser.setCommentCount(safeLong(commentUser.getCommentCount()) + 1L);
            userService.updateById(commentUser);
        }
        if (!CommentConstant.ROOT_COMMENT_ID.equals(rootCommentId)) {
            ForumComment rootComment = this.getById(rootCommentId);
            if (rootComment != null) {
                rootComment.setChildCount(safeLong(rootComment.getChildCount()) + 1L);
                this.updateById(rootComment);
            }
        }
        log.info("新增评论 uid:{} commentId:{} postId:{}", loginUser.getId(), forumComment.getId(), commentAddReq.getPostId());
        return forumComment.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteComment(Long id, ForumUser loginUser) {
        ThrowUtils.throwIf(id == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null || loginUser.getId() == null, ErrorCode.NOT_LOGIN_ERROR);
        ForumComment forumComment = this.getById(id);
        ThrowUtils.throwIf(forumComment == null, ErrorCode.NOT_FOUND_ERROR, "评论不存在");
        ThrowUtils.throwIf(!forumComment.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser), ErrorCode.NO_AUTH_ERROR, "无权限删除评论");
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "删除评论失败");
        ForumPost forumPost = postService.getById(forumComment.getPostId());
        if (forumPost != null) {
            forumPost.setCommentCount(Math.max(safeLong(forumPost.getCommentCount()) - 1L, 0L));
            postService.updateById(forumPost);
        }
        ForumUser commentUser = userService.getById(forumComment.getUserId());
        if (commentUser != null) {
            commentUser.setCommentCount(Math.max(safeLong(commentUser.getCommentCount()) - 1L, 0L));
            userService.updateById(commentUser);
        }
        log.info("删除评论 uid:{} commentId:{}", loginUser.getId(), id);
        return true;
    }

    @Override
    public Page<CommentVO> listCommentByPage(CommentQueryReq commentQueryReq, boolean adminQuery) {
        ThrowUtils.throwIf(commentQueryReq == null, ErrorCode.PARAMS_ERROR);
        QueryWrapper<ForumComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(commentQueryReq.getPostId() != null, "postId", commentQueryReq.getPostId());
        queryWrapper.eq(commentQueryReq.getRootCommentId() != null, "rootCommentId", commentQueryReq.getRootCommentId());
        if (!adminQuery) {
            queryWrapper.eq("commentStatus", CommentConstant.COMMENT_STATUS_NORMAL);
            queryWrapper.eq("auditStatus", PostConstant.AUDIT_STATUS_PASS);
        }
        queryWrapper.orderByAsc("createTime");
        Page<ForumComment> commentPage = this.page(new Page<>(commentQueryReq.getCurrent(), commentQueryReq.getPageSize()), queryWrapper);
        Page<CommentVO> commentVOPage = new Page<>(commentQueryReq.getCurrent(), commentQueryReq.getPageSize(), commentPage.getTotal());
        commentVOPage.setRecords(commentPage.getRecords().stream().map(this::buildCommentVO).collect(Collectors.toList()));
        return commentVOPage;
    }

    @Override
    public Boolean auditComment(CommentAuditReq commentAuditReq, ForumUser loginUser) {
        ThrowUtils.throwIf(commentAuditReq == null || commentAuditReq.getId() == null || commentAuditReq.getAuditStatus() == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null || loginUser.getId() == null, ErrorCode.NOT_LOGIN_ERROR);
        ForumComment forumComment = this.getById(commentAuditReq.getId());
        ThrowUtils.throwIf(forumComment == null, ErrorCode.NOT_FOUND_ERROR, "评论不存在");
        forumComment.setAuditStatus(commentAuditReq.getAuditStatus());
        boolean result = this.updateById(forumComment);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "审核评论失败");
        log.info("审核评论 uid:{} commentId:{} auditStatus:{}", loginUser.getId(), commentAuditReq.getId(), commentAuditReq.getAuditStatus());
        return true;
    }

    private CommentVO buildCommentVO(ForumComment forumComment) {
        CommentVO commentVO = new CommentVO();
        BeanUtils.copyProperties(forumComment, commentVO);
        ForumUser forumUser = userService.getById(forumComment.getUserId());
        if (forumUser != null) {
            commentVO.setUserName(forumUser.getUserName());
            commentVO.setUserAvatar(forumUser.getUserAvatar());
        }
        if (forumComment.getReplyUserId() != null) {
            ForumUser replyUser = userService.getById(forumComment.getReplyUserId());
            if (replyUser != null) {
                commentVO.setReplyUserName(replyUser.getUserName());
            }
        }
        return commentVO;
    }

    private long safeLong(Long value) {
        return value == null ? 0L : value;
    }
}
