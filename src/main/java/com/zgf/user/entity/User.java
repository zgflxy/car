package com.zgf.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * (User)表实体类
 *
 * @author makejava
 * @since 2024-05-29 09:58:49
 */
@SuppressWarnings("serial")
public class User extends Model<User> {
    @TableId(value = "user_id", type = IdType.AUTO)
    //用户id
    private String userId;
    //用户名
    private String userName;
    //密码
    private String password;
    //性别
    private String sex;
    //年龄
    private Integer age;
    //联系电话
    private String phoneNumber;
    //邮箱
    private String email;
    //身份证
    private String idCard;
    //逻辑删除
    private String deleted;
    //头像
    private Byte[] avatar;
    //地址
    private String address;

    public User(String userName) {
        this.userName = userName;
    }

    public User() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public Byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(Byte[] avatar) {
        this.avatar = avatar;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    public Serializable pkVal() {
        return this.userId;
    }
}

