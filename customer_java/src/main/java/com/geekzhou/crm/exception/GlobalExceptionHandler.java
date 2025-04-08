package com.geekzhou.crm.exception;

import cn.hutool.log.StaticLog;
import com.geekzhou.crm.common.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice("com.geekzhou.crm.controller")
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    @ResponseBody // 返回json串
    public Result exceptionHandler(Exception e) {
        // 引入日志打印异常信息
        StaticLog.error("500 系统异常：error log: {}", e.getMessage());
//        e.printStackTrace();
        return Result.error();
    }

    @ExceptionHandler(value = CustomException.class)
    @ResponseBody // 返回json串
    public Result exceptionHandler(CustomException e) {
        // TODO:引入日志打印异常信息
        StaticLog.error("自定义异常：error log: {}", e.getMsg());
        return Result.error(e.getCode(), e.getMsg());
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result handleSQLException(SQLIntegrityConstraintViolationException e) {
        StaticLog.error("500 数据库异常：error log: {}", e.getMessage());
        // 提取数据库错误信息
        if (e.getMessage().contains("idx_employee_no")) {
            return Result.error();
        }
        return Result.error();
    }
}
