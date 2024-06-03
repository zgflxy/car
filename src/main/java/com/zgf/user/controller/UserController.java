package com.zgf.user.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zgf.user.entity.Result;
import com.zgf.user.entity.User;
import com.zgf.user.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * (User)表控制层
 *
 * @author makejava
 * @since 2024-05-29 09:58:48
 */
@RestController
@RequestMapping("user")
public class UserController {
    /**
     * 服务对象
     */
    @Resource
    private UserService userService;

    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    @GetMapping("/login")
    public Result login(@RequestParam String username, @RequestParam String password){
        return userService.login(username, password);
    }

    /**
     * 注册
     * @param username
     * @param password
     * @param captcha
     * @return
     */
    @PostMapping("/enroll")
    public Result enroll(@RequestParam String username, @RequestParam String password, @RequestParam String captcha) {
        return userService.enroll(username, password, captcha);
    }

    /**
     * 获取验证码
     * @return
     */
    @GetMapping("/captcha")
    public Result captcha(){
        return userService.captcha();
    }

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param user 查询实体
     * @return 所有数据
     */
    @GetMapping
    public Result selectAll(Page<User> page, User user) {
        return Result.success(this.userService.page(page, new QueryWrapper<>()));
    }

    /**
     * 分页查询数据
     *
     * @param pages 页数
     * @return 所有数据
     */
    @GetMapping("/pages/{pages}")
    public Result selectPage(@PathVariable int pages) {
        Page<User> page = new Page<>(pages, 10);
        return Result.success(this.userService.page(page, new QueryWrapper<>()));
    }

    /**
     *  通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public Result<User> selectOne(@PathVariable Serializable id) {
        return Result.success(this.userService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param user 实体对象
     * @return 新增结果
     */
    @PostMapping
    public Result<Boolean> insert(@RequestBody User user) {
        return userService.insert(user);
    }

    /**
     * 修改数据
     *
     * @param user 实体对象
     * @return 修改结果
     */
    @PutMapping
    public Result<Boolean> update(@RequestBody User user) {
        return Result.success(this.userService.updateById(user));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public Result<Boolean> delete(@RequestParam("idList") List<Long> idList) {
        if (idList.size() == 0){
            return Result.error("请选择要删除的数据");
        }
        return Result.success(this.userService.removeByIds(idList));
    }
}

