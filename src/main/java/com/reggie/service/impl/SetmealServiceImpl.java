package com.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.dto.SetmealDto;
import com.reggie.entity.Setmeal;
import com.reggie.entity.SetmealDish;
import com.reggie.mapper.SetmealMapper;
import com.reggie.service.SetmealDishService;
import com.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 新增套餐同时保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Transactional//开启事务注解(要么两张表全成功，要么全失败)
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息 操作Setmeal 执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //遍历插入id信息(因为执行保存操作后，数据库中的信息会填充上原本为空的属性)
        setmealDishes.stream().map((iterm)->{
            iterm.setSetmealId(setmealDto.getId());
            return iterm;
        }).collect(Collectors.toList());
        //保存菜品的关联信息，操作setmeal_dish 执行insert操作
        setmealDishService.saveBatch(setmealDishes);


    }
}
