package com.hccmac.communityforumbackend.module.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hccmac.communityforumbackend.module.system.entity.ForumOperationLog;
import com.hccmac.communityforumbackend.module.system.model.dto.OperationLogQueryReq;
import com.hccmac.communityforumbackend.module.system.model.vo.OperationLogVO;

/**
 * 操作日志服务
 */
public interface OperationLogService extends IService<ForumOperationLog> {

    /**
     * 保存操作日志
     *
     * @param forumOperationLog 操作日志实体
     */
    void saveOperationLog(ForumOperationLog forumOperationLog);

    /**
     * 分页查询操作日志
     *
     * @param operationLogQueryReq 查询参数
     * @return 分页结果
     */
    Page<OperationLogVO> listOperationLogByPage(OperationLogQueryReq operationLogQueryReq);
}
