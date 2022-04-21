package com.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器
 * 代理AOP
 */
//不管哪个类上加了这个RestController或者Controller注解，都会被我们这个类处理
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody//因为我们还要写一个方法，最终返回json数据的
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 异常处理方法：
     * 一旦controller抛出这种SQLIntegrityConstraintViolationException异常
     * 就会被这个方法处理
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException exception){
        log.error(exception.getMessage());
        //Duplicate entry 'zhangsan' for key 'employee.idx_username'
        //首先判断异常信息中是否有Duplicate entry关键字信息，因为出现异常不一定都是双重输入重复异常
        if (exception.getMessage().contains("Duplicate entry")){
            //这个时候肯定是用户名重复了
            //我们提取出重复的用户名和菜品套餐，使用空格分割
            String[] split = exception.getMessage().split(" ");
            String msg = split[2] + "已存在";
            //返回错误信息
            return R.error(msg);

        }

        return R.error("未知错误");
    }

    /**
     * 异常处理方法：自定义异常，删除分类判断是否有菜品或者套餐
     * @param ex
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        log.error(ex.getMessage());

        return R.error(ex.getMessage());
    }

}
