package com.zxhshop.sellergoods.service;

import com.zxhshop.pojo.TbSeller;
import com.zxhshop.service.BaseService;
import com.zxhshop.vo.PageResult;

public interface SellerService extends BaseService<TbSeller> {

    PageResult search(Integer page, Integer rows, TbSeller seller);
}