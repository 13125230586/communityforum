package com.hccmac.communityforumbackend.module.post.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hccmac.communityforumbackend.module.post.entity.ForumPostTag;
import com.hccmac.communityforumbackend.module.post.model.dto.PostTagAddReq;
import com.hccmac.communityforumbackend.module.post.model.dto.PostTagQueryReq;
import com.hccmac.communityforumbackend.module.post.model.dto.PostTagUpdateReq;
import com.hccmac.communityforumbackend.module.post.model.vo.PostTagVO;

import java.util.List;

/**
 * 标签服务
 */
public interface PostTagService extends IService<ForumPostTag> {

    Long addPostTag(PostTagAddReq postTagAddReq);

    Boolean updatePostTag(PostTagUpdateReq postTagUpdateReq);

    Boolean deletePostTag(Long id);

    Page<PostTagVO> listPostTagByPage(PostTagQueryReq postTagQueryReq);

    List<PostTagVO> listEnableTag();

    void validTagIdList(List<Long> tagIdList);

    void batchUpdateUseCount(List<Long> tagIdList, long delta);
}
