package com.hccmac.communityforumbackend.module.interaction.service;

import com.hccmac.communityforumbackend.module.user.entity.ForumUser;

/**
 * 互动服务
 */
public interface InteractionService {

    Boolean togglePostLike(Long postId, ForumUser loginUser);

    Boolean toggleCommentLike(Long commentId, ForumUser loginUser);

    Boolean togglePostCollect(Long postId, ForumUser loginUser);
}
