package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 获取当前用户地址列表
     * @param session
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> getList(HttpSession session) {
        Long userId = (Long)session.getAttribute("user");
        LambdaQueryWrapper<AddressBook> addressBookLambdaQueryWrapper = new LambdaQueryWrapper<>();
        addressBookLambdaQueryWrapper.eq(AddressBook::getUserId,userId);
        addressBookLambdaQueryWrapper.orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> list = addressBookService.list(addressBookLambdaQueryWrapper);
        return R.success(list);
    }

    /**
     * 添加地址
     * @param addressBook
     * @param session
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook,HttpSession session){
        Long userId = (Long)session.getAttribute("user");
        addressBook.setIsDefault(0);
        addressBook.setUserId(userId);
        addressBookService.save(addressBook);
        return R.success("新增地址成功");
    }


    /**
     * 设为默认地址
     * @param map
     * @return
     */
    @Transactional
    @PutMapping("/default")
    public R<String> setDefault(@RequestBody AddressBook addressBook){
        // 把存在的默认id设为不默认
        LambdaUpdateWrapper<AddressBook> addressBookLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        addressBookLambdaUpdateWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        addressBookLambdaUpdateWrapper.eq(AddressBook::getIsDefault,1);
        addressBookLambdaUpdateWrapper.set(AddressBook::getIsDefault,0);
        addressBookService.update(addressBookLambdaUpdateWrapper);
        // 把当前id设为默认
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success("设为默认成功");
    }

    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        LambdaQueryWrapper<AddressBook> addressBookLambdaQueryWrapper = new LambdaQueryWrapper<>();
        addressBookLambdaQueryWrapper.eq(AddressBook::getIsDefault,1);
        addressBookLambdaQueryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        AddressBook one = addressBookService.getOne(addressBookLambdaQueryWrapper);
        return R.success(one);
    }


    /**
     * 根据id获取地址
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> getOne(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        return R.success(addressBook);
    }

    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook){
        addressBookService.updateById(addressBook);
        return R.success("编辑地址成功");
    }

    @DeleteMapping
    public R<String> delete(Long ids) {
        addressBookService.removeById(ids);
        return R.success("删除地址成功");
    }


}
