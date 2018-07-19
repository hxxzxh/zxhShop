package com.zxhshop.user.service;

import com.zxhshop.pojo.TbAddress;
import com.zxhshop.service.BaseService;
import com.zxhshop.vo.PageResult;

public interface AddressService extends BaseService<TbAddress> {

    PageResult search(Integer page, Integer rows, TbAddress address);
}