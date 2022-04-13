package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reggie.common.R;
import com.reggie.entity.Employee;
import com.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登陆：
     * 参数一：登陆成功后，需要把Employee员工对象存入到session，表示登陆成功
     * 参数二：接收前端页面的登陆数据，post请求，请求数据为json类型的
     * 在登陆参数中添加一个@RequestBody注解,以根据请求的内容类型解析方法参数
     * 传的json中有一个key是username 还有一个是password,与Employee实体类中的属性命名必须一样
     * 否则无法封装成功
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request,
                             @RequestBody Employee employee){
        /*
         * 处理逻辑：
         * 1.首先对用户输入的密码进行md5加密（因为数据库中的密码是md5加密后的）
         * 2.根据用户名查询数据库（没有用户，返回msg失败结果）
         * 3.比对密码是否一致
         * 4.查看员工状态是否禁用
         * 5.将员工id放入到session中，返回成功的结果
         */

        //1.密码进行md5加密：
        //  使用DigestUtils工具类中的md5DigestAsHex方法
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2.根据用户名查数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //第一个参数是数据库中的数据，第二个参数是前端页面传入的数据
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        //调用employeeService的getOne方法，因为数据库中对用户名添加了一个唯一约束
        Employee emp = employeeService.getOne(queryWrapper);
        //3.如果没有查到则返回登陆失败结果
        if (Objects.isNull(emp)){
            return R.error("登陆失败");
        }
        //4.比对密码
        if (!emp.getPassword().equals(password)){
            return R.error("密码错误");
        }
        //5.查看员工状态
        if (emp.getStatus() == 0){
            return R.error("该账户已禁用");
        }
        //6.登陆成功，将员工的id存入到Session中并返回登陆成功的结果
        HttpSession session = request.getSession();
        session.setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     * 1.清理session中保存的当前登陆员工的id
     * 2.返回结果（退出成功）
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功！");
    }

}
