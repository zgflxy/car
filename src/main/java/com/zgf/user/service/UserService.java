package com.zgf.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zgf.user.entity.Result;
import com.zgf.user.entity.User;

/**
 * (User)表服务接口
 *
 * @author makejava
 * @since 2024-05-29 09:58:49
 */
public interface UserService extends IService<User> {

    Result login(String username, String password);

    Result<Boolean> insert(User user);

    Result enroll(String username, String password, String captcha);

    Result captcha();
}

