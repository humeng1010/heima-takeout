package com.reggie.controller;

import com.reggie.common.R;
import com.reggie.dto.DishDto;
import com.reggie.service.DishFlavorService;
import com.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

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

}

