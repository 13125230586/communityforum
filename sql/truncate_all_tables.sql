USE communityforum;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE `forum_statistics_day`;
TRUNCATE TABLE `forum_operation_log`;
TRUNCATE TABLE `forum_file_record`;
TRUNCATE TABLE `forum_system_config`;
TRUNCATE TABLE `forum_report_record`;
TRUNCATE TABLE `forum_collect_record`;
TRUNCATE TABLE `forum_like_record`;
TRUNCATE TABLE `forum_comment`;
TRUNCATE TABLE `forum_post_tag_rel`;
TRUNCATE TABLE `forum_post_tag`;
TRUNCATE TABLE `forum_post_content`;
TRUNCATE TABLE `forum_post`;
TRUNCATE TABLE `forum_board`;
TRUNCATE TABLE `forum_board_category`;
TRUNCATE TABLE `forum_user`;
SET FOREIGN_KEY_CHECKS = 1;
