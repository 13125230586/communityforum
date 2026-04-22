CREATE DATABASE IF NOT EXISTS communityforum DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE communityforum;

CREATE TABLE IF NOT EXISTS `forum_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `userAccount` VARCHAR(128) NOT NULL COMMENT '用户账号',
    `userPassword` VARCHAR(256) NOT NULL COMMENT '用户密码',
    `userName` VARCHAR(128) NOT NULL COMMENT '用户昵称',
    `userAvatar` VARCHAR(512) DEFAULT NULL COMMENT '用户头像',
    `userProfile` VARCHAR(512) DEFAULT NULL COMMENT '用户简介',
    `userRole` VARCHAR(32) NOT NULL COMMENT '用户角色',
    `userStatus` TINYINT NOT NULL DEFAULT 0 COMMENT '用户状态',
    `phone` VARCHAR(32) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    `muteEndTime` DATETIME DEFAULT NULL COMMENT '禁言截止时间',
    `lastLoginTime` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `lastLoginIp` VARCHAR(64) DEFAULT NULL COMMENT '最后登录IP',
    `postCount` BIGINT NOT NULL DEFAULT 0 COMMENT '发帖数量',
    `commentCount` BIGINT NOT NULL DEFAULT 0 COMMENT '评论数量',
    `likeReceivedCount` BIGINT NOT NULL DEFAULT 0 COMMENT '获赞数量',
    `createTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleteFlag` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_userAccount` (`userAccount`),
    UNIQUE KEY `uk_email` (`email`),
    KEY `idx_userRole` (`userRole`),
    KEY `idx_userStatus` (`userStatus`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS `forum_board_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `categoryName` VARCHAR(128) NOT NULL COMMENT '分类名称',
    `categoryIcon` VARCHAR(512) DEFAULT NULL COMMENT '分类图标',
    `sortOrder` INT NOT NULL DEFAULT 0 COMMENT '排序值',
    `categoryStatus` TINYINT NOT NULL DEFAULT 0 COMMENT '分类状态',
    `boardCount` BIGINT NOT NULL DEFAULT 0 COMMENT '板块数量',
    `createTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleteFlag` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_categoryStatus_sortOrder` (`categoryStatus`, `sortOrder`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='板块分类表';

CREATE TABLE IF NOT EXISTS `forum_board` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `categoryId` BIGINT NOT NULL COMMENT '分类ID',
    `boardName` VARCHAR(128) NOT NULL COMMENT '板块名称',
    `boardCode` VARCHAR(64) NOT NULL COMMENT '板块编码',
    `boardIcon` VARCHAR(512) DEFAULT NULL COMMENT '板块图标',
    `boardDescription` VARCHAR(512) DEFAULT NULL COMMENT '板块描述',
    `sortOrder` INT NOT NULL DEFAULT 0 COMMENT '排序值',
    `boardStatus` TINYINT NOT NULL DEFAULT 0 COMMENT '板块状态',
    `postAuditFlag` TINYINT NOT NULL DEFAULT 0 COMMENT '发帖审核标记',
    `commentAuditFlag` TINYINT NOT NULL DEFAULT 0 COMMENT '评论审核标记',
    `postCount` BIGINT NOT NULL DEFAULT 0 COMMENT '帖子数量',
    `commentCount` BIGINT NOT NULL DEFAULT 0 COMMENT '评论数量',
    `followCount` BIGINT NOT NULL DEFAULT 0 COMMENT '关注数量',
    `createUserId` BIGINT NOT NULL DEFAULT 0 COMMENT '创建人ID',
    `createTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleteFlag` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_boardCode` (`boardCode`),
    KEY `idx_categoryId_sortOrder` (`categoryId`, `sortOrder`),
    KEY `idx_boardStatus` (`boardStatus`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='板块表';

CREATE TABLE IF NOT EXISTS `forum_post` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `boardId` BIGINT NOT NULL COMMENT '板块ID',
    `userId` BIGINT NOT NULL COMMENT '用户ID',
    `postTitle` VARCHAR(256) NOT NULL COMMENT '帖子标题',
    `postSummary` VARCHAR(512) DEFAULT NULL COMMENT '帖子摘要',
    `coverImage` VARCHAR(512) DEFAULT NULL COMMENT '封面图',
    `postSourceType` TINYINT NOT NULL DEFAULT 1 COMMENT '帖子来源类型',
    `postStatus` TINYINT NOT NULL DEFAULT 0 COMMENT '帖子状态',
    `auditStatus` TINYINT NOT NULL DEFAULT 0 COMMENT '审核状态',
    `anonymousFlag` TINYINT NOT NULL DEFAULT 0 COMMENT '匿名标记',
    `topFlag` TINYINT NOT NULL DEFAULT 0 COMMENT '置顶标记',
    `essenceFlag` TINYINT NOT NULL DEFAULT 0 COMMENT '精华标记',
    `commentAllowedFlag` TINYINT NOT NULL DEFAULT 1 COMMENT '允许评论标记',
    `viewCount` BIGINT NOT NULL DEFAULT 0 COMMENT '浏览数量',
    `commentCount` BIGINT NOT NULL DEFAULT 0 COMMENT '评论数量',
    `likeCount` BIGINT NOT NULL DEFAULT 0 COMMENT '点赞数量',
    `collectCount` BIGINT NOT NULL DEFAULT 0 COMMENT '收藏数量',
    `reportCount` BIGINT NOT NULL DEFAULT 0 COMMENT '举报数量',
    `hotScore` BIGINT NOT NULL DEFAULT 0 COMMENT '热度分值',
    `lastCommentTime` DATETIME DEFAULT NULL COMMENT '最后评论时间',
    `publishTime` DATETIME DEFAULT NULL COMMENT '发布时间',
    `auditUserId` BIGINT DEFAULT NULL COMMENT '审核人ID',
    `auditRemark` VARCHAR(512) DEFAULT NULL COMMENT '审核备注',
    `auditTime` DATETIME DEFAULT NULL COMMENT '审核时间',
    `editTime` DATETIME DEFAULT NULL COMMENT '编辑时间',
    `createTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleteFlag` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_boardId_postStatus_publishTime` (`boardId`, `postStatus`, `publishTime`),
    KEY `idx_boardId_topFlag_publishTime` (`boardId`, `topFlag`, `publishTime`),
    KEY `idx_userId_postStatus_createTime` (`userId`, `postStatus`, `createTime`),
    KEY `idx_auditStatus_createTime` (`auditStatus`, `createTime`),
    KEY `idx_hotScore` (`hotScore`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子表';

CREATE TABLE IF NOT EXISTS `forum_post_content` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `postId` BIGINT NOT NULL COMMENT '帖子ID',
    `contentType` VARCHAR(32) NOT NULL COMMENT '内容类型',
    `content` LONGTEXT NOT NULL COMMENT '正文内容',
    `contentText` LONGTEXT DEFAULT NULL COMMENT '纯文本内容',
    `wordCount` INT NOT NULL DEFAULT 0 COMMENT '字数',
    `createTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_postId` (`postId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子内容表';

CREATE TABLE IF NOT EXISTS `forum_post_tag` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `tagName` VARCHAR(64) NOT NULL COMMENT '标签名称',
    `sortOrder` INT NOT NULL DEFAULT 0 COMMENT '排序值',
    `tagStatus` TINYINT NOT NULL DEFAULT 0 COMMENT '标签状态',
    `useCount` BIGINT NOT NULL DEFAULT 0 COMMENT '使用次数',
    `createTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleteFlag` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tagName` (`tagName`),
    KEY `idx_tagStatus_sortOrder` (`tagStatus`, `sortOrder`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子标签表';

CREATE TABLE IF NOT EXISTS `forum_post_tag_rel` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `postId` BIGINT NOT NULL COMMENT '帖子ID',
    `tagId` BIGINT NOT NULL COMMENT '标签ID',
    `createTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_postId_tagId` (`postId`, `tagId`),
    KEY `idx_tagId` (`tagId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子标签关系表';

CREATE TABLE IF NOT EXISTS `forum_comment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `postId` BIGINT NOT NULL COMMENT '帖子ID',
    `userId` BIGINT NOT NULL COMMENT '用户ID',
    `rootCommentId` BIGINT NOT NULL DEFAULT 0 COMMENT '根评论ID',
    `parentCommentId` BIGINT NOT NULL DEFAULT 0 COMMENT '父评论ID',
    `replyUserId` BIGINT DEFAULT NULL COMMENT '被回复用户ID',
    `contentType` VARCHAR(32) NOT NULL COMMENT '内容类型',
    `content` VARCHAR(2000) NOT NULL COMMENT '评论内容',
    `commentStatus` TINYINT NOT NULL DEFAULT 0 COMMENT '评论状态',
    `auditStatus` TINYINT NOT NULL DEFAULT 0 COMMENT '审核状态',
    `anonymousFlag` TINYINT NOT NULL DEFAULT 0 COMMENT '匿名标记',
    `likeCount` BIGINT NOT NULL DEFAULT 0 COMMENT '点赞数量',
    `childCount` BIGINT NOT NULL DEFAULT 0 COMMENT '子评论数量',
    `createTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleteFlag` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_postId_rootCommentId_createTime` (`postId`, `rootCommentId`, `createTime`),
    KEY `idx_parentCommentId` (`parentCommentId`),
    KEY `idx_userId_createTime` (`userId`, `createTime`),
    KEY `idx_auditStatus_createTime` (`auditStatus`, `createTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

CREATE TABLE IF NOT EXISTS `forum_like_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `bizType` VARCHAR(32) NOT NULL COMMENT '业务类型',
    `bizId` BIGINT NOT NULL COMMENT '业务ID',
    `userId` BIGINT NOT NULL COMMENT '用户ID',
    `createTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_userId_bizType_bizId` (`userId`, `bizType`, `bizId`),
    KEY `idx_bizType_bizId` (`bizType`, `bizId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞记录表';

CREATE TABLE IF NOT EXISTS `forum_collect_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `bizType` VARCHAR(32) NOT NULL COMMENT '业务类型',
    `bizId` BIGINT NOT NULL COMMENT '业务ID',
    `userId` BIGINT NOT NULL COMMENT '用户ID',
    `createTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_collect_userId_bizType_bizId` (`userId`, `bizType`, `bizId`),
    KEY `idx_collect_bizType_bizId` (`bizType`, `bizId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏记录表';

CREATE TABLE IF NOT EXISTS `forum_report_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `bizType` VARCHAR(32) NOT NULL COMMENT '业务类型',
    `bizId` BIGINT NOT NULL COMMENT '业务ID',
    `reportUserId` BIGINT NOT NULL COMMENT '举报用户ID',
    `reportType` VARCHAR(64) NOT NULL COMMENT '举报类型',
    `reportReason` VARCHAR(512) DEFAULT NULL COMMENT '举报原因',
    `processStatus` TINYINT NOT NULL DEFAULT 0 COMMENT '处理状态',
    `processUserId` BIGINT DEFAULT NULL COMMENT '处理人ID',
    `processRemark` VARCHAR(512) DEFAULT NULL COMMENT '处理备注',
    `processTime` DATETIME DEFAULT NULL COMMENT '处理时间',
    `createTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_bizType_bizId` (`bizType`, `bizId`),
    KEY `idx_processStatus_createTime` (`processStatus`, `createTime`),
    KEY `idx_reportUserId` (`reportUserId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='举报记录表';

CREATE TABLE IF NOT EXISTS `forum_system_config` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `configGroup` VARCHAR(64) NOT NULL COMMENT '配置分组',
    `configKey` VARCHAR(64) NOT NULL COMMENT '配置键',
    `configName` VARCHAR(128) NOT NULL COMMENT '配置名称',
    `configValue` VARCHAR(2000) DEFAULT NULL COMMENT '配置值',
    `valueType` VARCHAR(32) NOT NULL COMMENT '值类型',
    `configStatus` TINYINT NOT NULL DEFAULT 0 COMMENT '配置状态',
    `remark` VARCHAR(512) DEFAULT NULL COMMENT '备注',
    `updateUserId` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `createTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleteFlag` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_configGroup_configKey` (`configGroup`, `configKey`),
    KEY `idx_configGroup` (`configGroup`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

CREATE TABLE IF NOT EXISTS `forum_file_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `uploaderUserId` BIGINT NOT NULL COMMENT '上传用户ID',
    `uploadType` VARCHAR(64) NOT NULL COMMENT '上传类型',
    `storageType` VARCHAR(32) NOT NULL COMMENT '存储类型',
    `fileName` VARCHAR(256) NOT NULL COMMENT '文件名称',
    `fileUrl` VARCHAR(1024) NOT NULL COMMENT '文件地址',
    `objectKey` VARCHAR(512) DEFAULT NULL COMMENT 'OSS对象Key',
    `fileSize` BIGINT NOT NULL DEFAULT 0 COMMENT '文件大小',
    `fileSuffix` VARCHAR(32) DEFAULT NULL COMMENT '文件后缀',
    `bizCode` VARCHAR(64) DEFAULT NULL COMMENT '业务标识',
    `fileStatus` TINYINT NOT NULL DEFAULT 0 COMMENT '文件状态',
    `createTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_uploaderUserId_createTime` (`uploaderUserId`, `createTime`),
    KEY `idx_uploadType_fileStatus` (`uploadType`, `fileStatus`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件记录表';

CREATE TABLE IF NOT EXISTS `forum_operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `operatorUserId` BIGINT NOT NULL COMMENT '操作人ID',
    `operatorRole` VARCHAR(32) NOT NULL COMMENT '操作人角色',
    `bizType` VARCHAR(64) DEFAULT NULL COMMENT '业务类型',
    `bizId` BIGINT DEFAULT NULL COMMENT '业务ID',
    `actionType` VARCHAR(64) NOT NULL COMMENT '操作类型',
    `requestPath` VARCHAR(256) DEFAULT NULL COMMENT '请求路径',
    `requestParam` TEXT DEFAULT NULL COMMENT '请求参数',
    `resultCode` INT NOT NULL DEFAULT 0 COMMENT '结果码',
    `resultMessage` VARCHAR(512) DEFAULT NULL COMMENT '结果信息',
    `operateIp` VARCHAR(64) DEFAULT NULL COMMENT '操作IP',
    `createTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_operatorUserId_createTime` (`operatorUserId`, `createTime`),
    KEY `idx_bizType_bizId` (`bizType`, `bizId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

CREATE TABLE IF NOT EXISTS `forum_statistics_day` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `statDate` DATETIME NOT NULL COMMENT '统计日期',
    `newUserCount` BIGINT NOT NULL DEFAULT 0 COMMENT '新增用户数',
    `activeUserCount` BIGINT NOT NULL DEFAULT 0 COMMENT '活跃用户数',
    `newPostCount` BIGINT NOT NULL DEFAULT 0 COMMENT '新增帖子数',
    `publishPostCount` BIGINT NOT NULL DEFAULT 0 COMMENT '发布帖子数',
    `newCommentCount` BIGINT NOT NULL DEFAULT 0 COMMENT '新增评论数',
    `newLikeCount` BIGINT NOT NULL DEFAULT 0 COMMENT '新增点赞数',
    `newCollectCount` BIGINT NOT NULL DEFAULT 0 COMMENT '新增收藏数',
    `reportCount` BIGINT NOT NULL DEFAULT 0 COMMENT '举报数量',
    `createTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_statDate` (`statDate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日统计表';
