package com.hccmac.communityforumbackend.module.system.service.impl;

import org.springframework.beans.BeanUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hccmac.communityforumbackend.common.ErrorCode;
import com.hccmac.communityforumbackend.exception.ThrowUtils;
import com.hccmac.communityforumbackend.module.system.entity.ForumOperationLog;
import com.hccmac.communityforumbackend.mapper.ForumOperationLogMapper;
import com.hccmac.communityforumbackend.module.system.model.dto.OperationLogQueryReq;
import com.hccmac.communityforumbackend.module.system.model.vo.OperationLogVO;
import com.hccmac.communityforumbackend.module.system.service.OperationLogService;
import com.hccmac.communityforumbackend.module.user.entity.ForumUser;
import com.hccmac.communityforumbackend.module.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.stream.Collectors;

/**
 * 操作日志服务实现
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<ForumOperationLogMapper, ForumOperationLog> implements OperationLogService {

    @Resource
    private UserService userService;

    @Override
    public void saveOperationLog(ForumOperationLog forumOperationLog) {
        if (forumOperationLog == null) {
            return;
        }
        this.save(forumOperationLog);
    }

    @Override
    public Page<OperationLogVO> listOperationLogByPage(OperationLogQueryReq operationLogQueryReq) {
        ThrowUtils.throwIf(operationLogQueryReq == null, ErrorCode.PARAMS_ERROR);
        QueryWrapper<ForumOperationLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(operationLogQueryReq.getOperatorUserId() != null, "operatorUserId", operationLogQueryReq.getOperatorUserId());
        queryWrapper.eq(StringUtils.isNotBlank(operationLogQueryReq.getBizType()), "bizType", operationLogQueryReq.getBizType());
        queryWrapper.eq(StringUtils.isNotBlank(operationLogQueryReq.getActionType()), "actionType", operationLogQueryReq.getActionType());
        queryWrapper.eq(operationLogQueryReq.getResultCode() != null, "resultCode", operationLogQueryReq.getResultCode());
        queryWrapper.orderByDesc("createTime");
        Page<ForumOperationLog> logPage = this.page(new Page<>(operationLogQueryReq.getCurrent(), operationLogQueryReq.getPageSize()), queryWrapper);
        Page<OperationLogVO> operationLogVOPage = new Page<>(operationLogQueryReq.getCurrent(), operationLogQueryReq.getPageSize(), logPage.getTotal());
        operationLogVOPage.setRecords(logPage.getRecords().stream().map(this::toOperationLogVO).collect(Collectors.toList()));
        return operationLogVOPage;
    }

    private OperationLogVO toOperationLogVO(ForumOperationLog forumOperationLog) {
        OperationLogVO operationLogVO = new OperationLogVO();
        BeanUtils.copyProperties(forumOperationLog, operationLogVO);
        ForumUser forumUser = userService.getById(forumOperationLog.getOperatorUserId());
        if (forumUser != null) {
            operationLogVO.setOperatorUserName(forumUser.getUserName());
        }
        return operationLogVO;
    }
}
