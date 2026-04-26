package com.hccmac.communityforumbackend.module.system.init;

import com.hccmac.communityforumbackend.constant.SystemConfigConstant;
import com.hccmac.communityforumbackend.module.system.entity.ForumSystemConfig;
import com.hccmac.communityforumbackend.module.system.service.SystemConfigService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 系统配置初始化
 */
@Component
@Slf4j
public class SystemConfigInitRunner implements CommandLineRunner {

    @Resource
    private SystemConfigService systemConfigService;

    @Override
    public void run(String... args) {
        List<ConfigDefinition> defaultConfigList = buildDefaultConfigList();
        List<String> configKeyList = defaultConfigList.stream().map(ConfigDefinition::getConfigKey).collect(Collectors.toList());
        Set<String> existConfigKeySet = new HashSet<>(systemConfigService.lambdaQuery()
            .in(ForumSystemConfig::getConfigKey, configKeyList)
            .list()
            .stream()
            .map(ForumSystemConfig::getConfigKey)
            .collect(Collectors.toSet()));
        List<ForumSystemConfig> saveConfigList = defaultConfigList.stream()
            .filter(configDefinition -> !existConfigKeySet.contains(configDefinition.getConfigKey()))
            .map(this::buildSystemConfig)
            .collect(Collectors.toList());
        if (saveConfigList.isEmpty()) {
            return;
        }
        boolean result = systemConfigService.saveBatch(saveConfigList);
        if (result) {
            log.info("补齐系统配置 count:{}", saveConfigList.size());
        }
    }

    private List<ConfigDefinition> buildDefaultConfigList() {
        return Arrays.asList(
            new ConfigDefinition(SystemConfigConstant.CONFIG_GROUP_BASE, SystemConfigConstant.KEY_SITE_NAME, "站点名称",
                SystemConfigConstant.DEFAULT_SITE_NAME, "站点基础配置"),
            new ConfigDefinition(SystemConfigConstant.CONFIG_GROUP_BASE, SystemConfigConstant.KEY_SITE_NOTICE, "站点公告",
                SystemConfigConstant.DEFAULT_SITE_NOTICE, "首页公告正文"),
            new ConfigDefinition(SystemConfigConstant.CONFIG_GROUP_HOME, SystemConfigConstant.KEY_HOME_HERO_TITLE, "首页主标题",
                SystemConfigConstant.DEFAULT_HOME_HERO_TITLE, "首页顶部主标题"),
            new ConfigDefinition(SystemConfigConstant.CONFIG_GROUP_HOME, SystemConfigConstant.KEY_HOME_HERO_SUBTITLE, "首页副标题",
                SystemConfigConstant.DEFAULT_HOME_HERO_SUBTITLE, "首页顶部副标题"),
            new ConfigDefinition(SystemConfigConstant.CONFIG_GROUP_HOME, SystemConfigConstant.KEY_HOME_FEED_TITLE, "帖子区标题",
                SystemConfigConstant.DEFAULT_HOME_FEED_TITLE, "首页内容区标题"),
            new ConfigDefinition(SystemConfigConstant.CONFIG_GROUP_HOME, SystemConfigConstant.KEY_HOME_FEED_SUBTITLE, "帖子区说明",
                SystemConfigConstant.DEFAULT_HOME_FEED_SUBTITLE, "首页内容区说明"),
            new ConfigDefinition(SystemConfigConstant.CONFIG_GROUP_HOME, SystemConfigConstant.KEY_HOME_NOTICE_TITLE, "公告区标题",
                SystemConfigConstant.DEFAULT_HOME_NOTICE_TITLE, "首页公告区标题")
        );
    }

    private ForumSystemConfig buildSystemConfig(ConfigDefinition configDefinition) {
        ForumSystemConfig forumSystemConfig = new ForumSystemConfig();
        forumSystemConfig.setConfigGroup(configDefinition.getConfigGroup());
        forumSystemConfig.setConfigKey(configDefinition.getConfigKey());
        forumSystemConfig.setConfigName(configDefinition.getConfigName());
        forumSystemConfig.setConfigValue(configDefinition.getConfigValue());
        forumSystemConfig.setValueType(SystemConfigConstant.CONFIG_VALUE_TYPE_STRING);
        forumSystemConfig.setConfigStatus(SystemConfigConstant.CONFIG_STATUS_ENABLED);
        forumSystemConfig.setRemark(configDefinition.getRemark());
        forumSystemConfig.setUpdateUserId(SystemConfigConstant.DEFAULT_UPDATE_USER_ID);
        return forumSystemConfig;
    }

    /**
     * 配置定义
     */
    @Data
    @AllArgsConstructor
    private static class ConfigDefinition {

        private String configGroup;

        private String configKey;

        private String configName;

        private String configValue;

        private String remark;
    }
}
