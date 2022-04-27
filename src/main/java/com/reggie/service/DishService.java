package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.dto.DishDto;
import com.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

    //新增菜品，同时添加菜品对应的口味数据(多表操作:dish,dish_flavor)
    void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和对应的口味信息
    DishDto getByIdWithFlavor(Long id);

    //更新菜品信息，同时还要更新口味信息
    void updateWithFlavor(DishDto dishDto);
}
