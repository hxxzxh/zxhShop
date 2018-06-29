package com.zxhShop.sellergoods.service;

import com.zxhShop.pojo.TbSeller;
import com.zxhShop.service.BaseService;
import com.zxhShop.vo.PageResult;

public interface SellerService extends BaseService<TbSeller> {

    PageResult search(Integer page, Integer rows, TbSeller seller);
}