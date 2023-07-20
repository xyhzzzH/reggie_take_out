package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    /**
     * 查询订单分页
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @RequestMapping("/page")
    public R<Page> page(int page, int pageSize, String number,String beginTime,String endTime){
        Page<Orders> ordersPage = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.eq(StringUtils.isNotEmpty(number),Orders::getNumber,number);
        if (beginTime != null && endTime != null){
            ordersLambdaQueryWrapper.between(Orders::getOrderTime,beginTime,endTime);
        }
        ordersLambdaQueryWrapper.orderByDesc(Orders::getOrderTime);
        ordersService.page(ordersPage,ordersLambdaQueryWrapper);
        return R.success(ordersPage);
    }
    @RequestMapping("/userPage")
    public R<Page> userPage(int page, int pageSize){
        Page<Orders> ordersPage = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        ordersLambdaQueryWrapper.orderByDesc(Orders::getOrderTime);
        ordersService.page(ordersPage,ordersLambdaQueryWrapper);
        return R.success(ordersPage);
    }


    /**
     * 用户下单
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){

        ordersService.submit(orders);
        return R.success("下单成功");

    }

}
