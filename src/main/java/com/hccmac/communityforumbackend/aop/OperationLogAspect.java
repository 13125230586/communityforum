package com.hccmac.communityforumbackend.aop;

import com.alibaba.fastjson.JSON;
import com.hccmac.communityforumbackend.annotation.OperationLog;
import com.hccmac.communityforumbackend.common.BaseResponse;
import com.hccmac.communityforumbackend.common.ErrorCode;
import com.hccmac.communityforumbackend.constant.OperationLogConstant;
import com.hccmac.communityforumbackend.exception.BusinessException;
import com.hccmac.communityforumbackend.module.system.entity.ForumOperationLog;
import com.hccmac.communityforumbackend.module.system.service.OperationLogService;
import com.hccmac.communityforumbackend.module.user.entity.ForumUser;
import com.hccmac.communityforumbackend.module.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 操作日志切面
 */
@Aspect
@Component
@Slf4j
public class OperationLogAspect {

    @Resource
    private HttpServletRequest request;

    @Resource
    private UserService userService;

    @Resource
    private OperationLogService operationLogService;

    @Around("@annotation(operationLog)")
    public Object doOperationLog(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
        ForumUser loginUser = null;
        try {
            loginUser = userService.getLoginUserEntity(request);
        } catch (Exception e) {
            log.warn("操作日志取用户失败 path:{}", request.getRequestURI());
        }
        ForumOperationLog forumOperationLog = buildOperationLog(joinPoint, operationLog, loginUser);
        try {
            Object result = joinPoint.proceed();
            fillSuccessResult(forumOperationLog, result);
            fillBizId(forumOperationLog, joinPoint, result);
            operationLogService.saveOperationLog(forumOperationLog);
            return result;
        } catch (BusinessException e) {
            fillErrorResult(forumOperationLog, e.getCode(), e.getMessage());
            fillBizId(forumOperationLog, joinPoint, null);
            operationLogService.saveOperationLog(forumOperationLog);
            throw e;
        } catch (Throwable e) {
            fillErrorResult(forumOperationLog, ErrorCode.SYSTEM_ERROR.getCode(), e.getMessage());
            fillBizId(forumOperationLog, joinPoint, null);
            operationLogService.saveOperationLog(forumOperationLog);
            throw e;
        }
    }

    private ForumOperationLog buildOperationLog(ProceedingJoinPoint joinPoint, OperationLog operationLog, ForumUser loginUser) {
        ForumOperationLog forumOperationLog = new ForumOperationLog();
        if (loginUser != null) {
            forumOperationLog.setOperatorUserId(loginUser.getId());
            forumOperationLog.setOperatorRole(loginUser.getUserRole());
        } else {
            forumOperationLog.setOperatorUserId(0L);
            forumOperationLog.setOperatorRole("unknown");
        }
        forumOperationLog.setBizType(operationLog.bizType());
        forumOperationLog.setActionType(operationLog.actionType());
        forumOperationLog.setRequestPath(request.getRequestURI());
        forumOperationLog.setRequestParam(buildRequestParam(joinPoint));
        forumOperationLog.setOperateIp(request.getRemoteAddr());
        return forumOperationLog;
    }

    private void fillSuccessResult(ForumOperationLog forumOperationLog, Object result) {
        int resultCode = OperationLogConstant.SUCCESS_CODE;
        String resultMessage = ErrorCode.SUCCESS.getMessage();
        if (result instanceof BaseResponse) {
            BaseResponse<?> baseResponse = (BaseResponse<?>) result;
            resultCode = baseResponse.getCode();
            resultMessage = baseResponse.getMessage();
        }
        forumOperationLog.setResultCode(resultCode);
        forumOperationLog.setResultMessage(substring(resultMessage, OperationLogConstant.MESSAGE_MAX_LENGTH));
    }

    private void fillErrorResult(ForumOperationLog forumOperationLog, Integer resultCode, String resultMessage) {
        forumOperationLog.setResultCode(resultCode);
        forumOperationLog.setResultMessage(substring(resultMessage, OperationLogConstant.MESSAGE_MAX_LENGTH));
    }

    private void fillBizId(ForumOperationLog forumOperationLog, ProceedingJoinPoint joinPoint, Object result) {
        Long bizId = extractBizIdFromArgs(joinPoint.getArgs());
        if (bizId == null && result instanceof BaseResponse) {
            Object data = ((BaseResponse<?>) result).getData();
            if (data instanceof Long) {
                bizId = (Long) data;
            }
        }
        forumOperationLog.setBizId(bizId);
    }

    private Long extractBizIdFromArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        for (Object arg : args) {
            if (arg == null) {
                continue;
            }
            if (arg instanceof Long) {
                return (Long) arg;
            }
            try {
                Method method = arg.getClass().getMethod("getId");
                Object value = method.invoke(arg);
                if (value instanceof Long) {
                    return (Long) value;
                }
            } catch (Exception ignored) {
                // do nothing
            }
        }
        return null;
    }

    private String buildRequestParam(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = methodSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        Map<String, Object> paramMap = new LinkedHashMap<>();
        if (parameterNames != null && args != null) {
            for (int i = 0; i < parameterNames.length && i < args.length; i++) {
                if (isSkipArg(args[i])) {
                    continue;
                }
                paramMap.put(parameterNames[i], args[i]);
            }
        }
        return substring(JSON.toJSONString(paramMap), OperationLogConstant.PARAM_MAX_LENGTH);
    }

    private boolean isSkipArg(Object arg) {
        if (arg == null) {
            return false;
        }
        String className = arg.getClass().getName();
        return className.startsWith("org.springframework.web.multipart")
            || className.startsWith("org.springframework.validation")
            || className.startsWith("javax.servlet")
            || className.startsWith("org.apache.catalina");
    }

    private String substring(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }
}
