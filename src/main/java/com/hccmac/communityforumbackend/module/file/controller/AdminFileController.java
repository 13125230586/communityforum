package com.hccmac.communityforumbackend.module.file.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hccmac.communityforumbackend.annotation.AuthCheck;
import com.hccmac.communityforumbackend.annotation.OperationLog;
import com.hccmac.communityforumbackend.common.BaseResponse;
import com.hccmac.communityforumbackend.common.DeleteRequest;
import com.hccmac.communityforumbackend.common.ResultUtils;
import com.hccmac.communityforumbackend.constant.OperationLogConstant;
import com.hccmac.communityforumbackend.constant.UserConstant;
import com.hccmac.communityforumbackend.module.file.model.dto.FileRecordQueryReq;
import com.hccmac.communityforumbackend.module.file.model.vo.FileRecordVO;
import com.hccmac.communityforumbackend.module.file.service.FileRecordService;
import com.hccmac.communityforumbackend.module.user.entity.ForumUser;
import com.hccmac.communityforumbackend.module.user.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 后台文件接口
 */
@RestController
@RequestMapping("/admin/file")
public class AdminFileController {

    @Resource
    private FileRecordService fileRecordService;

    @Resource
    private UserService userService;

    @GetMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<FileRecordVO>> listFileRecordByPage(FileRecordQueryReq fileRecordQueryReq) {
        return ResultUtils.success(fileRecordService.listFileRecordByPage(fileRecordQueryReq));
    }

    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @OperationLog(bizType = OperationLogConstant.BIZ_TYPE_FILE, actionType = "delete")
    public BaseResponse<Boolean> deleteFile(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        ForumUser loginUser = userService.getLoginUserEntity(request);
        return ResultUtils.success(fileRecordService.deleteFile(deleteRequest.getId(), loginUser, true));
    }
}
