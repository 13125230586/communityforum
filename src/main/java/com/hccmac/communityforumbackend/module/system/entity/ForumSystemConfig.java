package com.hccmac.communityforumbackend.module.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统配置实体
 */
@TableName(value = "forum_system_config")
@Data
public class ForumSystemConfig implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String configGroup;
    private String configKey;
    private String configName;
    private String configValue;
    private String valueType;
    private Integer configStatus;
    private String remark;
    private Long updateUserId;
    private Date createTime;
    private Date updateTime;
    @TableLogic
    private Integer deleteFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
