package com.hccmac.communityforumbackend.module.post.controller;

import com.hccmac.communityforumbackend.common.BaseResponse;
import com.hccmac.communityforumbackend.common.ResultUtils;
import com.hccmac.communityforumbackend.module.post.model.vo.PostTagVO;
import com.hccmac.communityforumbackend.module.post.service.PostTagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 标签接口
 */
@RestController
@RequestMapping("/post/tag")
public class PostTagController {

    @Resource
    private PostTagService postTagService;

    @GetMapping("/list")
    public BaseResponse<List<PostTagVO>> listEnableTag() {
        return ResultUtils.success(postTagService.listEnableTag());
    }
}
