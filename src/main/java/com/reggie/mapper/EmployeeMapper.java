package com.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * 创建员工mapper接口，基于mybatisPlus简化开发，
 * 直接让接口继承 BaseMapper< T >，自动完成基本的数据库操作功能
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
