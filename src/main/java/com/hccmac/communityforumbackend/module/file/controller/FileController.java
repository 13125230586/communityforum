package com.hccmac.communityforumbackend.module.file.controller;

import com.hccmac.communityforumbackend.annotation.AuthCheck;
import com.hccmac.communityforumbackend.common.BaseResponse;
import com.hccmac.communityforumbackend.common.DeleteRequest;
import com.hccmac.communityforumbackend.common.ResultUtils;
import com.hccmac.communityforumbackend.constant.FileConstant;
import com.hccmac.communityforumbackend.constant.UserConstant;
import com.hccmac.communityforumbackend.manager.upload.FileOssPictureUpload;
import com.hccmac.communityforumbackend.module.file.entity.ForumFileRecord;
import com.hccmac.communityforumbackend.module.file.model.vo.UploadFileVO;
import com.hccmac.communityforumbackend.module.file.service.FileRecordService;
import com.hccmac.communityforumbackend.module.user.entity.ForumUser;
import com.hccmac.communityforumbackend.module.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 文件上传接口
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private FileOssPictureUpload fileOssPictureUpload;

    @Resource
    private UserService userService;

    @Resource
    private FileRecordService fileRecordService;

    @PostMapping("/upload/avatar")
    public BaseResponse<UploadFileVO> uploadAvatar(@RequestPart("file") MultipartFile multipartFile, HttpServletRequest request) {
        ForumUser loginUser = userService.getLoginUserEntity(request);
        return ResultUtils.success(buildUploadFileVO(multipartFile, FileConstant.AVATAR_PATH + "/" + loginUser.getId(), FileConstant.UPLOAD_TYPE_AVATAR, loginUser));
    }

    @PostMapping("/upload/board")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<UploadFileVO> uploadBoard(@RequestPart("file") MultipartFile multipartFile, HttpServletRequest request) {
        ForumUser loginUser = userService.getLoginUserEntity(request);
        return ResultUtils.success(buildUploadFileVO(multipartFile, FileConstant.BOARD_PATH, FileConstant.UPLOAD_TYPE_BOARD, loginUser));
    }

    @PostMapping("/upload/postCover")
    public BaseResponse<UploadFileVO> uploadPostCover(@RequestPart("file") MultipartFile multipartFile, HttpServletRequest request) {
        ForumUser loginUser = userService.getLoginUserEntity(request);
        return ResultUtils.success(buildUploadFileVO(multipartFile, FileConstant.POST_COVER_PATH + "/" + loginUser.getId(), FileConstant.UPLOAD_TYPE_POST_COVER, loginUser));
    }

    @PostMapping("/upload/postContentImage")
    public BaseResponse<UploadFileVO> uploadPostContentImage(@RequestPart("file") MultipartFile multipartFile, HttpServletRequest request) {
        ForumUser loginUser = userService.getLoginUserEntity(request);
        return ResultUtils.success(buildUploadFileVO(multipartFile, FileConstant.POST_CONTENT_PATH + "/" + loginUser.getId(), FileConstant.UPLOAD_TYPE_POST_CONTENT_IMAGE, loginUser));
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteFile(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        ForumUser loginUser = userService.getLoginUserEntity(request);
        return ResultUtils.success(fileRecordService.deleteFile(deleteRequest.getId(), loginUser, false));
    }

    private UploadFileVO buildUploadFileVO(MultipartFile multipartFile, String uploadPathPrefix, String uploadType, ForumUser loginUser) {
        String fileUrl = fileOssPictureUpload.uploadPicture(multipartFile, uploadPathPrefix);
        ForumFileRecord forumFileRecord = fileRecordService.saveUploadRecord(multipartFile, fileUrl, uploadType, loginUser);
        UploadFileVO uploadFileVO = new UploadFileVO();
        uploadFileVO.setFileId(forumFileRecord.getId());
        uploadFileVO.setFileName(multipartFile.getOriginalFilename());
        uploadFileVO.setFileUrl(fileUrl);
        uploadFileVO.setFileSize(multipartFile.getSize());
        uploadFileVO.setUploadType(uploadType);
        log.info("文件上传 uid:{} fileId:{} fileUrl:{}", loginUser.getId(), forumFileRecord.getId(), fileUrl);
        return uploadFileVO;
    }
}
