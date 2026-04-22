package com.hccmac.communityforumbackend.module.post.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hccmac.communityforumbackend.common.BaseResponse;
import com.hccmac.communityforumbackend.common.DeleteRequest;
import com.hccmac.communityforumbackend.common.ResultUtils;
import com.hccmac.communityforumbackend.module.post.model.dto.PostAddReq;
import com.hccmac.communityforumbackend.module.post.model.dto.PostQueryReq;
import com.hccmac.communityforumbackend.module.post.model.dto.PostUpdateReq;
import com.hccmac.communityforumbackend.module.post.model.vo.PostDetailVO;
import com.hccmac.communityforumbackend.module.post.model.vo.PostVO;
import com.hccmac.communityforumbackend.module.post.service.PostService;
import com.hccmac.communityforumbackend.module.user.entity.ForumUser;
import com.hccmac.communityforumbackend.module.user.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 帖子接口
 */
@RestController
@RequestMapping("/post")
public class PostController {

    @Resource
    private PostService postService;

    @Resource
    private UserService userService;

    @PostMapping("/add")
    public BaseResponse<Long> addPost(@RequestBody PostAddReq postAddReq, HttpServletRequest request) {
        ForumUser loginUser = userService.getLoginUserEntity(request);
        return ResultUtils.success(postService.addPost(postAddReq, loginUser));
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updatePost(@RequestBody PostUpdateReq postUpdateReq, HttpServletRequest request) {
        ForumUser loginUser = userService.getLoginUserEntity(request);
        return ResultUtils.success(postService.updatePost(postUpdateReq, loginUser));
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePost(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        ForumUser loginUser = userService.getLoginUserEntity(request);
        return ResultUtils.success(postService.deletePost(deleteRequest.getId(), loginUser));
    }

    @GetMapping("/get")
    public BaseResponse<PostDetailVO> getPostDetail(@RequestParam Long id) {
        return ResultUtils.success(postService.getPostDetail(id));
    }

    @GetMapping("/list/page")
    public BaseResponse<Page<PostVO>> listPostByPage(PostQueryReq postQueryReq) {
        return ResultUtils.success(postService.listPostByPage(postQueryReq, false));
    }

    @GetMapping("/list/hot")
    public BaseResponse<List<PostVO>> listHotPost(@RequestParam(required = false) Long boardId,
                                                  @RequestParam(required = false, defaultValue = "10") long size) {
        return ResultUtils.success(postService.listHotPost(boardId, size));
    }

    @GetMapping("/list/latest")
    public BaseResponse<List<PostVO>> listLatestPost(@RequestParam(required = false) Long boardId,
                                                     @RequestParam(required = false, defaultValue = "10") long size) {
        return ResultUtils.success(postService.listLatestPost(boardId, size));
    }
}
