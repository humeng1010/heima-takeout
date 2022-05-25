package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.dto.DishDto;
import com.reggie.entity.Category;
import com.reggie.entity.Dish;
import com.reggie.service.CategoryService;
import com.reggie.service.DishFlavorService;
import com.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，由于前端传给我们的数据是json形式的，我们需要给这就接口传递参数，接收前端数据，
     * 但是我们发现，我们的Dish类中没有flavors属性，则不能自动一一对应接收数据，所以我们需要自定义一个类
     * 接收前端数据：DishDto
     * DTO:全称Data Transfer Object, 即数据传输对象，一般用于展示层与服务层之间的数据传输
     *      因为前端传给我们的数据和实体类中的属性不一致，所以需要使用DTO
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }


    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器
        //Dish菜品分页构造器，用于封装菜品单表的基础信息
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        //DishDto分页构造器，用于封装前端需要展示的所有（和上面的区别就是多了个categoryName数据）
        //  用于展示菜品的分类信息
        Page<DishDto> dishDtoPage = new Page<>();

        //添加菜品查询条件
        //查询条件构造器
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件（模糊查询）当name不为null时，执行这个查询
        dishLambdaQueryWrapper.like(name!=null,Dish::getName,name);
        //添加排序条件
        dishLambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);

        //调用service方法进行查询
        dishService.page(pageInfo, dishLambdaQueryWrapper);

        //把得到的菜品分页对象 除去 records (详细记录，就是菜品的具体信息，注意这里是不要拷贝这个信息的，因为这个信息和我们前端需要的信息中有区别，缺少categoryName...)
        //其他的分页信息（比如 total page pageSize ...)拷贝到DTO中
        //records我们单独取出进行操作之后再进行赋值到DTO
        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        //records我们单独取出进行操作之后再进行赋值到DTO
        //获取分页信息中的具体内容记录
        List<Dish> records = pageInfo.getRecords();
        //通过stream流的方式进行操作：遍历records中的每一个对象（就是数据库中每一行的k-v）
        List<DishDto> list = records.stream().map((item) -> {
            //我们在这个里面new一个DishDto对象
            DishDto dishDto = new DishDto();
            //把基本的Dish拷贝到DishDto(plus版）中
            BeanUtils.copyProperties(item,dishDto);//拷贝普通属性

            //我们获取到每一个Dish的分类Id
            Long categoryId = item.getCategoryId();//分类ID
            //调用categoryService，根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            //如果category不为空，我们就获取分类对象的名称，并且让dishDto中的categoryName等于分类对象的name
            if (!Objects.isNull(category)){
                //根据对象获取对象名称
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            //最后我们map返回dishDto对象
            return dishDto;
        }).collect(Collectors.toList());//在封装为集合（且类型为DishDto），赋给list

        //我们再给dishDtoPage设置上 记录
        dishDtoPage.setRecords(list);

        //最后返回dishDtoPage
        return R.success(dishDtoPage);

    }

    /**
     * 根据id查询菜品信息 和 对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto>  getById(@PathVariable Long id){
        //扩展自定义查询方法
        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.updateWithFlavor(dishDto);

        return R.success("修改菜品成功");
    }


    /**
     * 根据id删除菜品
     * @param ids
     * @return
     */
//    @DeleteMapping
    public R<String> deleteById(Long ids){
        log.info("需要删除的id为{}",ids);
        dishService.removeById(ids);

        return R.success("删除成功");
    }

    /**
     * 根据id批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteByIds(Long[] ids){
        log.info("需要删除的id为{}", Arrays.toString(ids));
//        dishService.removeById(ids);
        dishService.removeByIds(Arrays.asList(ids));
        return R.success("删除成功");
    }

    /**
     * 停售and批量停售
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public R<String> stop(Long[] ids){
        log.info(Arrays.toString(ids));
//        Dish dish = dishService.getById(ids);
//        Integer status = dish.getStatus();
        for (Long id : ids) {
            Dish dish = dishService.getById(id);
            dish.setStatus(0);
            dishService.updateById(dish);
        }
        return R.success("停售成功");
    }

    /**
     * 启售and批量启售
     * @param ids
     * @return
     */
    @PostMapping("/status/1")
    public R<String> turnOn(Long[] ids){
        log.info(Arrays.toString(ids));
//        Dish dish = dishService.getById(ids);
        for (Long id : ids) {
            Dish dish = dishService.getById(id);
            dish.setStatus(1);
            dishService.updateById(dish);
        }
        return R.success("启售成功");
    }


    /**
     * 根据条件查询菜品数据
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        //构造查询条件对象
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());

        //查询状态为1的(启售)的菜品
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        //排序条件
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(lambdaQueryWrapper);

        return R.success(list);
    }
}

