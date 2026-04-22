package com.hccmac.communityforumbackend.module.file.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hccmac.communityforumbackend.module.file.entity.ForumFileRecord;
import com.hccmac.communityforumbackend.module.file.model.dto.FileRecordQueryReq;
import com.hccmac.communityforumbackend.module.file.model.vo.FileRecordVO;
import com.hccmac.communityforumbackend.module.user.entity.ForumUser;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件记录服务
 */
public interface FileRecordService extends IService<ForumFileRecord> {

    /**
     * 保存上传记录
     *
     * @param multipartFile 文件
     * @param fileUrl 文件地址
     * @param uploadType 上传类型
     * @param loginUser 登录用户
     * @return 文件记录
     */
    ForumFileRecord saveUploadRecord(MultipartFile multipartFile, String fileUrl, String uploadType, ForumUser loginUser);

    /**
     * 分页查询文件记录
     *
     * @param fileRecordQueryReq 查询参数
     * @return 分页结果
     */
    Page<FileRecordVO> listFileRecordByPage(FileRecordQueryReq fileRecordQueryReq);

    /**
     * 删除文件
     *
     * @param id 文件记录ID
     * @param loginUser 登录用户
     * @param adminDelete 是否管理员删除
     * @return 删除结果
     */
    Boolean deleteFile(Long id, ForumUser loginUser, boolean adminDelete);
}
