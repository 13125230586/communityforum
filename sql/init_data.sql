USE communityforum;

SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO `forum_user` (
    `id`, `userAccount`, `userPassword`, `userName`, `userAvatar`, `userProfile`, `userRole`, `userStatus`,
    `phone`, `email`, `muteEndTime`, `lastLoginTime`, `lastLoginIp`, `postCount`, `commentCount`, `likeReceivedCount`
)
VALUES
    (1, 'admin', 'abd18a45308ba9e0c6ccfb8dbcf1aef2', '系统管理员', 'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/avatar/1/admin.png', '负责社区后台治理', 'admin', 0,
     '13800000001', 'admin@communityforum.com', NULL, '2026-04-21 20:00:00', '127.0.0.1', 0, 0, 0),
    (2, 'testadmin', 'abd18a45308ba9e0c6ccfb8dbcf1aef2', '测试管理员', 'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/avatar/2/testadmin.png', '用于联调后台接口', 'admin', 0,
     '13800000002', 'testadmin@communityforum.com', NULL, '2026-04-21 19:30:00', '127.0.0.1', 1, 1, 6),
    (3, 'testuser', 'abd18a45308ba9e0c6ccfb8dbcf1aef2', '测试用户', 'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/avatar/3/testuser.png', '用于联调前台主链路', 'user', 0,
     '13800000003', 'testuser@communityforum.com', NULL, '2026-04-21 19:45:00', '127.0.0.1', 2, 2, 9),
    (4, 'techguy', 'abd18a45308ba9e0c6ccfb8dbcf1aef2', '后端老哥', 'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/avatar/4/techguy.png', '主攻 Java 后端与中间件', 'user', 0,
     '13800000004', 'techguy@communityforum.com', NULL, '2026-04-20 21:00:00', '127.0.0.1', 1, 2, 4),
    (5, 'frontendgirl', 'abd18a45308ba9e0c6ccfb8dbcf1aef2', '前端同学', 'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/avatar/5/frontendgirl.png', '关注前端工程化与体验设计', 'user', 0,
     '13800000005', 'frontendgirl@communityforum.com', NULL, '2026-04-20 18:00:00', '127.0.0.1', 1, 1, 3),
    (6, 'lifeplayer', 'abd18a45308ba9e0c6ccfb8dbcf1aef2', '生活玩家', 'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/avatar/6/lifeplayer.png', '分享生活日常和效率工具', 'user', 0,
     '13800000006', 'lifeplayer@communityforum.com', NULL, '2026-04-19 22:10:00', '127.0.0.1', 1, 1, 2);

INSERT INTO `forum_board_category` (`id`, `categoryName`, `categoryIcon`, `sortOrder`, `categoryStatus`, `boardCount`)
VALUES
    (1, '技术交流', 'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/board/category/tech.png', 1, 0, 2),
    (2, '产品生活', 'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/board/category/life.png', 2, 0, 2),
    (3, '站务公告', 'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/board/category/notice.png', 3, 0, 1);

INSERT INTO `forum_board` (
    `id`, `categoryId`, `boardName`, `boardCode`, `boardIcon`, `boardDescription`, `sortOrder`, `boardStatus`,
    `postAuditFlag`, `commentAuditFlag`, `postCount`, `commentCount`, `followCount`, `createUserId`
)
VALUES
    (1, 1, 'Java 后端', 'java_backend', 'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/board/java_backend.png', 'Java 后端开发讨论区', 1, 0, 0, 0, 2, 3, 125, 1),
    (2, 1, '前端开发', 'frontend_dev', 'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/board/frontend_dev.png', '前端技术交流区', 2, 0, 0, 0, 2, 2, 98, 1),
    (3, 2, '日常闲聊', 'daily_chat', 'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/board/daily_chat.png', '轻松交流区', 1, 0, 0, 0, 1, 1, 66, 1),
    (4, 2, '产品反馈', 'product_feedback', 'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/board/product_feedback.png', '产品建议与反馈', 2, 0, 1, 1, 1, 0, 42, 1),
    (5, 3, '官方公告', 'official_notice', 'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/board/official_notice.png', '官方通知与版本发布', 1, 0, 0, 0, 1, 0, 15, 1);

INSERT INTO `forum_post_tag` (`id`, `tagName`, `sortOrder`, `tagStatus`, `useCount`)
VALUES
    (1, 'Spring Boot', 1, 0, 2),
    (2, 'MyBatis Plus', 2, 0, 2),
    (3, '前端实践', 3, 0, 2),
    (4, '日常分享', 4, 0, 1),
    (5, '产品建议', 5, 0, 1),
    (6, '站务通知', 6, 0, 1),
    (7, '待启用标签', 7, 1, 0);

INSERT INTO `forum_post` (
    `id`, `boardId`, `userId`, `postTitle`, `postSummary`, `coverImage`, `postSourceType`, `postStatus`, `auditStatus`,
    `anonymousFlag`, `topFlag`, `essenceFlag`, `commentAllowedFlag`, `viewCount`, `commentCount`, `likeCount`, `collectCount`,
    `reportCount`, `hotScore`, `lastCommentTime`, `publishTime`, `auditUserId`, `auditRemark`, `auditTime`, `editTime`, `createTime`
)
VALUES
    (1, 1, 3, 'Spring Boot 单体论坛后端应该怎么做分层设计', '分享一次社区论坛单体项目的后端分层设计思路',
     'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/post/cover/3/post1.png', 1, 1, 1,
     0, 1, 1, 1, 256, 2, 3, 2, 1, 520, '2026-04-21 18:30:00', '2026-04-21 09:00:00', 2, '初始化通过', '2026-04-21 09:10:00', '2026-04-21 11:00:00', '2026-04-21 08:55:00'),
    (2, 1, 4, 'MyBatis Plus 在内容系统里怎么优雅使用', '总结内容类系统里使用 MyBatis Plus 的注意点',
     'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/post/cover/4/post2.png', 1, 1, 1,
     0, 0, 0, 1, 189, 1, 2, 1, 0, 350, '2026-04-20 21:10:00', '2026-04-20 20:00:00', 2, '初始化通过', '2026-04-20 20:05:00', NULL, '2026-04-20 19:50:00'),
    (3, 2, 5, '前端项目里如何组织富文本帖子详情页', '分享帖子详情页渲染和样式组织经验',
     'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/post/cover/5/post3.png', 1, 1, 1,
     0, 0, 1, 1, 145, 1, 2, 1, 0, 300, '2026-04-20 18:35:00', '2026-04-20 18:00:00', 2, '初始化通过', '2026-04-20 18:10:00', NULL, '2026-04-20 17:50:00'),
    (4, 3, 6, '今天分享三个提升效率的小工具', '记录日常使用的效率工具和使用感受',
     'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/post/cover/6/post4.png', 1, 1, 1,
     0, 0, 0, 1, 88, 1, 1, 1, 0, 180, '2026-04-19 22:20:00', '2026-04-19 21:40:00', 2, '初始化通过', '2026-04-19 21:45:00', NULL, '2026-04-19 21:30:00'),
    (5, 4, 3, '建议增加帖子草稿箱能力', '社区内容创作场景里草稿箱是很有必要的能力',
     'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/post/cover/3/post5.png', 1, 1, 0,
     0, 0, 0, 1, 35, 0, 0, 0, 0, 35, NULL, NULL, NULL, NULL, NULL, NULL, '2026-04-21 20:20:00'),
    (6, 5, 2, '社区论坛系统已完成一期后端骨架搭建', '公告一期后端基础能力已经就绪 可开始联调',
     'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/post/cover/2/post6.png', 1, 1, 1,
     0, 1, 1, 1, 312, 0, 4, 2, 0, 460, NULL, '2026-04-21 10:30:00', 1, '初始化通过', '2026-04-21 10:35:00', NULL, '2026-04-21 10:20:00'),
    (7, 4, 5, '建议增加更多站内消息提醒方式', '希望在互动和审核通知上有更及时的站内提醒',
     'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/post/cover/5/post7.png', 1, 2, 2,
     0, 0, 0, 1, 12, 0, 0, 0, 0, 12, NULL, NULL, 2, '暂不采纳', '2026-04-21 12:00:00', NULL, '2026-04-21 11:20:00');

INSERT INTO `forum_post_content` (`id`, `postId`, `contentType`, `content`, `contentText`, `wordCount`)
VALUES
    (1, 1, 'markdown', '# Spring Boot 单体论坛后端\n\n可以按用户 板块 帖子 评论 统计 文件等业务域拆分', 'Spring Boot 单体论坛后端 可以按用户 板块 帖子 评论 统计 文件等业务域拆分', 43),
    (2, 2, 'markdown', '# MyBatis Plus 使用心得\n\n内容系统更适合配合 QueryWrapper 和分页插件使用', 'MyBatis Plus 使用心得 内容系统更适合配合 QueryWrapper 和分页插件使用', 40),
    (3, 3, 'markdown', '# 前端帖子详情页\n\n重点关注富文本渲染 图片展示和目录结构', '前端帖子详情页 重点关注富文本渲染 图片展示和目录结构', 31),
    (4, 4, 'markdown', '# 效率工具分享\n\n今天推荐三个我在工作和生活里常用的小工具', '效率工具分享 今天推荐三个我在工作和生活里常用的小工具', 31),
    (5, 5, 'markdown', '# 草稿箱建议\n\n移动端和 Web 端都需要支持草稿暂存', '草稿箱建议 移动端和 Web 端都需要支持草稿暂存', 26),
    (6, 6, 'markdown', '# 官方公告\n\n社区论坛系统后端骨架已完成 欢迎前端联调', '官方公告 社区论坛系统后端骨架已完成 欢迎前端联调', 28),
    (7, 7, 'markdown', '# 消息提醒建议\n\n希望增加更丰富的消息提醒方式', '消息提醒建议 希望增加更丰富的消息提醒方式', 23);

INSERT INTO `forum_post_tag_rel` (`id`, `postId`, `tagId`)
VALUES
    (1, 1, 1),
    (2, 1, 2),
    (3, 2, 2),
    (4, 2, 1),
    (5, 3, 3),
    (6, 4, 4),
    (7, 5, 5),
    (8, 6, 6),
    (9, 3, 5);

INSERT INTO `forum_comment` (
    `id`, `postId`, `userId`, `rootCommentId`, `parentCommentId`, `replyUserId`, `contentType`, `content`,
    `commentStatus`, `auditStatus`, `anonymousFlag`, `likeCount`, `childCount`, `createTime`
)
VALUES
    (1, 1, 4, 0, 0, NULL, 'text', '这个分层思路很清晰 适合单体项目快速落地', 0, 1, 0, 2, 1, '2026-04-21 18:10:00'),
    (2, 1, 3, 1, 1, 4, 'text', '是的 先把主链路跑通最重要', 0, 1, 0, 1, 0, '2026-04-21 18:30:00'),
    (3, 2, 3, 0, 0, NULL, 'text', '分页和逻辑删除在内容系统里确实很好用', 0, 1, 0, 1, 0, '2026-04-20 21:10:00'),
    (4, 3, 4, 0, 0, NULL, 'text', '详情页目录和代码高亮这两个点也很关键', 0, 1, 0, 1, 0, '2026-04-20 18:35:00'),
    (5, 4, 5, 0, 0, NULL, 'text', '我也在用其中两个 工具确实不错', 0, 1, 0, 0, 0, '2026-04-19 22:20:00'),
    (6, 5, 2, 0, 0, NULL, 'text', '这个建议已收到 会先评估优先级', 0, 0, 0, 0, 0, '2026-04-21 20:40:00');

INSERT INTO `forum_like_record` (`id`, `bizType`, `bizId`, `userId`, `createTime`)
VALUES
    (1, 'post', 1, 4, '2026-04-21 18:40:00'),
    (2, 'post', 1, 5, '2026-04-21 18:42:00'),
    (3, 'post', 1, 6, '2026-04-21 18:45:00'),
    (4, 'post', 2, 3, '2026-04-20 21:15:00'),
    (5, 'post', 2, 5, '2026-04-20 21:20:00'),
    (6, 'post', 3, 3, '2026-04-20 18:40:00'),
    (7, 'post', 3, 4, '2026-04-20 18:42:00'),
    (8, 'post', 4, 3, '2026-04-19 22:30:00'),
    (9, 'post', 6, 3, '2026-04-21 11:00:00'),
    (10, 'post', 6, 4, '2026-04-21 11:02:00'),
    (11, 'post', 6, 5, '2026-04-21 11:05:00'),
    (12, 'post', 6, 6, '2026-04-21 11:08:00'),
    (13, 'comment', 1, 3, '2026-04-21 18:50:00'),
    (14, 'comment', 1, 5, '2026-04-21 18:51:00'),
    (15, 'comment', 2, 4, '2026-04-21 18:55:00'),
    (16, 'comment', 3, 4, '2026-04-20 21:18:00'),
    (17, 'comment', 4, 5, '2026-04-20 18:45:00');

INSERT INTO `forum_collect_record` (`id`, `bizType`, `bizId`, `userId`, `createTime`)
VALUES
    (1, 'post', 1, 4, '2026-04-21 18:48:00'),
    (2, 'post', 1, 5, '2026-04-21 18:49:00'),
    (3, 'post', 2, 3, '2026-04-20 21:25:00'),
    (4, 'post', 3, 4, '2026-04-20 18:50:00'),
    (5, 'post', 4, 3, '2026-04-19 22:35:00'),
    (6, 'post', 6, 3, '2026-04-21 11:10:00');

INSERT INTO `forum_report_record` (
    `id`, `bizType`, `bizId`, `reportUserId`, `reportType`, `reportReason`, `processStatus`, `processUserId`, `processRemark`, `processTime`, `createTime`
)
VALUES
    (1, 'post', 1, 5, 'spam', '怀疑存在营销内容 先提一个测试举报', 0, NULL, NULL, NULL, '2026-04-21 19:00:00'),
    (2, 'comment', 5, 3, 'abuse', '评论内容不太友好 需要人工确认', 1, 2, '已核查 维持展示', '2026-04-21 19:10:00', '2026-04-21 19:05:00');

INSERT INTO `forum_system_config` (`id`, `configGroup`, `configKey`, `configName`, `configValue`, `valueType`, `configStatus`, `remark`, `updateUserId`)
VALUES
    (1, 'base', 'siteName', '站点名称', 'Communityforum', 'string', 0, '站点基础配置', 1),
    (2, 'base', 'siteNotice', '站点公告', '欢迎来到 Communityforum 社区论坛系统', 'string', 0, '首页顶部公告', 1),
    (3, 'post', 'postAuditDefault', '默认发帖审核', '0', 'number', 0, '默认无需审核', 1),
    (4, 'comment', 'commentAuditDefault', '默认评论审核', '0', 'number', 0, '默认无需审核', 1),
    (5, 'upload', 'imageMaxSizeMb', '图片最大大小', '10', 'number', 0, '单位 MB', 1),
    (6, 'content', 'hotPostSize', '热门帖子展示数量', '10', 'number', 0, '首页展示热门帖子数量', 1);

INSERT INTO `forum_file_record` (
    `id`, `uploaderUserId`, `uploadType`, `storageType`, `fileName`, `fileUrl`, `objectKey`, `fileSize`, `fileSuffix`, `bizCode`, `fileStatus`, `createTime`
)
VALUES
    (1, 3, 'avatar', 'oss', 'testuser.png', 'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/avatar/3/testuser.png', 'avatar/3/testuser.png', 20480, 'png', 'userAvatar', 0, '2026-04-21 09:30:00'),
    (2, 4, 'avatar', 'oss', 'techguy.png', 'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/avatar/4/techguy.png', 'avatar/4/techguy.png', 21800, 'png', 'userAvatar', 0, '2026-04-20 20:10:00'),
    (3, 5, 'avatar', 'oss', 'frontendgirl.png', 'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/avatar/5/frontendgirl.png', 'avatar/5/frontendgirl.png', 22500, 'png', 'userAvatar', 0, '2026-04-20 17:30:00'),
    (4, 3, 'postCover', 'oss', 'post1.png', 'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/post/cover/3/post1.png', 'post/cover/3/post1.png', 38900, 'png', 'postCover', 0, '2026-04-21 08:50:00'),
    (5, 4, 'postCover', 'oss', 'post2.png', 'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/post/cover/4/post2.png', 'post/cover/4/post2.png', 40100, 'png', 'postCover', 0, '2026-04-20 19:40:00'),
    (6, 5, 'postCover', 'oss', 'post3.png', 'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/post/cover/5/post3.png', 'post/cover/5/post3.png', 41500, 'png', 'postCover', 0, '2026-04-20 17:40:00'),
    (7, 6, 'postCover', 'oss', 'post4.png', 'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/post/cover/6/post4.png', 'post/cover/6/post4.png', 32200, 'png', 'postCover', 0, '2026-04-19 21:20:00'),
    (8, 2, 'postCover', 'oss', 'post6.png', 'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/post/cover/2/post6.png', 'post/cover/2/post6.png', 29800, 'png', 'postCover', 0, '2026-04-21 10:10:00'),
    (9, 3, 'postContentImage', 'oss', 'content1.png', 'https://communityforum-backendd.oss-cn-hangzhou.aliyuncs.com/post/content/3/content1.png', 'post/content/3/content1.png', 28700, 'png', 'postContent', 0, '2026-04-21 08:52:00');

INSERT INTO `forum_operation_log` (
    `id`, `operatorUserId`, `operatorRole`, `bizType`, `bizId`, `actionType`, `requestPath`, `requestParam`, `resultCode`, `resultMessage`, `operateIp`, `createTime`
)
VALUES
    (1, 1, 'admin', 'boardCategory', 1, 'add', '/api/admin/boardCategory/add', '{"categoryName":"技术交流"}', 0, 'ok', '127.0.0.1', '2026-04-18 10:00:00'),
    (2, 1, 'admin', 'board', 1, 'add', '/api/admin/board/add', '{"boardName":"Java 后端"}', 0, 'ok', '127.0.0.1', '2026-04-18 10:10:00'),
    (3, 2, 'admin', 'postTag', 1, 'add', '/api/admin/post/tag/add', '{"tagName":"Spring Boot"}', 0, 'ok', '127.0.0.1', '2026-04-19 09:00:00'),
    (4, 2, 'admin', 'post', 1, 'audit', '/api/admin/post/audit', '{"id":1,"auditStatus":1}', 0, 'ok', '127.0.0.1', '2026-04-21 09:10:00'),
    (5, 2, 'admin', 'comment', 1, 'audit', '/api/admin/comment/audit', '{"id":1,"auditStatus":1}', 0, 'ok', '127.0.0.1', '2026-04-21 18:15:00'),
    (6, 2, 'admin', 'report', 2, 'process', '/api/admin/report/process', '{"id":2,"processStatus":1}', 0, 'ok', '127.0.0.1', '2026-04-21 19:10:00'),
    (7, 2, 'admin', 'systemConfig', 1, 'update', '/api/admin/system/config/update', '{"id":1,"configValue":"Communityforum"}', 0, 'ok', '127.0.0.1', '2026-04-21 19:30:00');

INSERT INTO `forum_statistics_day` (
    `id`, `statDate`, `newUserCount`, `activeUserCount`, `newPostCount`, `publishPostCount`, `newCommentCount`, `newLikeCount`, `newCollectCount`, `reportCount`
)
VALUES
    (1, '2026-04-15 00:00:00', 3, 9, 6, 5, 10, 12, 4, 1),
    (2, '2026-04-16 00:00:00', 5, 12, 8, 7, 13, 16, 6, 0),
    (3, '2026-04-17 00:00:00', 4, 10, 7, 7, 9, 11, 5, 2),
    (4, '2026-04-18 00:00:00', 6, 15, 10, 9, 15, 18, 8, 1),
    (5, '2026-04-19 00:00:00', 2, 8, 5, 4, 8, 7, 2, 0),
    (6, '2026-04-20 00:00:00', 7, 18, 12, 11, 17, 22, 9, 1),
    (7, '2026-04-21 00:00:00', 4, 13, 9, 8, 11, 14, 6, 1);

SET FOREIGN_KEY_CHECKS = 1;
