package com.hccmac.communityforumbackend.module.file.service.impl;

import cn.hutool.core.io.FileUtil;
import org.springframework.beans.BeanUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hccmac.communityforumbackend.common.ErrorCode;
import com.hccmac.communityforumbackend.constant.FileConstant;
import com.hccmac.communityforumbackend.exception.ThrowUtils;
import com.hccmac.communityforumbackend.manager.OssManager;
import com.hccmac.communityforumbackend.module.file.entity.ForumFileRecord;
import com.hccmac.communityforumbackend.mapper.ForumFileRecordMapper;
import com.hccmac.communityforumbackend.module.file.model.dto.FileRecordQueryReq;
import com.hccmac.communityforumbackend.module.file.model.vo.FileRecordVO;
import com.hccmac.communityforumbackend.module.file.service.FileRecordService;
import com.hccmac.communityforumbackend.module.user.entity.ForumUser;
import com.hccmac.communityforumbackend.module.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.stream.Collectors;

/**
 * 文件记录服务实现
 */
@Service
@Slf4j
public class FileRecordServiceImpl extends ServiceImpl<ForumFileRecordMapper, ForumFileRecord> implements FileRecordService {

    private static final int FILE_STATUS_NORMAL = 0;
    private static final int FILE_STATUS_DELETED = 1;

    @Resource
    private UserService userService;

    @Resource
    private OssManager ossManager;

    @Override
    public ForumFileRecord saveUploadRecord(MultipartFile multipartFile, String fileUrl, String uploadType, ForumUser loginUser) {
        ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "文件不能为空");
        ThrowUtils.throwIf(StringUtils.isBlank(fileUrl), ErrorCode.PARAMS_ERROR, "文件地址不能为空");
        ThrowUtils.throwIf(loginUser == null || loginUser.getId() == null, ErrorCode.NOT_LOGIN_ERROR);
        ForumFileRecord forumFileRecord = new ForumFileRecord();
        forumFileRecord.setUploaderUserId(loginUser.getId());
        forumFileRecord.setUploadType(uploadType);
        forumFileRecord.setStorageType(FileConstant.STORAGE_TYPE_OSS);
        forumFileRecord.setFileName(multipartFile.getOriginalFilename());
        forumFileRecord.setFileUrl(fileUrl);
        forumFileRecord.setObjectKey(extractObjectKey(fileUrl));
        forumFileRecord.setFileSize(multipartFile.getSize());
        forumFileRecord.setFileSuffix(FileUtil.getSuffix(multipartFile.getOriginalFilename()));
        forumFileRecord.setFileStatus(FILE_STATUS_NORMAL);
        boolean result = this.save(forumFileRecord);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "保存文件记录失败");
        log.info("保存文件记录 uid:{} fileId:{} uploadType:{}", loginUser.getId(), forumFileRecord.getId(), uploadType);
        return forumFileRecord;
    }

    @Override
    public Page<FileRecordVO> listFileRecordByPage(FileRecordQueryReq fileRecordQueryReq) {
        ThrowUtils.throwIf(fileRecordQueryReq == null, ErrorCode.PARAMS_ERROR);
        QueryWrapper<ForumFileRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(fileRecordQueryReq.getUploaderUserId() != null, "uploaderUserId", fileRecordQueryReq.getUploaderUserId());
        queryWrapper.eq(StringUtils.isNotBlank(fileRecordQueryReq.getUploadType()), "uploadType", fileRecordQueryReq.getUploadType());
        queryWrapper.eq(fileRecordQueryReq.getFileStatus() != null, "fileStatus", fileRecordQueryReq.getFileStatus());
        queryWrapper.like(StringUtils.isNotBlank(fileRecordQueryReq.getFileName()), "fileName", fileRecordQueryReq.getFileName());
        queryWrapper.orderByDesc("createTime");
        Page<ForumFileRecord> fileRecordPage = this.page(new Page<>(fileRecordQueryReq.getCurrent(), fileRecordQueryReq.getPageSize()), queryWrapper);
        Page<FileRecordVO> fileRecordVOPage = new Page<>(fileRecordQueryReq.getCurrent(), fileRecordQueryReq.getPageSize(), fileRecordPage.getTotal());
        fileRecordVOPage.setRecords(fileRecordPage.getRecords().stream().map(this::toFileRecordVO).collect(Collectors.toList()));
        return fileRecordVOPage;
    }

    @Override
    public Boolean deleteFile(Long id, ForumUser loginUser, boolean adminDelete) {
        ThrowUtils.throwIf(id == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null || loginUser.getId() == null, ErrorCode.NOT_LOGIN_ERROR);
        ForumFileRecord forumFileRecord = this.getById(id);
        ThrowUtils.throwIf(forumFileRecord == null, ErrorCode.NOT_FOUND_ERROR, "文件记录不存在");
        if (!adminDelete) {
            ThrowUtils.throwIf(!loginUser.getId().equals(forumFileRecord.getUploaderUserId()), ErrorCode.NO_AUTH_ERROR, "无权限删除文件");
        }
        ThrowUtils.throwIf(FILE_STATUS_DELETED == safeInt(forumFileRecord.getFileStatus()), ErrorCode.OPERATION_ERROR, "文件已删除");
        if (StringUtils.isNotBlank(forumFileRecord.getObjectKey())) {
            ossManager.deleteObject(forumFileRecord.getObjectKey());
        }
        forumFileRecord.setFileStatus(FILE_STATUS_DELETED);
        boolean result = this.updateById(forumFileRecord);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "删除文件失败");
        log.info("删除文件 uid:{} fileId:{} adminDelete:{}", loginUser.getId(), id, adminDelete);
        return true;
    }

    private FileRecordVO toFileRecordVO(ForumFileRecord forumFileRecord) {
        FileRecordVO fileRecordVO = new FileRecordVO();
        BeanUtils.copyProperties(forumFileRecord, fileRecordVO);
        ForumUser forumUser = userService.getById(forumFileRecord.getUploaderUserId());
        if (forumUser != null) {
            fileRecordVO.setUploaderUserName(forumUser.getUserName());
        }
        return fileRecordVO;
    }

    private String extractObjectKey(String fileUrl) {
        if (StringUtils.isBlank(fileUrl)) {
            return null;
        }
        String noSchemaUrl = StringUtils.substringAfter(fileUrl, "//");
        return StringUtils.substringAfter(noSchemaUrl, "/");
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }
}
