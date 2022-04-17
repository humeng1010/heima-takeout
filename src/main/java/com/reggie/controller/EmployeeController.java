package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.entity.Employee;
import com.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
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

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息{}",employee.toString());
        //id=null, username=zhangsan, name=张三, password=null,
        // phone=17358002861, sex=1, idNumber=111222333444555666,
        // status=null, createTime=null, updateTime=null,
        // createUser=null, updateUser=null
        //设置初始密码：123456，但是需要进行md5的加密处理
        //status不需要设置，因为数据库中设置了默认值 1 启用
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //============MP公共字段自动填充==============
        //这个地方我们使用MP的字段自动填充功能：首先在实体类需要自动填充的属性上
        // 添加@TableField(fill = FieldFill.INSERT/FieldFill.INSERT_UPDATE )
        //然后再common包下创建数据对象处理器MyMetaObjectHandler并且实现MetaObjectHandler接口
        //实现insertFill和updateFill方法，在方法中实现需要填充的内容，这样就可以实现每次自动填充公共字段了！！！

        //设置创建和修改时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        //设置创建该用户的人
            //获得当前登陆用户的id
//        HttpSession session = request.getSession();
//        Long empId = (Long) session.getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);
        //========MP公共字段自动填充===========

        //调用IService 把数据保存到数据库
        employeeService.save(employee);

        //定义全局异常控制器
//        try {
//            employeeService.save(employee);
//        } catch (Exception e) {
//            R.error("新增员工失败！");
//        }

        return R.success("新增员工成功！");
    }

    /**
     * 员工信息的分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);
        //分页构造器
        Page<Employee> pageInfo = new Page<>(page,pageSize);

        //条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件,使用like自带的判断条件，
        // 第一个参数：判断是否有内容，如果没有内容则不会执行like
        queryWrapper.like(StringUtils.hasText(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息（基本信息以及status状态信息）
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){
        log.info(employee.toString());//查看employee是否封装上了

        //验证一个请求（编辑功能）是否是同一个线程
        //long id = Thread.currentThread().getId();
        //log.info("当前线程id为{}",id);
        //end

        //使用MP的公共字段自动填充
        //在更新前设置一下更新人的信息
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));

        //进行更新
        employeeService.updateById(employee);

        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查询员工信息
     * 使用路径变量获取id
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息，获取到的id为{}",id);
        Employee employee = employeeService.getById(id);
        //如果查出来的employee是空，返回错误信息
        if (Objects.isNull(employee)){
            return R.error("没有查询到对应的一个信息");
        }
        return R.success(employee);
    }

}
