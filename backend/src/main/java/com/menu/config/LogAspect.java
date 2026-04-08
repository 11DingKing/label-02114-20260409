package com.menu.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.menu.entity.OperationLog;
import com.menu.mapper.OperationLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {
    
    private final OperationLogMapper operationLogMapper;
    private final ObjectMapper objectMapper;
    
    @Pointcut("execution(* com.menu.controller.*.*(..))")
    public void controllerPointcut() {}
    
    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        
        String className = point.getTarget().getClass().getSimpleName();
        String methodName = point.getSignature().getName();
        String fullMethod = className + "." + methodName;
        
        log.info("==> 请求开始: {} {}", request != null ? request.getMethod() : "", request != null ? request.getRequestURI() : "");
        log.info("==> 调用方法: {}", fullMethod);
        
        Object result;
        try {
            result = point.proceed();
            long costTime = System.currentTimeMillis() - startTime;
            log.info("<== 请求成功: {} 耗时: {}ms", fullMethod, costTime);
            
            // 记录操作日志（仅记录写操作）
            if (request != null && isWriteOperation(request.getMethod())) {
                saveOperationLog(request, fullMethod, point.getArgs(), costTime);
            }
            
            return result;
        } catch (Exception e) {
            long costTime = System.currentTimeMillis() - startTime;
            log.error("<== 请求失败: {} 耗时: {}ms 错误: {}", fullMethod, costTime, e.getMessage());
            throw e;
        }
    }
    
    private boolean isWriteOperation(String method) {
        return "POST".equalsIgnoreCase(method) || 
               "PUT".equalsIgnoreCase(method) || 
               "DELETE".equalsIgnoreCase(method);
    }
    
    private void saveOperationLog(HttpServletRequest request, String method, Object[] args, long costTime) {
        try {
            OperationLog operationLog = new OperationLog();
            
            Object userId = request.getAttribute("userId");
            Object username = request.getAttribute("username");
            
            operationLog.setUserId(userId != null ? (Long) userId : null);
            operationLog.setUsername(username != null ? (String) username : null);
            operationLog.setOperation(getOperationType(request.getMethod()));
            operationLog.setMethod(method);
            operationLog.setParams(args.length > 0 ? objectMapper.writeValueAsString(args[0]) : null);
            operationLog.setIp(getClientIp(request));
            operationLog.setCostTime(costTime);
            
            operationLogMapper.insert(operationLog);
            log.debug("操作日志已记录: {}", method);
        } catch (Exception e) {
            log.warn("记录操作日志失败: {}", e.getMessage());
        }
    }
    
    private String getOperationType(String method) {
        return switch (method.toUpperCase()) {
            case "POST" -> "新增";
            case "PUT" -> "修改";
            case "DELETE" -> "删除";
            default -> "其他";
        };
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
