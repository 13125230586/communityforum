package com.hccmac.communityforumbackend.module.post.service.impl;

import org.springframework.beans.BeanUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hccmac.communityforumbackend.common.ErrorCode;
import com.hccmac.communityforumbackend.constant.PostConstant;
import com.hccmac.communityforumbackend.exception.ThrowUtils;
import com.hccmac.communityforumbackend.module.board.entity.ForumBoard;
import com.hccmac.communityforumbackend.module.board.service.BoardService;
import com.hccmac.communityforumbackend.module.post.entity.ForumPost;
import com.hccmac.communityforumbackend.module.post.entity.ForumPostContent;
import com.hccmac.communityforumbackend.module.post.entity.ForumPostTag;
import com.hccmac.communityforumbackend.module.post.entity.ForumPostTagRel;
import com.hccmac.communityforumbackend.mapper.ForumPostContentMapper;
import com.hccmac.communityforumbackend.mapper.ForumPostMapper;
import com.hccmac.communityforumbackend.mapper.ForumPostTagMapper;
import com.hccmac.communityforumbackend.mapper.ForumPostTagRelMapper;
import com.hccmac.communityforumbackend.module.post.model.dto.PostAddReq;
import com.hccmac.communityforumbackend.module.post.model.dto.PostAuditReq;
import com.hccmac.communityforumbackend.module.post.model.dto.PostFlagUpdateReq;
import com.hccmac.communityforumbackend.module.post.model.dto.PostQueryReq;
import com.hccmac.communityforumbackend.module.post.model.dto.PostUpdateReq;
import com.hccmac.communityforumbackend.module.post.model.vo.PostDetailVO;
import com.hccmac.communityforumbackend.module.post.model.vo.PostVO;
import com.hccmac.communityforumbackend.module.post.service.PostService;
import com.hccmac.communityforumbackend.module.post.service.PostTagService;
import com.hccmac.communityforumbackend.module.user.entity.ForumUser;
import com.hccmac.communityforumbackend.module.user.service.UserService;
import com.hccmac.communityforumbackend.utils.HotScoreUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 帖子服务实现
 */
@Service
@Slf4j
public class PostServiceImpl extends ServiceImpl<ForumPostMapper, ForumPost> implements PostService {

    private static final int DEFAULT_SOURCE_TYPE = 1;
    private static final int DEFAULT_FLAG = 0;
    private static final int DEFAULT_COMMENT_ALLOWED_FLAG = 1;
    private static final long DEFAULT_LIST_SIZE = 10L;
    private static final String SORT_TYPE_LATEST = "latest";

    @Resource
    private ForumPostContentMapper forumPostContentMapper;

    @Resource
    private ForumPostTagRelMapper forumPostTagRelMapper;

    @Resource
    private ForumPostTagMapper forumPostTagMapper;

    @Resource
    private BoardService boardService;

    @Resource
    private UserService userService;

    @Resource
    private PostTagService postTagService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addPost(PostAddReq postAddReq, ForumUser loginUser) {
        ThrowUtils.throwIf(postAddReq == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null || loginUser.getId() == null, ErrorCode.NOT_LOGIN_ERROR);
        validPostAddReq(postAddReq);
        ForumBoard forumBoard = boardService.getById(postAddReq.getBoardId());
        ThrowUtils.throwIf(forumBoard == null, ErrorCode.NOT_FOUND_ERROR, "板块不存在");
        postTagService.validTagIdList(postAddReq.getTagIdList());

        ForumPost forumPost = new ForumPost();
        forumPost.setBoardId(postAddReq.getBoardId());
        forumPost.setUserId(loginUser.getId());
        forumPost.setPostTitle(postAddReq.getPostTitle().trim());
        forumPost.setPostSummary(buildSummary(postAddReq.getPostSummary(), postAddReq.getContent()));
        forumPost.setCoverImage(postAddReq.getCoverImage());
        forumPost.setPostSourceType(DEFAULT_SOURCE_TYPE);
        forumPost.setPostStatus(PostConstant.POST_STATUS_PUBLISHED);
        forumPost.setAuditStatus(forumBoard.getPostAuditFlag() == null || forumBoard.getPostAuditFlag() == DEFAULT_FLAG ? PostConstant.AUDIT_STATUS_PASS : PostConstant.AUDIT_STATUS_WAIT);
        forumPost.setAnonymousFlag(defaultZero(postAddReq.getAnonymousFlag()));
        forumPost.setTopFlag(DEFAULT_FLAG);
        forumPost.setEssenceFlag(DEFAULT_FLAG);
        forumPost.setCommentAllowedFlag(postAddReq.getCommentAllowedFlag() == null ? DEFAULT_COMMENT_ALLOWED_FLAG : postAddReq.getCommentAllowedFlag());
        forumPost.setViewCount(0L);
        forumPost.setCommentCount(0L);
        forumPost.setLikeCount(0L);
        forumPost.setCollectCount(0L);
        forumPost.setReportCount(0L);
        forumPost.setHotScore(0L);
        if (PostConstant.AUDIT_STATUS_PASS.equals(forumPost.getAuditStatus())) {
            forumPost.setPublishTime(new Date());
        }
        boolean saveResult = this.save(forumPost);
        ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR, "新增帖子失败");

        ForumPostContent forumPostContent = new ForumPostContent();
        forumPostContent.setPostId(forumPost.getId());
        forumPostContent.setContentType(postAddReq.getContentType());
        forumPostContent.setContent(postAddReq.getContent());
        forumPostContent.setContentText(extractText(postAddReq.getContent()));
        forumPostContent.setWordCount(lengthOfText(forumPostContent.getContentText()));
        forumPostContentMapper.insert(forumPostContent);

        saveTagRel(forumPost.getId(), distinctTagIdList(postAddReq.getTagIdList()));
        updateBoardPostCount(postAddReq.getBoardId(), 1L);
        updateUserPostCount(loginUser.getId(), 1L);
        log.info("新增帖子 uid:{} postId:{} boardId:{}", loginUser.getId(), forumPost.getId(), postAddReq.getBoardId());
        return forumPost.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePost(PostUpdateReq postUpdateReq, ForumUser loginUser) {
        ThrowUtils.throwIf(postUpdateReq == null || postUpdateReq.getId() == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null || loginUser.getId() == null, ErrorCode.NOT_LOGIN_ERROR);
        ForumPost oldPost = this.getById(postUpdateReq.getId());
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR, "帖子不存在");
        ThrowUtils.throwIf(!oldPost.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser), ErrorCode.NO_AUTH_ERROR, "无权限修改帖子");
        ThrowUtils.throwIf(StringUtils.isBlank(postUpdateReq.getPostTitle()), ErrorCode.PARAMS_ERROR, "标题不能为空");
        ThrowUtils.throwIf(StringUtils.isBlank(postUpdateReq.getContentType()), ErrorCode.PARAMS_ERROR, "内容类型不能为空");
        ThrowUtils.throwIf(StringUtils.isBlank(postUpdateReq.getContent()), ErrorCode.PARAMS_ERROR, "内容不能为空");
        if (postUpdateReq.getBoardId() != null) {
            ForumBoard forumBoard = boardService.getById(postUpdateReq.getBoardId());
            ThrowUtils.throwIf(forumBoard == null, ErrorCode.NOT_FOUND_ERROR, "板块不存在");
        }
        postTagService.validTagIdList(postUpdateReq.getTagIdList());

        ForumPost updatePost = new ForumPost();
        updatePost.setId(postUpdateReq.getId());
        if (postUpdateReq.getBoardId() != null) {
            updatePost.setBoardId(postUpdateReq.getBoardId());
        }
        updatePost.setPostTitle(postUpdateReq.getPostTitle().trim());
        updatePost.setPostSummary(buildSummary(postUpdateReq.getPostSummary(), postUpdateReq.getContent()));
        updatePost.setCoverImage(postUpdateReq.getCoverImage());
        updatePost.setAnonymousFlag(defaultZero(postUpdateReq.getAnonymousFlag()));
        updatePost.setCommentAllowedFlag(postUpdateReq.getCommentAllowedFlag() == null ? DEFAULT_COMMENT_ALLOWED_FLAG : postUpdateReq.getCommentAllowedFlag());
        updatePost.setEditTime(new Date());
        boolean result = this.updateById(updatePost);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新帖子失败");

        QueryWrapper<ForumPostContent> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper.eq("postId", postUpdateReq.getId());
        ForumPostContent forumPostContent = forumPostContentMapper.selectOne(contentQueryWrapper);
        ThrowUtils.throwIf(forumPostContent == null, ErrorCode.NOT_FOUND_ERROR, "帖子内容不存在");
        forumPostContent.setContentType(postUpdateReq.getContentType());
        forumPostContent.setContent(postUpdateReq.getContent());
        forumPostContent.setContentText(extractText(postUpdateReq.getContent()));
        forumPostContent.setWordCount(lengthOfText(forumPostContent.getContentText()));
        forumPostContentMapper.updateById(forumPostContent);

        List<Long> oldTagIdList = listTagIdByPostId(postUpdateReq.getId());
        postTagService.batchUpdateUseCount(oldTagIdList, -1L);
        QueryWrapper<ForumPostTagRel> relQueryWrapper = new QueryWrapper<>();
        relQueryWrapper.eq("postId", postUpdateReq.getId());
        forumPostTagRelMapper.delete(relQueryWrapper);
        saveTagRel(postUpdateReq.getId(), distinctTagIdList(postUpdateReq.getTagIdList()));
        if (postUpdateReq.getBoardId() != null && !postUpdateReq.getBoardId().equals(oldPost.getBoardId())) {
            updateBoardPostCount(oldPost.getBoardId(), -1L);
            updateBoardPostCount(postUpdateReq.getBoardId(), 1L);
        }
        log.info("更新帖子 uid:{} postId:{}", loginUser.getId(), postUpdateReq.getId());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePost(Long id, ForumUser loginUser) {
        ThrowUtils.throwIf(id == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null || loginUser.getId() == null, ErrorCode.NOT_LOGIN_ERROR);
        ForumPost forumPost = this.getById(id);
        ThrowUtils.throwIf(forumPost == null, ErrorCode.NOT_FOUND_ERROR, "帖子不存在");
        ThrowUtils.throwIf(!forumPost.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser), ErrorCode.NO_AUTH_ERROR, "无权限删除帖子");
        postTagService.batchUpdateUseCount(listTagIdByPostId(id), -1L);
        QueryWrapper<ForumPostTagRel> relQueryWrapper = new QueryWrapper<>();
        relQueryWrapper.eq("postId", id);
        forumPostTagRelMapper.delete(relQueryWrapper);
        QueryWrapper<ForumPostContent> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper.eq("postId", id);
        forumPostContentMapper.delete(contentQueryWrapper);
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "删除帖子失败");
        updateBoardPostCount(forumPost.getBoardId(), -1L);
        updateUserPostCount(forumPost.getUserId(), -1L);
        log.info("删除帖子 uid:{} postId:{}", loginUser.getId(), id);
        return true;
    }

    @Override
    public PostDetailVO getPostDetail(Long id) {
        ThrowUtils.throwIf(id == null, ErrorCode.PARAMS_ERROR);
        ForumPost forumPost = this.getById(id);
        ThrowUtils.throwIf(forumPost == null, ErrorCode.NOT_FOUND_ERROR, "帖子不存在");
        PostDetailVO postDetailVO = buildPostDetailVO(forumPost);
        forumPost.setViewCount(safeLong(forumPost.getViewCount()) + 1L);
        forumPost.setHotScore(HotScoreUtil.calculateHotScore(safeLong(forumPost.getViewCount()), safeLong(forumPost.getCommentCount()), safeLong(forumPost.getLikeCount()), safeLong(forumPost.getCollectCount())));
        this.updateById(forumPost);
        postDetailVO.setViewCount(forumPost.getViewCount());
        postDetailVO.setHotScore(forumPost.getHotScore());
        return postDetailVO;
    }

    @Override
    public Page<PostVO> listPostByPage(PostQueryReq postQueryReq, boolean adminQuery) {
        ThrowUtils.throwIf(postQueryReq == null, ErrorCode.PARAMS_ERROR);
        QueryWrapper<ForumPost> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(postQueryReq.getBoardId() != null, "boardId", postQueryReq.getBoardId());
        queryWrapper.like(StringUtils.isNotBlank(postQueryReq.getPostTitle()), "postTitle", postQueryReq.getPostTitle());
        queryWrapper.eq(postQueryReq.getPostStatus() != null, "postStatus", postQueryReq.getPostStatus());
        queryWrapper.eq(postQueryReq.getAuditStatus() != null, "auditStatus", postQueryReq.getAuditStatus());
        queryWrapper.eq(postQueryReq.getUserId() != null, "userId", postQueryReq.getUserId());
        if (!adminQuery) {
            queryWrapper.eq("postStatus", PostConstant.POST_STATUS_PUBLISHED);
            queryWrapper.eq("auditStatus", PostConstant.AUDIT_STATUS_PASS);
        }
        buildPostPageSort(queryWrapper, postQueryReq.getSortType(), adminQuery);
        Page<ForumPost> postPage = this.page(new Page<>(postQueryReq.getCurrent(), postQueryReq.getPageSize()), queryWrapper);
        Page<PostVO> postVOPage = new Page<>(postQueryReq.getCurrent(), postQueryReq.getPageSize(), postPage.getTotal());
        postVOPage.setRecords(postPage.getRecords().stream().map(this::buildPostVO).collect(Collectors.toList()));
        return postVOPage;
    }

    @Override
    public List<PostVO> listHotPost(Long boardId, long size) {
        QueryWrapper<ForumPost> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(boardId != null, "boardId", boardId);
        queryWrapper.eq("postStatus", PostConstant.POST_STATUS_PUBLISHED);
        queryWrapper.eq("auditStatus", PostConstant.AUDIT_STATUS_PASS);
        queryWrapper.orderByDesc("hotScore");
        queryWrapper.last("limit " + (size <= 0 ? DEFAULT_LIST_SIZE : size));
        return this.list(queryWrapper).stream().map(this::buildPostVO).collect(Collectors.toList());
    }

    @Override
    public List<PostVO> listLatestPost(Long boardId, long size) {
        QueryWrapper<ForumPost> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(boardId != null, "boardId", boardId);
        queryWrapper.eq("postStatus", PostConstant.POST_STATUS_PUBLISHED);
        queryWrapper.eq("auditStatus", PostConstant.AUDIT_STATUS_PASS);
        queryWrapper.orderByDesc("publishTime");
        queryWrapper.last("limit " + (size <= 0 ? DEFAULT_LIST_SIZE : size));
        return this.list(queryWrapper).stream().map(this::buildPostVO).collect(Collectors.toList());
    }

    @Override
    public Boolean auditPost(PostAuditReq postAuditReq, ForumUser loginUser) {
        ThrowUtils.throwIf(postAuditReq == null || postAuditReq.getId() == null || postAuditReq.getAuditStatus() == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null || loginUser.getId() == null, ErrorCode.NOT_LOGIN_ERROR);
        ForumPost forumPost = this.getById(postAuditReq.getId());
        ThrowUtils.throwIf(forumPost == null, ErrorCode.NOT_FOUND_ERROR, "帖子不存在");
        forumPost.setAuditStatus(postAuditReq.getAuditStatus());
        forumPost.setAuditRemark(postAuditReq.getAuditRemark());
        forumPost.setAuditUserId(loginUser.getId());
        forumPost.setAuditTime(new Date());
        if (PostConstant.AUDIT_STATUS_PASS.equals(postAuditReq.getAuditStatus())) {
            forumPost.setPostStatus(PostConstant.POST_STATUS_PUBLISHED);
            if (forumPost.getPublishTime() == null) {
                forumPost.setPublishTime(new Date());
            }
        } else if (PostConstant.AUDIT_STATUS_REJECT.equals(postAuditReq.getAuditStatus())) {
            forumPost.setPostStatus(PostConstant.POST_STATUS_OFFLINE);
        }
        boolean result = this.updateById(forumPost);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "审核帖子失败");
        log.info("审核帖子 uid:{} postId:{} auditStatus:{}", loginUser.getId(), postAuditReq.getId(), postAuditReq.getAuditStatus());
        return true;
    }

    @Override
    public Boolean updateTopFlag(PostFlagUpdateReq postFlagUpdateReq) {
        ThrowUtils.throwIf(postFlagUpdateReq == null || postFlagUpdateReq.getId() == null || postFlagUpdateReq.getFlagValue() == null, ErrorCode.PARAMS_ERROR);
        ForumPost forumPost = new ForumPost();
        forumPost.setId(postFlagUpdateReq.getId());
        forumPost.setTopFlag(postFlagUpdateReq.getFlagValue());
        boolean result = this.updateById(forumPost);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新置顶失败");
        log.info("更新置顶 postId:{} topFlag:{}", postFlagUpdateReq.getId(), postFlagUpdateReq.getFlagValue());
        return true;
    }

    @Override
    public Boolean updateEssenceFlag(PostFlagUpdateReq postFlagUpdateReq) {
        ThrowUtils.throwIf(postFlagUpdateReq == null || postFlagUpdateReq.getId() == null || postFlagUpdateReq.getFlagValue() == null, ErrorCode.PARAMS_ERROR);
        ForumPost forumPost = new ForumPost();
        forumPost.setId(postFlagUpdateReq.getId());
        forumPost.setEssenceFlag(postFlagUpdateReq.getFlagValue());
        boolean result = this.updateById(forumPost);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新精华失败");
        log.info("更新精华 postId:{} essenceFlag:{}", postFlagUpdateReq.getId(), postFlagUpdateReq.getFlagValue());
        return true;
    }

    private void validPostAddReq(PostAddReq postAddReq) {
        ThrowUtils.throwIf(postAddReq.getBoardId() == null, ErrorCode.PARAMS_ERROR, "板块不能为空");
        ThrowUtils.throwIf(StringUtils.isBlank(postAddReq.getPostTitle()), ErrorCode.PARAMS_ERROR, "标题不能为空");
        ThrowUtils.throwIf(StringUtils.isBlank(postAddReq.getContentType()), ErrorCode.PARAMS_ERROR, "内容类型不能为空");
        ThrowUtils.throwIf(StringUtils.isBlank(postAddReq.getContent()), ErrorCode.PARAMS_ERROR, "内容不能为空");
    }

    private String buildSummary(String postSummary, String content) {
        if (StringUtils.isNotBlank(postSummary)) {
            return StringUtils.substring(postSummary.trim(), 0, 200);
        }
        String text = extractText(content);
        return StringUtils.substring(text, 0, 200);
    }

    private String extractText(String content) {
        if (StringUtils.isBlank(content)) {
            return StringUtils.EMPTY;
        }
        return content.replaceAll("<[^>]+>", " ").replaceAll("\\s+", " ").trim();
    }

    private Integer lengthOfText(String text) {
        return text == null ? 0 : text.length();
    }

    private Integer defaultZero(Integer value) {
        return value == null ? DEFAULT_FLAG : value;
    }

    private void buildPostPageSort(QueryWrapper<ForumPost> queryWrapper, String sortType, boolean adminQuery) {
        if (!adminQuery && SORT_TYPE_LATEST.equals(sortType)) {
            queryWrapper.orderByDesc("publishTime");
            queryWrapper.orderByDesc("createTime");
            return;
        }
        queryWrapper.orderByDesc("topFlag");
        queryWrapper.orderByDesc("publishTime");
        queryWrapper.orderByDesc("createTime");
    }

    private long safeLong(Long value) {
        return value == null ? 0L : value;
    }

    private void saveTagRel(Long postId, List<Long> tagIdList) {
        if (tagIdList == null || tagIdList.isEmpty()) {
            return;
        }
        for (Long tagId : tagIdList) {
            if (tagId == null) {
                continue;
            }
            ForumPostTagRel forumPostTagRel = new ForumPostTagRel();
            forumPostTagRel.setPostId(postId);
            forumPostTagRel.setTagId(tagId);
            forumPostTagRelMapper.insert(forumPostTagRel);
        }
        postTagService.batchUpdateUseCount(tagIdList, 1L);
    }

    private void updateBoardPostCount(Long boardId, Long delta) {
        if (boardId == null || delta == null) {
            return;
        }
        ForumBoard forumBoard = boardService.getById(boardId);
        if (forumBoard == null) {
            return;
        }
        long postCount = safeLong(forumBoard.getPostCount()) + delta;
        forumBoard.setPostCount(Math.max(postCount, 0L));
        boardService.updateById(forumBoard);
    }

    private void updateUserPostCount(Long userId, Long delta) {
        if (userId == null || delta == null) {
            return;
        }
        ForumUser forumUser = userService.getById(userId);
        if (forumUser == null) {
            return;
        }
        long postCount = safeLong(forumUser.getPostCount()) + delta;
        forumUser.setPostCount(Math.max(postCount, 0L));
        userService.updateById(forumUser);
    }

    private PostVO buildPostVO(ForumPost forumPost) {
        PostVO postVO = new PostVO();
        BeanUtils.copyProperties(forumPost, postVO);
        ForumBoard forumBoard = boardService.getById(forumPost.getBoardId());
        if (forumBoard != null) {
            postVO.setBoardName(forumBoard.getBoardName());
        }
        ForumUser forumUser = userService.getById(forumPost.getUserId());
        if (forumUser != null) {
            postVO.setUserName(forumUser.getUserName());
            postVO.setUserAvatar(forumUser.getUserAvatar());
        }
        fillTagInfo(postVO, forumPost.getId());
        return postVO;
    }

    private PostDetailVO buildPostDetailVO(ForumPost forumPost) {
        PostDetailVO postDetailVO = new PostDetailVO();
        BeanUtils.copyProperties(buildPostVO(forumPost), postDetailVO);
        QueryWrapper<ForumPostContent> contentQueryWrapper = new QueryWrapper<>();
        contentQueryWrapper.eq("postId", forumPost.getId());
        ForumPostContent forumPostContent = forumPostContentMapper.selectOne(contentQueryWrapper);
        if (forumPostContent != null) {
            postDetailVO.setContentType(forumPostContent.getContentType());
            postDetailVO.setContent(forumPostContent.getContent());
            postDetailVO.setContentText(forumPostContent.getContentText());
            postDetailVO.setWordCount(forumPostContent.getWordCount());
        }
        postDetailVO.setLastCommentTime(forumPost.getLastCommentTime());
        postDetailVO.setEditTime(forumPost.getEditTime());
        return postDetailVO;
    }

    private void fillTagInfo(PostVO postVO, Long postId) {
        QueryWrapper<ForumPostTagRel> relQueryWrapper = new QueryWrapper<>();
        relQueryWrapper.eq("postId", postId);
        List<ForumPostTagRel> relList = forumPostTagRelMapper.selectList(relQueryWrapper);
        if (relList == null || relList.isEmpty()) {
            postVO.setTagIdList(new ArrayList<Long>());
            postVO.setTagNameList(new ArrayList<String>());
            return;
        }
        List<Long> tagIdList = relList.stream().map(ForumPostTagRel::getTagId).collect(Collectors.toList());
        postVO.setTagIdList(tagIdList);
        if (tagIdList.isEmpty()) {
            postVO.setTagNameList(new ArrayList<String>());
            return;
        }
        List<ForumPostTag> forumPostTagList = forumPostTagMapper.selectBatchIds(tagIdList);
        postVO.setTagNameList(forumPostTagList.stream().map(ForumPostTag::getTagName).collect(Collectors.toList()));
    }

    private List<Long> listTagIdByPostId(Long postId) {
        QueryWrapper<ForumPostTagRel> relQueryWrapper = new QueryWrapper<>();
        relQueryWrapper.eq("postId", postId);
        List<ForumPostTagRel> relList = forumPostTagRelMapper.selectList(relQueryWrapper);
        if (relList == null || relList.isEmpty()) {
            return new ArrayList<Long>();
        }
        return relList.stream().map(ForumPostTagRel::getTagId).collect(Collectors.toList());
    }

    private List<Long> distinctTagIdList(List<Long> tagIdList) {
        if (tagIdList == null || tagIdList.isEmpty()) {
            return new ArrayList<Long>();
        }
        return tagIdList.stream().filter(java.util.Objects::nonNull).distinct().collect(Collectors.toList());
    }
}
