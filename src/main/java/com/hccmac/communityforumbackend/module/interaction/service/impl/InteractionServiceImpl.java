package com.hccmac.communityforumbackend.module.interaction.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hccmac.communityforumbackend.common.ErrorCode;
import com.hccmac.communityforumbackend.exception.ThrowUtils;
import com.hccmac.communityforumbackend.module.comment.entity.ForumComment;
import com.hccmac.communityforumbackend.mapper.ForumCommentMapper;
import com.hccmac.communityforumbackend.module.interaction.entity.ForumCollectRecord;
import com.hccmac.communityforumbackend.module.interaction.entity.ForumLikeRecord;
import com.hccmac.communityforumbackend.mapper.ForumCollectRecordMapper;
import com.hccmac.communityforumbackend.mapper.ForumLikeRecordMapper;
import com.hccmac.communityforumbackend.module.interaction.service.InteractionService;
import com.hccmac.communityforumbackend.module.post.entity.ForumPost;
import com.hccmac.communityforumbackend.mapper.ForumPostMapper;
import com.hccmac.communityforumbackend.module.user.entity.ForumUser;
import com.hccmac.communityforumbackend.mapper.ForumUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 互动服务实现
 */
@Service
@Slf4j
public class InteractionServiceImpl implements InteractionService {

    private static final String BIZ_TYPE_POST = "post";
    private static final String BIZ_TYPE_COMMENT = "comment";

    @Resource
    private ForumLikeRecordMapper forumLikeRecordMapper;

    @Resource
    private ForumCollectRecordMapper forumCollectRecordMapper;

    @Resource
    private ForumPostMapper forumPostMapper;

    @Resource
    private ForumCommentMapper forumCommentMapper;

    @Resource
    private ForumUserMapper forumUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean togglePostLike(Long postId, ForumUser loginUser) {
        ThrowUtils.throwIf(postId == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null || loginUser.getId() == null, ErrorCode.NOT_LOGIN_ERROR);
        ForumPost forumPost = forumPostMapper.selectById(postId);
        ThrowUtils.throwIf(forumPost == null, ErrorCode.NOT_FOUND_ERROR, "帖子不存在");
        QueryWrapper<ForumLikeRecord> queryWrapper = buildLikeQueryWrapper(BIZ_TYPE_POST, postId, loginUser.getId());
        ForumLikeRecord forumLikeRecord = forumLikeRecordMapper.selectOne(queryWrapper);
        if (forumLikeRecord == null) {
            ForumLikeRecord insertRecord = new ForumLikeRecord();
            insertRecord.setBizType(BIZ_TYPE_POST);
            insertRecord.setBizId(postId);
            insertRecord.setUserId(loginUser.getId());
            forumLikeRecordMapper.insert(insertRecord);
            forumPost.setLikeCount(safeLong(forumPost.getLikeCount()) + 1L);
            updateUserLikeReceivedCount(forumPost.getUserId(), 1L);
            log.info("帖子点赞 uid:{} postId:{}", loginUser.getId(), postId);
        } else {
            forumLikeRecordMapper.deleteById(forumLikeRecord.getId());
            forumPost.setLikeCount(Math.max(safeLong(forumPost.getLikeCount()) - 1L, 0L));
            updateUserLikeReceivedCount(forumPost.getUserId(), -1L);
            log.info("取消帖子点赞 uid:{} postId:{}", loginUser.getId(), postId);
        }
        forumPostMapper.updateById(forumPost);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean toggleCommentLike(Long commentId, ForumUser loginUser) {
        ThrowUtils.throwIf(commentId == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null || loginUser.getId() == null, ErrorCode.NOT_LOGIN_ERROR);
        ForumComment forumComment = forumCommentMapper.selectById(commentId);
        ThrowUtils.throwIf(forumComment == null, ErrorCode.NOT_FOUND_ERROR, "评论不存在");
        QueryWrapper<ForumLikeRecord> queryWrapper = buildLikeQueryWrapper(BIZ_TYPE_COMMENT, commentId, loginUser.getId());
        ForumLikeRecord forumLikeRecord = forumLikeRecordMapper.selectOne(queryWrapper);
        if (forumLikeRecord == null) {
            ForumLikeRecord insertRecord = new ForumLikeRecord();
            insertRecord.setBizType(BIZ_TYPE_COMMENT);
            insertRecord.setBizId(commentId);
            insertRecord.setUserId(loginUser.getId());
            forumLikeRecordMapper.insert(insertRecord);
            forumComment.setLikeCount(safeLong(forumComment.getLikeCount()) + 1L);
            updateUserLikeReceivedCount(forumComment.getUserId(), 1L);
            log.info("评论点赞 uid:{} commentId:{}", loginUser.getId(), commentId);
        } else {
            forumLikeRecordMapper.deleteById(forumLikeRecord.getId());
            forumComment.setLikeCount(Math.max(safeLong(forumComment.getLikeCount()) - 1L, 0L));
            updateUserLikeReceivedCount(forumComment.getUserId(), -1L);
            log.info("取消评论点赞 uid:{} commentId:{}", loginUser.getId(), commentId);
        }
        forumCommentMapper.updateById(forumComment);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean togglePostCollect(Long postId, ForumUser loginUser) {
        ThrowUtils.throwIf(postId == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null || loginUser.getId() == null, ErrorCode.NOT_LOGIN_ERROR);
        ForumPost forumPost = forumPostMapper.selectById(postId);
        ThrowUtils.throwIf(forumPost == null, ErrorCode.NOT_FOUND_ERROR, "帖子不存在");
        QueryWrapper<ForumCollectRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("bizType", BIZ_TYPE_POST);
        queryWrapper.eq("bizId", postId);
        queryWrapper.eq("userId", loginUser.getId());
        ForumCollectRecord forumCollectRecord = forumCollectRecordMapper.selectOne(queryWrapper);
        if (forumCollectRecord == null) {
            ForumCollectRecord insertRecord = new ForumCollectRecord();
            insertRecord.setBizType(BIZ_TYPE_POST);
            insertRecord.setBizId(postId);
            insertRecord.setUserId(loginUser.getId());
            forumCollectRecordMapper.insert(insertRecord);
            forumPost.setCollectCount(safeLong(forumPost.getCollectCount()) + 1L);
            log.info("帖子收藏 uid:{} postId:{}", loginUser.getId(), postId);
        } else {
            forumCollectRecordMapper.deleteById(forumCollectRecord.getId());
            forumPost.setCollectCount(Math.max(safeLong(forumPost.getCollectCount()) - 1L, 0L));
            log.info("取消帖子收藏 uid:{} postId:{}", loginUser.getId(), postId);
        }
        forumPostMapper.updateById(forumPost);
        return true;
    }

    private QueryWrapper<ForumLikeRecord> buildLikeQueryWrapper(String bizType, Long bizId, Long userId) {
        QueryWrapper<ForumLikeRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("bizType", bizType);
        queryWrapper.eq("bizId", bizId);
        queryWrapper.eq("userId", userId);
        return queryWrapper;
    }

    private void updateUserLikeReceivedCount(Long userId, Long delta) {
        if (userId == null || delta == null) {
            return;
        }
        ForumUser forumUser = forumUserMapper.selectById(userId);
        if (forumUser == null) {
            return;
        }
        forumUser.setLikeReceivedCount(Math.max(safeLong(forumUser.getLikeReceivedCount()) + delta, 0L));
        forumUserMapper.updateById(forumUser);
    }

    private long safeLong(Long value) {
        return value == null ? 0L : value;
    }
}
