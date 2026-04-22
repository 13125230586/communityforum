package com.hccmac.communityforumbackend.module.post.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hccmac.communityforumbackend.annotation.AuthCheck;
import com.hccmac.communityforumbackend.annotation.OperationLog;
import com.hccmac.communityforumbackend.common.BaseResponse;
import com.hccmac.communityforumbackend.common.DeleteRequest;
import com.hccmac.communityforumbackend.common.ResultUtils;
import com.hccmac.communityforumbackend.constant.OperationLogConstant;
import com.hccmac.communityforumbackend.constant.UserConstant;
import com.hccmac.communityforumbackend.module.post.model.dto.PostTagAddReq;
import com.hccmac.communityforumbackend.module.post.model.dto.PostTagQueryReq;
import com.hccmac.communityforumbackend.module.post.model.dto.PostTagUpdateReq;
import com.hccmac.communityforumbackend.module.post.model.vo.PostTagVO;
import com.hccmac.communityforumbackend.module.post.service.PostTagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 后台标签接口
 */
@RestController
@RequestMapping("/admin/post/tag")
public class AdminPostTagController {

    @Resource
    private PostTagService postTagService;

    @GetMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<PostTagVO>> listPostTagByPage(PostTagQueryReq postTagQueryReq) {
        return ResultUtils.success(postTagService.listPostTagByPage(postTagQueryReq));
    }

    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @OperationLog(bizType = OperationLogConstant.BIZ_TYPE_POST_TAG, actionType = "add")
    public BaseResponse<Long> addPostTag(@RequestBody PostTagAddReq postTagAddReq) {
        return ResultUtils.success(postTagService.addPostTag(postTagAddReq));
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @OperationLog(bizType = OperationLogConstant.BIZ_TYPE_POST_TAG, actionType = "update")
    public BaseResponse<Boolean> updatePostTag(@RequestBody PostTagUpdateReq postTagUpdateReq) {
        return ResultUtils.success(postTagService.updatePostTag(postTagUpdateReq));
    }

    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @OperationLog(bizType = OperationLogConstant.BIZ_TYPE_POST_TAG, actionType = "delete")
    public BaseResponse<Boolean> deletePostTag(@RequestBody DeleteRequest deleteRequest) {
        return ResultUtils.success(postTagService.deletePostTag(deleteRequest.getId()));
    }
}
