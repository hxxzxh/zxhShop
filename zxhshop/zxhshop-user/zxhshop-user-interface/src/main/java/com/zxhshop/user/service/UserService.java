package com.zxhshop.user.service;

import com.zxhshop.pojo.TbUser;
import com.zxhshop.service.BaseService;
import com.zxhshop.vo.PageResult;

public interface UserService extends BaseService<TbUser> {

    PageResult search(Integer page, Integer rows, TbUser user);

    /**
     * 发送短信验证码
     * @param phone 手机号
     */
    void sendSmsCode(String phone);

    /**
     * 校验验证码
     * @param phone 手机号
     * @param smsCode 验证码
     * @return true or false
     */
    boolean checkSmsCode(String phone, String smsCode);
}