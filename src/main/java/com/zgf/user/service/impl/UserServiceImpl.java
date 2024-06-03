package com.zgf.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zgf.user.dao.UserDao;
import com.zgf.user.entity.Result;
import com.zgf.user.entity.User;
import com.zgf.user.service.UserService;
import com.zgf.user.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * (User)表服务实现类
 *
 * @author makejava
 * @since 2024-05-29 09:58:49
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result login(String username, String password) {
        User user = new User();
        user.setUserName(username);
        User user1 = this.getOne(new QueryWrapper<>(user));
        if (!StringUtils.isEmpty(user1)) {
            String verify = TokenUtil.verify(user1.getPassword());
            if (!StringUtils.isEmpty(verify) && verify.equals(password)){
                return Result.success("登录成功");
            }
        }
        return Result.error("用户名或密码错误");
    }

    @Override
    public Result insert(User user) {
        User user2 = new User();
        user2.setUserName(user.getUserName());
        user2.setDeleted("0");
        if (!StringUtils.isEmpty(this.getOne(new QueryWrapper<>(user2)))){
            return Result.error("用户名已存在");
        }
        user.setDeleted("0");
        user.setPassword(TokenUtil.sign(user.getUserName(),user.getPassword()));
        return Result.success(this.save(user));
    }

    @Override
    public Result enroll(String username, String password, String captcha) {
        if (!StringUtils.isEmpty(stringRedisTemplate.opsForValue().get("captcha")) && stringRedisTemplate.opsForValue().get("captcha").equals(captcha)){
            User user = new User();
            user.setUserName(username);
            user.setPassword(password);
            return insert(user);
        }
        return Result.error("验证码错误");
    }

    public Result captcha() {
        Random random = new Random();
        String captcha = "";
        for (int i = 0; i < 6; i++) {
            captcha += random.nextInt(10);
        }
        stringRedisTemplate.opsForValue().set("captcha", captcha, 60, TimeUnit.SECONDS);
        return Result.success(captcha);
    }

}

