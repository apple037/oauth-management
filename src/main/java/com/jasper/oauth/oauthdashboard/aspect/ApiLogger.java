package com.jasper.oauth.oauthdashboard.aspect;

import java.lang.annotation.Annotation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

// 紀錄Controller API的呼叫
@Aspect
@Slf4j
@Component
public class ApiLogger {
  @Pointcut("execution(* com.jasper.oauth.oauthdashboard.controller..*.*(..))")
  public void apiLogger() {}

  @Before("apiLogger()")
  public void before(JoinPoint joinPoint) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String methodName = signature.getMethod().getName();
    String className = joinPoint.getTarget().getClass().getName();
    log.info("[ApiLogger] {}#{}", className, methodName);

    // 紀錄呼叫的參數
    Object[] args = joinPoint.getArgs();
    RequestMethod requestMethod = getRequestType(joinPoint);
    log.info("[ApiLogger] RequestMethod: {}", requestMethod);
    if (requestMethod == null) {
      log.debug("[ApiLogger] Request method not found");
      return;
    }
    requestLog(args, requestMethod);
  }

  private RequestMethod getRequestType(JoinPoint joinPoint) {
    Annotation[] annotations =
        ((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature())
            .getMethod()
            .getAnnotations();

    for (Annotation annotation : annotations) {
      if (annotation instanceof RequestMapping requestMapping) {
        return requestMapping.method()[0];
      }
      if (annotation instanceof GetMapping) {
        return RequestMethod.GET;
      } else if (annotation instanceof PostMapping) {
        return RequestMethod.POST;
      } else if (annotation instanceof PutMapping) {
        return RequestMethod.PUT;
      } else if (annotation instanceof DeleteMapping) {
        return RequestMethod.DELETE;
      }
    }
    return null;
  }

  private void requestLog(Object[] args, RequestMethod requestMethod) {
    StringBuilder logMessage =
        new StringBuilder("[ApiLogger][" + requestMethod.name() + "][Parameters] ");
    for (Object arg : args) {
      logMessage.append(arg.toString()).append(", ");
    }
    // remove last comma
    if (!logMessage.isEmpty()) logMessage.deleteCharAt(logMessage.length() - 2);

    log.debug(logMessage.toString());
  }
}
