package com.zgf.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zgf.user.dao.UserDao;
import com.zgf.user.entity.Result;
import com.zgf.user.entity.User;
import com.zgf.user.service.UserService;
import com.zgf.user.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;
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
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Result login(String userName, String password) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries("UserName:"+userName);
        if (!entries.isEmpty() && entries.size()>0){
            String password1 = entries.get("password").toString();
            if (!StringUtils.isEmpty(password1)) {
                String verify = TokenUtil.verify(password1);
                if (!StringUtils.isEmpty(verify) && verify.equals(password)){
                    return Result.success(entries.get("userId").toString());
                }
            }
        }
        else {
            User user = new User();
            user.setUserName(userName);
            User user1 = this.getOne(new QueryWrapper<>(user));
            if (!StringUtils.isEmpty(user1)){
                if (user1.getPassword().equals(TokenUtil.sign(user1.getUserName(),password))){
                    redisTemplate.opsForHash().put("UserName:"+userName,"userId",user1.getUserId());
                    redisTemplate.opsForHash().put("UserName:"+userName,"userName",user1.getUserName());
                    redisTemplate.opsForHash().put("UserName:"+userName,"password",TokenUtil.sign(user1.getUserName(),password));
                    return Result.success(user1.getUserId());
                }
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
        // todo 通知redis更新缓存
        String userId = user.getUserId();
        Map<String, String> map = new HashMap<>();
        Map<String, String> map1 = new HashMap<>();
        map.put("userId",userId);
        map1.put("userId",userId);
        map.put("userName",user.getUserName());
        map1.put("userName",user.getUserName());
        map.put("password",user.getPassword());
        map1.put("password",user.getPassword());
        map.put("address",user.getAddress());
        map.put("age",user.getAge().toString());
        map.put("email",user.getEmail());
        map.put("idCard",user.getIdCard());
        map.put("phoneNumber",user.getPhoneNumber());
        map.put("sex",user.getSex());
        map.put("deleted",user.getDeleted());
        map.put("avatar", Arrays.toString(user.getAvatar()));
        redisTemplate.opsForHash().putAll("UserName:"+user.getUserName(),map1);
        redisTemplate.opsForHash().putAll("User:"+userId,map);
        redisTemplate.opsForZSet().add("UserId",userId,Integer.valueOf(userId));
        return Result.success(this.save(user));
    }

    @Override
    public Result enroll(String username, String password, String captcha) {
        if (!StringUtils.isEmpty(redisTemplate.opsForValue().get("captcha"))
                && redisTemplate.opsForValue().get("captcha").equals(captcha)){
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
        redisTemplate.opsForValue().set("captcha", captcha, 60, TimeUnit.SECONDS);
        return Result.success(captcha);
    }

    @Override
    public Result selectPage(int pages) {
//        Page<User> page = new Page<>(pages, 10);
//        Page<User> page1 = this.page(page, new QueryWrapper<>());
//        for (User user : page1.getRecords()) {
//            String userId = user.getUserId();
//            Map<String, String> map = new HashMap<>();
//            map.put("userId",userId);
//            map.put("userName",user.getUserName());
//            map.put("password",user.getPassword());
//            map.put("address",user.getAddress());
//            map.put("age",user.getAge().toString());
//            map.put("email",user.getEmail());
//            map.put("idCard",user.getIdCard());
//            map.put("phoneNumber",user.getPhoneNumber());
//            map.put("sex",user.getSex());
//            map.put("deleted",user.getDeleted());
//            map.put("avatar", Arrays.toString(user.getAvatar()));
//            redisTemplate.opsForHash().putAll("UserName:"+user.getUserName(),map);
//            redisTemplate.opsForHash().putAll("User:"+user.getUserId,map);
//            redisTemplate.opsForZSet().add("UserId",userId,Integer.valueOf(userId));
//        }

        // 计算分页参数
        Long start = (long) ((pages - 1) * 10);
        Long end = start + 10 - 1; // 注意：Redis的区间是左闭右开，所以不需要+1

        // 执行查询
        Set<Object> range = redisTemplate.opsForZSet().range("UserId", start, end);
        Map<String, Map> map = new HashMap<>();
        for (Object o : range) {
            String userId = o.toString();
            Map<Object, Object> entries = redisTemplate.opsForHash().entries("User:" + userId);
            map.put(userId, entries);
        }
//        return Result.success(this.page(page, new QueryWrapper<>()));
        return Result.success(map);
    }

    @Override
    public Result selectOne(Serializable id) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries("User:" + id);
        if (!StringUtils.isEmpty(entries) && entries.size()>0){
            return Result.success(entries);
        }
        else {
            User user = this.getById(id);
            if (!StringUtils.isEmpty(user)){
                Map<String, String> map = new HashMap<>();
                map.put("userId",user.getUserId());
                map.put("userName",user.getUserName());
                map.put("password",user.getPassword());
                map.put("address",user.getAddress());
                map.put("age",user.getAge().toString());
                map.put("email",user.getEmail());
                map.put("idCard",user.getIdCard());
                map.put("phoneNumber",user.getPhoneNumber());
                map.put("sex",user.getSex());
                map.put("deleted",user.getDeleted());
                map.put("avatar", Arrays.toString(user.getAvatar()));
                redisTemplate.opsForHash().putAll("User:"+user.getUserId(),map);
                return Result.success(user);
            }
        }
        return Result.error("用户不存在");
    }

}

