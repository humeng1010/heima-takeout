package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.dto.DishDto;
import com.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

    //新增菜品，同时添加菜品对应的口味数据(多表操作:dish,dish_flavor)
    void saveWithFlavor(DishDto dishDto);


}
