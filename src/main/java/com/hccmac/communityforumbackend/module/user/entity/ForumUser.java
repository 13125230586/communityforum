package com.hccmac.communityforumbackend.module.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体
 */
@TableName(value = "forum_user")
@Data
public class ForumUser implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String userAccount;

    private String userPassword;

    private String userName;

    private String userAvatar;

    private String userProfile;

    private String userRole;

    private Integer userStatus;

    private String phone;

    private String email;

    private Date muteEndTime;

    private Date lastLoginTime;

    private String lastLoginIp;

    private Long postCount;

    private Long commentCount;

    private Long likeReceivedCount;

    private Date createTime;

    private Date updateTime;

    @TableLogic
    private Integer deleteFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
