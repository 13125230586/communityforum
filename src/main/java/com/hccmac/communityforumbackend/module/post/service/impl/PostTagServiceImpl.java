package com.hccmac.communityforumbackend.module.post.service.impl;

import org.springframework.beans.BeanUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hccmac.communityforumbackend.common.ErrorCode;
import com.hccmac.communityforumbackend.exception.ThrowUtils;
import com.hccmac.communityforumbackend.module.post.entity.ForumPostTag;
import com.hccmac.communityforumbackend.mapper.ForumPostTagMapper;
import com.hccmac.communityforumbackend.mapper.ForumPostTagRelMapper;
import com.hccmac.communityforumbackend.module.post.model.dto.PostTagAddReq;
import com.hccmac.communityforumbackend.module.post.model.dto.PostTagQueryReq;
import com.hccmac.communityforumbackend.module.post.model.dto.PostTagUpdateReq;
import com.hccmac.communityforumbackend.module.post.model.vo.PostTagVO;
import com.hccmac.communityforumbackend.module.post.service.PostTagService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签服务实现
 */
@Service
@Slf4j
public class PostTagServiceImpl extends ServiceImpl<ForumPostTagMapper, ForumPostTag> implements PostTagService {

    private static final int DEFAULT_TAG_STATUS = 0;

    @Resource
    private ForumPostTagRelMapper forumPostTagRelMapper;

    @Override
    public Long addPostTag(PostTagAddReq postTagAddReq) {
        ThrowUtils.throwIf(postTagAddReq == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StringUtils.isBlank(postTagAddReq.getTagName()), ErrorCode.PARAMS_ERROR, "标签名称不能为空");
        validateUniqueTagName(postTagAddReq.getTagName(), null);
        ForumPostTag forumPostTag = new ForumPostTag();
        forumPostTag.setTagName(postTagAddReq.getTagName().trim());
        forumPostTag.setSortOrder(postTagAddReq.getSortOrder() == null ? 0 : postTagAddReq.getSortOrder());
        forumPostTag.setTagStatus(postTagAddReq.getTagStatus() == null ? DEFAULT_TAG_STATUS : postTagAddReq.getTagStatus());
        forumPostTag.setUseCount(0L);
        boolean result = this.save(forumPostTag);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "新增标签失败");
        log.info("新增标签 tagId:{} tagName:{}", forumPostTag.getId(), forumPostTag.getTagName());
        return forumPostTag.getId();
    }

    @Override
    public Boolean updatePostTag(PostTagUpdateReq postTagUpdateReq) {
        ThrowUtils.throwIf(postTagUpdateReq == null || postTagUpdateReq.getId() == null, ErrorCode.PARAMS_ERROR);
        ForumPostTag oldPostTag = this.getById(postTagUpdateReq.getId());
        ThrowUtils.throwIf(oldPostTag == null, ErrorCode.NOT_FOUND_ERROR, "标签不存在");
        if (StringUtils.isNotBlank(postTagUpdateReq.getTagName())) {
            validateUniqueTagName(postTagUpdateReq.getTagName(), postTagUpdateReq.getId());
        }
        ForumPostTag forumPostTag = new ForumPostTag();
        BeanUtils.copyProperties(postTagUpdateReq, forumPostTag);
        if (StringUtils.isNotBlank(postTagUpdateReq.getTagName())) {
            forumPostTag.setTagName(postTagUpdateReq.getTagName().trim());
        }
        boolean result = this.updateById(forumPostTag);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新标签失败");
        log.info("更新标签 tagId:{}", postTagUpdateReq.getId());
        return true;
    }

    @Override
    public Boolean deletePostTag(Long id) {
        ThrowUtils.throwIf(id == null, ErrorCode.PARAMS_ERROR);
        ForumPostTag forumPostTag = this.getById(id);
        ThrowUtils.throwIf(forumPostTag == null, ErrorCode.NOT_FOUND_ERROR, "标签不存在");
        QueryWrapper<com.hccmac.communityforumbackend.module.post.entity.ForumPostTagRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tagId", id);
        ThrowUtils.throwIf(forumPostTagRelMapper.selectCount(queryWrapper) > 0, ErrorCode.OPERATION_ERROR, "标签已被使用");
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "删除标签失败");
        log.info("删除标签 tagId:{}", id);
        return true;
    }

    @Override
    public Page<PostTagVO> listPostTagByPage(PostTagQueryReq postTagQueryReq) {
        ThrowUtils.throwIf(postTagQueryReq == null, ErrorCode.PARAMS_ERROR);
        QueryWrapper<ForumPostTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(postTagQueryReq.getTagName()), "tagName", postTagQueryReq.getTagName());
        queryWrapper.eq(postTagQueryReq.getTagStatus() != null, "tagStatus", postTagQueryReq.getTagStatus());
        queryWrapper.orderByAsc("sortOrder");
        queryWrapper.orderByDesc("useCount");
        Page<ForumPostTag> postTagPage = this.page(new Page<>(postTagQueryReq.getCurrent(), postTagQueryReq.getPageSize()), queryWrapper);
        Page<PostTagVO> postTagVOPage = new Page<>(postTagQueryReq.getCurrent(), postTagQueryReq.getPageSize(), postTagPage.getTotal());
        postTagVOPage.setRecords(postTagPage.getRecords().stream().map(this::toPostTagVO).collect(Collectors.toList()));
        return postTagVOPage;
    }

    @Override
    public List<PostTagVO> listEnableTag() {
        QueryWrapper<ForumPostTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tagStatus", DEFAULT_TAG_STATUS);
        queryWrapper.orderByAsc("sortOrder");
        queryWrapper.orderByDesc("useCount");
        return this.list(queryWrapper).stream().map(this::toPostTagVO).collect(Collectors.toList());
    }

    @Override
    public void validTagIdList(List<Long> tagIdList) {
        if (tagIdList == null || tagIdList.isEmpty()) {
            return;
        }
        List<Long> validTagIdList = tagIdList.stream().filter(java.util.Objects::nonNull).distinct().collect(Collectors.toList());
        if (validTagIdList.isEmpty()) {
            return;
        }
        List<ForumPostTag> forumPostTagList = this.listByIds(validTagIdList);
        ThrowUtils.throwIf(forumPostTagList.size() != validTagIdList.size(), ErrorCode.PARAMS_ERROR, "标签不存在");
        boolean hasDisableTag = forumPostTagList.stream().anyMatch(item -> item.getTagStatus() == null || item.getTagStatus() != DEFAULT_TAG_STATUS);
        ThrowUtils.throwIf(hasDisableTag, ErrorCode.PARAMS_ERROR, "存在不可用标签");
    }

    @Override
    public void batchUpdateUseCount(List<Long> tagIdList, long delta) {
        if (tagIdList == null || tagIdList.isEmpty() || delta == 0) {
            return;
        }
        List<Long> validTagIdList = tagIdList.stream().filter(java.util.Objects::nonNull).distinct().collect(Collectors.toList());
        if (validTagIdList.isEmpty()) {
            return;
        }
        List<ForumPostTag> forumPostTagList = this.listByIds(validTagIdList);
        for (ForumPostTag forumPostTag : forumPostTagList) {
            long useCount = forumPostTag.getUseCount() == null ? 0L : forumPostTag.getUseCount();
            forumPostTag.setUseCount(Math.max(useCount + delta, 0L));
            this.updateById(forumPostTag);
        }
    }

    private void validateUniqueTagName(String tagName, Long excludeId) {
        QueryWrapper<ForumPostTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tagName", tagName.trim());
        queryWrapper.ne(excludeId != null, "id", excludeId);
        ThrowUtils.throwIf(this.count(queryWrapper) > 0, ErrorCode.PARAMS_ERROR, "标签名称已存在");
    }

    private PostTagVO toPostTagVO(ForumPostTag forumPostTag) {
        PostTagVO postTagVO = new PostTagVO();
        BeanUtils.copyProperties(forumPostTag, postTagVO);
        return postTagVO;
    }
}
