package com.hccmac.communityforumbackend.module.post.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hccmac.communityforumbackend.module.post.entity.ForumPost;
import com.hccmac.communityforumbackend.module.post.model.dto.PostAddReq;
import com.hccmac.communityforumbackend.module.post.model.dto.PostAuditReq;
import com.hccmac.communityforumbackend.module.post.model.dto.PostFlagUpdateReq;
import com.hccmac.communityforumbackend.module.post.model.dto.PostQueryReq;
import com.hccmac.communityforumbackend.module.post.model.dto.PostUpdateReq;
import com.hccmac.communityforumbackend.module.post.model.vo.PostDetailVO;
import com.hccmac.communityforumbackend.module.post.model.vo.PostVO;
import com.hccmac.communityforumbackend.module.user.entity.ForumUser;

import java.util.List;

/**
 * 帖子服务
 */
public interface PostService extends IService<ForumPost> {

    Long addPost(PostAddReq postAddReq, ForumUser loginUser);

    Boolean updatePost(PostUpdateReq postUpdateReq, ForumUser loginUser);

    Boolean deletePost(Long id, ForumUser loginUser);

    PostDetailVO getPostDetail(Long id);

    Page<PostVO> listPostByPage(PostQueryReq postQueryReq, boolean adminQuery);

    List<PostVO> listHotPost(Long boardId, long size);

    List<PostVO> listLatestPost(Long boardId, long size);

    Boolean auditPost(PostAuditReq postAuditReq, ForumUser loginUser);

    Boolean updateTopFlag(PostFlagUpdateReq postFlagUpdateReq);

    Boolean updateEssenceFlag(PostFlagUpdateReq postFlagUpdateReq);
}
