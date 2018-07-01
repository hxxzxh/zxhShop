package com.zxhshop.sellergoods.service;

import com.zxhshop.pojo.TbGoods;
import com.zxhshop.service.BaseService;
import com.zxhshop.vo.Goods;
import com.zxhshop.vo.PageResult;

public interface GoodsService extends BaseService<TbGoods> {

    PageResult search(Integer page, Integer rows, TbGoods goods);

    void add(Goods goods);
}