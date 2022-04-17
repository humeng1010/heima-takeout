package com.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 公共字段自动填充
 * 自定义元数据对象处理器
 */
@Component//加入到spring容器中
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 插入字段自动填充
     * 当前端提交过来例如保存请求的时候
     * 在执行sql前会来到这个类中，对字段进行填充
     * 参数：metaObject:元数据，封装了数据对象（employee对象）
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]...");
        log.info(metaObject.toString());
        //设置自动填充值
            //创建更新时间
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());

        //创建更新人，这个时候我们在这个类中获取不到session
        //因为MyMetaObjectHandler类中是不能获得HttpServletRequest中的HttpSession对象的（它不是HttpServlet的子类，而是MP的类）
        //使用ThreadLocal在该线程中存储登陆id作为副本，使用BaseContext工具类进行封装
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }

    /**
     * 更新字段自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update]...");
        log.info(metaObject.toString());
        //验证一个请求（编辑功能）是否是同一个线程
//        long id = Thread.currentThread().getId();
//        log.info("当前线程id为{}",id);
        //end
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());

    }
}
