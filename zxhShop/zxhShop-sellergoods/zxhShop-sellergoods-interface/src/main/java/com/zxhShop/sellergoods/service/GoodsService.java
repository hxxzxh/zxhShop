package com.zxhShop.sellergoods.service;

import com.zxhShop.pojo.TbGoods;
import com.zxhShop.service.BaseService;
import com.zxhShop.vo.PageResult;

public interface GoodsService extends BaseService<TbGoods> {

    PageResult search(Integer page, Integer rows, TbGoods goods);
}