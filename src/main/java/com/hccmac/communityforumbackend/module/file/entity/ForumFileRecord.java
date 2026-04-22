package com.hccmac.communityforumbackend.module.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 文件记录实体
 */
@TableName(value = "forum_file_record")
@Data
public class ForumFileRecord implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long uploaderUserId;
    private String uploadType;
    private String storageType;
    private String fileName;
    private String fileUrl;
    private String objectKey;
    private Long fileSize;
    private String fileSuffix;
    private String bizCode;
    private Integer fileStatus;
    private Date createTime;
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
