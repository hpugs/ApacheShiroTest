package com.hpugs.shiro.test.aop;

import com.hpugs.base.common.exception.BizException;
import com.hpugs.base.common.result.EnumError;
import com.hpugs.base.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * controller层全局异常处理器
 */

@ControllerAdvice
@Scope
@Slf4j
public class ControllerException {

    @ExceptionHandler(value = {UnauthorizedException.class, AuthorizationException.class})
    @ResponseBody
    public Result<?> handleException(AuthorizationException e) {
        return Result.buildFail(EnumError.HAS_NO_POWER);
    }

    /**
     * Spring 参数校验框架统一错误返回
     *
     * @param e Exception 参数校验框架异常
     */
    @ExceptionHandler(value = BizException.class)
    @ResponseBody
    public Result<?> handleException(BizException e) {
        log.error("捕获>>>>>> BizException=", e);
        return Result.buildFail(e.getMessage());
    }

    /**
     * Spring 参数校验框架统一错误返回
     *
     * @param e IllegalArgumentException 参数校验框架异常
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseBody
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("捕获>>>>>> IllegalArgumentException=", e);
        return Result.buildFail(e.getMessage());
    }

    /**
     * Spring 参数校验框架统一错误返回
     *
     * @param e Exception 参数校验框架异常
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result<?> handleException(Exception e) {
        log.error("捕获>>>>>> Exception=", e);
        return Result.buildFail(EnumError.DEFAULT_ERROR);
    }


}
