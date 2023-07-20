package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithSetmealDish(SetmealDto setmealDto);
    public SetmealDto getByIdWithSetmealDish(Long id);
    public void updateWithSetmealDish(SetmealDto setmealDto);
    public void removeWithSetmealDish(List<Long> ids);
}
