package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("list")
    public R<List<ShoppingCart>> getList(){
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartLambdaQueryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list();

        return R.success(list);
    }

    /**
     * 添加菜品到购物车
     * @param shoppingCart
     * @param session
     * @return
     */
    @PostMapping("/add")
    public R<String> add(@RequestBody ShoppingCart shoppingCart, HttpSession session){
        // 如果购物车中不存在，则添加当前菜品，数量为1
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getName,shoppingCart.getName());
        ShoppingCart one = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);
        if (one == null){
            shoppingCart.setUserId(Long.parseLong(session.getAttribute("user").toString()));
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
        }else {
            Integer number = one.getNumber();
            one.setNumber(number + 1);
            // 如果购物车该菜品存在，则更新当前菜品 数量+1
            shoppingCartService.updateById(one);
        }

        return R.success("加入菜品成功");
    }

    /**
     * 从购物车移除菜品
     */
    @PostMapping("/sub")
     public R<String> sub(@RequestBody ShoppingCart shoppingCart){
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();

        if (shoppingCart.getDishId() == null){
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }else {
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }

        ShoppingCart one = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);
        if (one.getNumber() <= 1){
            shoppingCartService.removeById(one.getId());
        }else {
            one.setNumber(one.getNumber()-1);

            shoppingCartService.updateById(one);
        }

        return R.success("减少成功");
     }

     @DeleteMapping("/clean")
     public R<String> clean() {
         LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
         shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
         shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
         return R.success("清空购物车成功");
     }


}
